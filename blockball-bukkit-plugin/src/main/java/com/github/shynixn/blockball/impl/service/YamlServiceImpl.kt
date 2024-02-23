package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.deprecated.YamlService
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Path

class YamlServiceImpl : YamlService {
    /**
     * Writes the given [content] to the given [path].
     */
    override fun write(path: Path, content: Map<String, Any?>) {
        val configuration = YamlConfiguration()
        val key = content.keys.toTypedArray()[0]
        configuration.set(key, content[key])
        configuration.save(path.toFile())
    }

    /**
     * Reads the content from the given [path].
     */
    override fun read(path: Path): Map<String, Any?> {
        val configuration = YamlConfiguration()
        configuration.load(path.toFile())
        val section = configuration.getValues(true)
        deserialize(section)
        return section
    }

    /**
     * Deserializes the given section.
     */
    private fun deserialize(section: MutableMap<String, Any?>) {
        for (key in section.keys) {
            if (section[key] is MemorySection) {
                val map = (section[key] as MemorySection).getValues(false)
                deserialize(map)
                section[key] = map
            }
        }
    }
}
