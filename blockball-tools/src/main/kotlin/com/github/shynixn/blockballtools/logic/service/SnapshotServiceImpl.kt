package com.github.shynixn.blockballtools.logic.service

import com.github.shynixn.blockballtools.contract.SonaTypeService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class SnapshotServiceImpl : SonaTypeService {
    /**
     * Searches the repository for the latest download link. Throws
     * a [IllegalArgumentException] if not found.
     */
    override fun findDownloadUrl(repository: String): String {
        val content = getSiteContent(repository)
        val data = content.split("SNAPSHOT")

        val lastSnapshotRepositoryPayload = data[data.size - 3]
        val lastSnapshotRepositoryURL = lastSnapshotRepositoryPayload.substring(lastSnapshotRepositoryPayload.indexOf("https://")) + "SNAPSHOT"

        val repositoryContent = getSiteContent(lastSnapshotRepositoryURL)
        val snapshotPayload = repositoryContent.split("href=\"")
        val subSnapshotPayload = snapshotPayload.filter { p -> p.contains(".jar\"") }

        val snapshotDownloadURLPayload = subSnapshotPayload[subSnapshotPayload.size - 1]

        return snapshotDownloadURLPayload.substring(0, snapshotDownloadURLPayload.indexOf("\">"))
    }

    /**
     * Searches the repository for the latest id. Throws
     * a [IllegalArgumentException] if not found.
     */
    override fun findId(repositor: String): String {
        val content = getSiteContent(repositor)
        val data = content.split("SNAPSHOT")

        val lastSnapshotRepositoryPayload = data[data.size - 3]
        val lastSnapshotRepositoryURL = lastSnapshotRepositoryPayload.substring(lastSnapshotRepositoryPayload.indexOf("https://")) + "SNAPSHOT"

        val repositoryContent = getSiteContent(lastSnapshotRepositoryURL)
        val snapshotPayload = repositoryContent.split("href=\"")
        val subSnapshotPayload = snapshotPayload.filter { p -> p.contains(".jar\"") }

        val snapshotDownloadURLPayload = subSnapshotPayload[subSnapshotPayload.size - 1]

        val url = snapshotDownloadURLPayload.substring(0, snapshotDownloadURLPayload.indexOf("\">"))
        val parts = url.substring(0, url.indexOf(".jar")).split("-")

        return "SNAPSHOT-" + parts[parts.size - 3] + "-" + parts[parts.size - 2] + "-" + parts[parts.size - 1]
    }

    /**
     * Gets the content of the site by given [url].
     */
    private fun getSiteContent(url: String): String {
        val content = StringBuilder()

        val httpsURLConnection = URL(url).openConnection() as HttpsURLConnection
        httpsURLConnection.inputStream.use { stream ->
            InputStreamReader(stream).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    bufferedReader.lines().forEach { line ->
                        content.appendln(line)
                    }
                }
            }
        }

        return content.toString()
    }
}