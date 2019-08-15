package com.github.shynixn.blockballtools.logic.service

import com.github.shynixn.blockballtools.contract.SonaTypeService
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ReleaseServiceImpl : SonaTypeService {
    /**
     * Searches the repository for the latest download link. Throws
     * a [IllegalArgumentException] if not found.
     */
    override fun findDownloadUrl(repository: String): String {
        val downloadUrl =
            "https://oss.sonatype.org/content/repositories/public/com/github/shynixn/blockball/blockball-bukkit-plugin/VERSION/blockball-bukkit-plugin-VERSION.jar"
        val id = findId(repository)
        val resultDownloadUrl = downloadUrl.replace("VERSION", id)

        return resultDownloadUrl
    }

    /**
     * Searches the repository for the latest id. Throws
     * a [IllegalArgumentException] if not found.
     */
    override fun findId(repositor: String): String {
        val tableItem = Jsoup.connect(repositor).get().body().getElementsByTag("table")[0]
        var latestId = "0.0.0"

        for (item in (tableItem.childNodes()[1] as Element).children()) {
            if (item.tagName() == "tr") {
                val nameColumn = item.children()[0]

                if (nameColumn.children().size > 0) {
                    val value = nameColumn.children()[0].html().replace("/", "")

                    if (value.toCharArray()[0].toString().toIntOrNull() != null && !value.endsWith("-SNAPSHOT")) {
                        val newNumbers = value.split("-")[0].split(".")
                        val oldNumbers = latestId.split("-")[0].split(".")

                        if (newNumbers[0].toInt() > oldNumbers[0].toInt()) {
                            latestId = value
                        }

                        if (newNumbers[0].toInt() == oldNumbers[0].toInt() && newNumbers[1].toInt() > oldNumbers[1].toInt()) {
                            latestId = value
                        }

                        if (newNumbers[0].toInt() == oldNumbers[0].toInt() && newNumbers[1].toInt() == oldNumbers[1].toInt() && newNumbers[2].toInt() > oldNumbers[2].toInt()) {
                            latestId = value
                        }
                    }
                }
            }
        }

        return latestId
    }
}