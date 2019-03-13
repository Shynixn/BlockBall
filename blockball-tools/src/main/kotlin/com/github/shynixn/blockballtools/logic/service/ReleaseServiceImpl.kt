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
        var latestId = "?"

        for (item in (tableItem.childNodes()[1] as Element).children()) {
            if (item.tagName() == "tr") {
                val nameColumn = item.children()[0]

                if (nameColumn.children().size > 0) {
                    val value = nameColumn.children()[0].html().replace("/", "")

                    if (value.toCharArray()[0].toString().toIntOrNull() != null && !value.endsWith("-SNAPSHOT")) {
                        latestId = value
                    }
                }
            }
        }

        return latestId
    }
}