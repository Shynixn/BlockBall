@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.sponge.logic.business.service

import com.github.shynixn.blockball.api.business.service.YamlService
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.FileWriter
import java.nio.file.Path

/**
 * Created by Shynixn 2020.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2020 by Shynixn
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
class YamlServiceImpl : YamlService {
    /**
     * Writes the given [content] to the given [path].
     */
    override fun write(path: Path, content: Map<String, Any?>) {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true

        val yaml = Yaml(options)

        FileWriter(path.toFile()).use { stream ->
            yaml.dump(content, stream)
        }
    }

    /**
     * Reads the content from the given [path].
     */
    override fun read(path: Path): Map<String, Any?> {
        val yaml = Yaml()

        FileInputStream(path.toFile()).use { stream ->
            return yaml.load(stream) as Map<String, Any>
        }
    }
}