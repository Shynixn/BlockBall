package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.Template


interface TemplateService {

    /**
     * Copies the stored templates file to the template folder if they
     * do not already exist.
     */
    fun copyTemplateFilesFromResources()

    /**
     * Returns a [List] of available
     */
    fun getAvailableTemplates(): List<Template>

    /**
     * Generates a new [Arena] from the given [template].
     */
    fun generateArena(template: Template): Arena
}
