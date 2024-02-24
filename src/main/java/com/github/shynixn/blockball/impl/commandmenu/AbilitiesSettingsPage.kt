package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.blockball.enumeration.MenuClickableItem
import com.github.shynixn.blockball.enumeration.MenuCommand
import com.github.shynixn.blockball.enumeration.MenuPageKey

class AbilitiesSettingsPage : Page(AbilitiesSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 19
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.ABILITIES
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        return ChatBuilder()
            .component("- DoubleJump:").builder()
            .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.DOUBLEJUMP_OPEN.command)
            .setHoverText("Configure the double jump options for the players.")
            .builder().nextLine()
    }
}
