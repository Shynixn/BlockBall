package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.MenuCommand
import com.github.shynixn.blockball.enumeration.MenuCommandResult
import com.github.shynixn.blockball.enumeration.MenuPageKey
import com.github.shynixn.mcutils.common.ChatColor

abstract class Page(
    /**
     * Page id.
     */
    val id: Int, private val previousId: Int
) {
    /**
     * GetPrevious id.
     */
    open fun getPreviousIdFrom(cache: Array<Any?>): Int {
        return previousId
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    abstract fun getCommandKey(): MenuPageKey

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    open fun buildPage(cache: Array<Any?>): ChatBuilder? {
        return null
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    open fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        return MenuCommandResult.SUCCESS
    }

    /**
     * Refactors a list to a single line.
     */
    internal fun List<String>.toSingleLine(): String {
        val builder = StringBuilder()
        this.forEachIndexed { index, p ->
            builder.append(ChatColor.translateChatColorCodes('&', p))
            if (index + 1 != this.size)
                builder.append('\n')
            builder.append(ChatColor.RESET)
        }
        return builder.toString()
    }

    /**
     * Merges page arguments.
     */
    internal fun mergeArgs(starting: Int, args: Array<String>): String {
        val builder = StringBuilder()

        for (i in starting until args.size) {
            if (builder.isNotEmpty()) {
                builder.append(' ')
            }
            builder.append(args[i])
        }

        return builder.toString()
    }
}
