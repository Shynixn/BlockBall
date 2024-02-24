package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.enumeration.PluginDependency

interface DependencyService {

    /**
     * Checks for installed dependencies and shows console output.
     */
    fun checkForInstalledDependencies()

    /**
     * Returns if the given [pluginDependency] is installed.
     */
    fun isInstalled(pluginDependency: PluginDependency, version: String? = null): Boolean
}
