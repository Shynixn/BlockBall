package com.github.shynixn.blockball.deprecated

import java.nio.file.Path

@Deprecated("Use FastXml serializer.")
interface YamlService {
    /**
     * Writes the given [content] to the given [path].
     */
    fun write(path: Path, content: Map<String, Any?>)

    /**
     * Reads the content from the given [path].
     */
    fun read(path: Path): Map<String, Any?>
}
