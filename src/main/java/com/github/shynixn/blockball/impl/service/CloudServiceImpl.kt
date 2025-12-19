package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.CloudService
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.fasterxml.jackson.databind.DeserializationFeature
import com.github.shynixn.fasterxml.jackson.databind.ObjectMapper
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.http.HttpClientFactory
import com.github.shynixn.mcutils.http.HttpClientSettings
import com.github.shynixn.mcutils.http.get
import com.github.shynixn.mcutils.http.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.logging.Level

class CloudServiceImpl(
    private val plugin: Plugin,
    private val httpClientFactory: HttpClientFactory,
    private val chatMessageService: ChatMessageService,
    private val language: BlockBallLanguage,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>
) : CloudService {
    private var trackingKey: String? = null
    private var serverId: String? = null
    private var loginProcess = HashSet<CommandSender>()
    private val emptyGuid = "00000000-0000-0000-0000-000000000000"

    /**
     * Performs the login flow.
     */
    override suspend fun performLoginFlow(sender: CommandSender) {
        loginProcess.add(sender)

        withContext(Dispatchers.IO) {
            plugin.logger.log(Level.INFO, "Starting login flow...")
            val authApiUrl = plugin.config.getString("cloud.authApiUrl")!!
            val authUrl = plugin.config.getString("cloud.authUrl")!!
            val baseUrl = plugin.config.getString("cloud.baseUrl")!!

            withContext(plugin.globalRegionDispatcher) {
                chatMessageService.sendLanguageMessage(sender, language.cloudLoginStart)
            }

            if (trackingKey == null) {
                fetchTrackingKey()
            }

            var refreshToken: String? = null
            httpClientFactory.createHttpClient(HttpClientSettings(authApiUrl)).use { httpClient ->
                val startResponse = httpClient.post<CloudSessionStart, String>(
                    "/api/v1/auth/session/start?client_id=sso-sporthub-client",
                    "",
                    hashMapOf("x-api-key" to trackingKey!!, "User-Agent" to plugin.name + "-Stats")
                )

                if (!startResponse.isSuccessStatusCode) {
                    throw IOException("Failed to retrieve deviceCode: ${startResponse.error}")
                }

                withContext(plugin.globalRegionDispatcher) {
                    chatMessageService.sendLanguageMessage(
                        sender,
                        language.cloudLoginOpenInBrowser
                    )
                    val url = authUrl + "/authorize?device_code=${startResponse.result!!.deviceCode}"
                    if (sender is Player) {
                        sender.sendMessage(ChatColor.GRAY.toString() + url)
                    } else {
                        sender.sendMessage(url)
                    }
                }

                for (i in 0 until 3 * 10) {
                    val sessionResponse =
                        httpClient.get<CloudCredentials, String>(
                            "/api/v1/auth/session/validate?client_id=sso-sporthub-client&device_code=${startResponse.result!!.deviceCode}",
                            hashMapOf("x-api-key" to trackingKey!!, "User-Agent" to plugin.name + "-Stats")
                        )

                    if (sessionResponse.isSuccessStatusCode) {
                        refreshToken = sessionResponse.result!!.refreshToken
                        break
                    }

                    delay(5000)
                    withContext(plugin.globalRegionDispatcher) {
                        chatMessageService.sendLanguageMessage(
                            sender,
                            language.cloudLoginWait
                        )
                    }

                    if (!loginProcess.contains(sender)) {
                        throw IOException("Login was cancelled!")
                    }
                }

                if (refreshToken == null) {
                    throw IOException("Timeout of login flow.")
                }
            }

            refreshTokens(refreshToken!!)
            loginProcess.remove(sender)
        }
    }

    /**
     * Performs logout.
     */
    override suspend fun performLogout(sender: CommandSender) {
        withContext(Dispatchers.IO) {
            playerDataRepository.delete(PlayerInformation().also {
                it.playerUUID = emptyGuid
            })
            loginProcess.remove(sender)
        }

        getCredentials()
    }

    /**
     * Publishes the game stats.
     */
    override suspend fun publishGameStats(cloudGame: CloudGame) {
        withContext(Dispatchers.IO) {
            if (serverId == null) {
                fetchServerId(true)
            }

            sendGameData(cloudGame, true)
        }
    }

    private suspend fun sendGameData(cloudGame: CloudGame, retry: Boolean) {
        val apiUrl = plugin.config.getString("cloud.apiUrl")!!
        val credentials = getCredentials()
        val headers = hashMapOf(
            "x-api-key" to credentials.apiKey,
            "User-Agent" to plugin.name + "-Stats",
            "Authorization" to "Bearer ${credentials.accessToken}",
            "content-type" to "application/json"
        )
        httpClientFactory.createHttpClient(HttpClientSettings(apiUrl)).use { httpClient ->
            val response = httpClient.post<String, String>(
                "/api/v1/game/${serverId}", cloudGame, headers
            )

            if (response.isSuccessStatusCode) {
                plugin.logger.log(Level.INFO, "Successfully published game.")
                return
            } else if (retry) {
                refreshTokens(credentials.refreshToken)
                return sendGameData(cloudGame, false)
            } else {
                throw IOException("HTTP ${response.statusCode}: Failed to send game - ${response.error}")
            }
        }
    }

    private suspend fun refreshTokens(refreshToken: String) {
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
            val refreshTokenResponse = httpClient.get<CloudCredentials, String>(
                "/api/v1/auth/authorize?client_id=sso-sporthub-client&redirect_uri=https://sso.shynixn.com", headers
            )

            if (!refreshTokenResponse.isSuccessStatusCode) {
                throw IOException("HTTP ${refreshTokenResponse.statusCode}: Failed to refresh tokens - ${refreshTokenResponse.error}")
            }

            val objectMapper = ObjectMapper()
            playerDataRepository.save(PlayerInformation().also {
                it.isPersisted = playerDataRepository.getByPlayerUUID(UUID.fromString(emptyGuid)) != null
                it.playerUUID = emptyGuid
                it.playerName = "Cloud"
                it.statsMeta = StatsMeta().also {
                    it.optional =
                        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(refreshTokenResponse.result!!)
                }
            })
        }
    }

    private suspend fun fetchServerId(retry: Boolean) {
        val apiUrl = plugin.config.getString("cloud.apiUrl")!!
        val credentials = getCredentials()
        val headers = hashMapOf(
            "x-api-key" to credentials.apiKey,
            "User-Agent" to plugin.name + "-Stats",
            "Authorization" to "Bearer ${credentials.accessToken}"
        )
        httpClientFactory.createHttpClient(HttpClientSettings(apiUrl)).use { httpClient ->
            val response = httpClient.get<CloudServerDataResponse, String>("/api/v1/server", headers)
            if (response.isSuccessStatusCode) {
                serverId = response.result!!.data.id
            } else if (retry) {
                refreshTokens(credentials.refreshToken)
                return fetchServerId(false)
            } else {
                throw IOException("HTTP ${response.statusCode}: Failed to fetch serverid - ${response.error}")
            }
        }
    }

    private suspend fun getCredentials(): CloudCredentials {
        val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val storageObject = playerDataRepository.getByPlayerUUID(UUID.fromString(emptyGuid))
            ?: throw IOException("You need to login first!")
        val cloudCredentials = objectMapper.readValue(storageObject.statsMeta.optional, CloudCredentials::class.java)
        val payload = Base64.getUrlDecoder().decode(cloudCredentials.accessToken.split(".")[1])
        cloudCredentials.apiKey =
            objectMapper.readValue(payload.toString(Charsets.UTF_8), CloudApiKeySet::class.java).apiKey
        return cloudCredentials
    }

    private fun fetchTrackingKey() {
        val baseUrl = plugin.config.getString("cloud.baseUrl")!!
        httpClientFactory.createHttpClient(HttpClientSettings(baseUrl)).use { httpClient ->
            val response = httpClient.get<CloudPublicKeys, String>("/publickeys.json")

            if (response.isSuccessStatusCode) {
                trackingKey = response.result!!.ssoTrackingKey
            } else {
            }
        }
    }
}