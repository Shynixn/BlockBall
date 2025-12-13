package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.CloudService
import com.github.shynixn.blockball.entity.CloudCredentials
import com.github.shynixn.blockball.entity.CloudPublicKeys
import com.github.shynixn.blockball.entity.CloudServerData
import com.github.shynixn.blockball.entity.CloudSessionStart
import com.github.shynixn.blockball.entity.StatsGame
import com.github.shynixn.fasterxml.jackson.databind.ObjectMapper
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.http.HttpClientFactory
import com.github.shynixn.mcutils.http.HttpClientSettings
import com.github.shynixn.mcutils.http.get
import com.github.shynixn.mcutils.http.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.logging.Level

class CloudServiceImpl(
    private val plugin: Plugin,
    private val httpClientFactory: HttpClientFactory,
    private val chatMessageService: ChatMessageService,
    private val language: BlockBallLanguage,
) : CloudService {
    private var trackingKey: String? = null
    private var serverId: String? = null

    /**
     * Performs the login flow.
     */
    override suspend fun performLoginFlow(sender: CommandSender) {
        withContext(Dispatchers.IO) {
            plugin.logger.log(Level.INFO, "Starting login flow...")

            val authApiUrl = plugin.config.getString("cloud.authApiUrl")!!
            val authUrl = plugin.config.getString("cloud.authUrl")!!
            val baseUrl = plugin.config.getString("cloud.baseUrl")!!

            withContext(plugin.globalRegionDispatcher) {
                chatMessageService.sendLanguageMessage(sender, language.cloudLoginStart, baseUrl)
            }

            if (trackingKey == null) {
                fetchTrackingKey()
            }

            // It's not a real api key. It is just usage tracking key ;)
            val headers = hashMapOf("x-api-key" to trackingKey!!, "User-Agent" to plugin.name + "-Stats")
            var refreshToken: String? = null
            httpClientFactory.createHttpClient(HttpClientSettings(authApiUrl)).use { httpClient ->
                val startResponse = httpClient.post<CloudSessionStart, String>(
                    "/api/v1/auth/session/start?client_id=sso-blockball-client", "", headers
                )

                if (!startResponse.isSuccessStatusCode) {
                    throw IOException("Failed to retrieve deviceCode: ${startResponse.error}")
                }

                withContext(plugin.globalRegionDispatcher) {
                    chatMessageService.sendLanguageMessage(
                        sender,
                        language.cloudLoginOpenInBrowser,
                        authUrl + "/authorize?device_code=${startResponse.result!!.deviceCode}"
                    )
                }

                for (i in 0 until 3 * 10) {
                    val sessionResponse =
                        httpClient.get<CloudCredentials, String>("/api/v1/auth/session/validate?client_id=sso-blockball-client")

                    if (sessionResponse.isSuccessStatusCode) {
                        refreshToken = sessionResponse.result!!.refreshToken
                        break
                    }

                    delay(20000)
                    withContext(plugin.globalRegionDispatcher) {
                        chatMessageService.sendLanguageMessage(
                            sender,
                            language.cloudLoginWait
                        )
                    }
                }

                if (refreshToken == null) {
                    throw IOException("Timeout of login flow.")
                }
            }

            refreshTokens(refreshToken!!)
        }
    }

    /**
     * Publishes the game stats.
     */
    override suspend fun publishGameStats(statsGame: StatsGame) {
        withContext(Dispatchers.IO) {
            val tokenFile = File(plugin.dataFolder, "tokens")
            if (!tokenFile.exists()) {
                throw IOException("You need to login first!")
            }

            if (serverId == null) {
                fetchServerId(true)
            }

            sendGameData(statsGame, true)
        }
    }

    private fun sendGameData(statsGame: StatsGame, retry: Boolean) {
        val apiUrl = plugin.config.getString("cloud.apiUrl")!!
        val credentials = getCredentials()
        val headers = hashMapOf(
            "x-api-key" to trackingKey!!,
            "User-Agent" to plugin.name + "-Stats",
            "Authorization" to "Bearer ${credentials.accessToken}"
        )
        httpClientFactory.createHttpClient(HttpClientSettings(apiUrl)).use { httpClient ->
            val response = httpClient.post<String, String>(
                "/api/v1/game/${serverId}", statsGame, headers
            )

            if (response.isSuccessStatusCode) {
                plugin.logger.log(Level.INFO, "Successfully published game.")
                return
            } else if (retry) {
                refreshTokens(credentials.refreshToken)
                return sendGameData(statsGame, false)
            } else {
                throw IOException("Failed to sendGameData")
            }
        }
    }

    private fun refreshTokens(refreshToken: String) {
        val authApiUrl = plugin.config.getString("cloud.authApiUrl")!!

        if (trackingKey == null) {
            fetchTrackingKey()
        }

        val headers = hashMapOf(
            "x-api-key" to trackingKey!!,
            "User-Agent" to plugin.name + "-Stats",
            "Authorization" to "Bearer $refreshToken"
        )
        httpClientFactory.createHttpClient(HttpClientSettings(authApiUrl)).use { httpClient ->
            val refreshTokenResponse = httpClient.post<String, String>(
                "/api/v1/auth/session/start?client_id=sso-blockball-client", "", headers
            )

            if (!refreshTokenResponse.isSuccessStatusCode) {
                throw IOException("Failed to refresh tokens.")
            }

            File(plugin.dataFolder, "tokens").writeText(refreshTokenResponse.result!!)
        }
    }

    private fun fetchServerId(retry: Boolean) {
        val apiUrl = plugin.config.getString("cloud.apiUrl")!!
        val credentials = getCredentials()
        val headers = hashMapOf(
            "x-api-key" to trackingKey!!,
            "User-Agent" to plugin.name + "-Stats",
            "Authorization" to "Bearer ${credentials.accessToken}"
        )
        httpClientFactory.createHttpClient(HttpClientSettings(apiUrl)).use { httpClient ->
            val response = httpClient.get<CloudServerData, String>("/api/v1/server", headers)
            if (response.isSuccessStatusCode) {
                serverId = response.result!!.id
            } else if (retry) {
                refreshTokens(credentials.refreshToken)
                return fetchServerId(false)
            } else {
                throw IOException("Failed to fetch serverId: ${response.error}")
            }
        }
    }

    private fun getCredentials(): CloudCredentials {
        val objectMapper = ObjectMapper()
        val rawCredentials = File(plugin.dataFolder, "tokens").readText()
        return objectMapper.readValue(rawCredentials, CloudCredentials::class.java)
    }

    private fun fetchTrackingKey() {
        val baseUrl = plugin.config.getString("cloud.baseUrl")!!
        httpClientFactory.createHttpClient(HttpClientSettings(baseUrl)).use { httpClient ->
            val response = httpClient.get<CloudPublicKeys, String>("/api/v1/publickeys.json")

            if (response.isSuccessStatusCode) {
                trackingKey = response.result!!.ssoTrackingKey
            } else {
                throw IOException("Failed to fetch publicKeys: ${response.error}")
            }
        }
    }
}