package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import org.bukkit.entity.Player

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
class BallModifierSettingsPage : Page(BallModifierSettingsPage.ID, BallSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 31
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.BALLMODIFIER
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val ballMeta = (cache[0] as BukkitArena).meta.ballMeta.modifiers
        if (command == BlockBallCommand.BALLMOD_HORIZONTALTOUCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.horizontalTouchModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_VERTICALTOUCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.verticalTouchModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_HORIZONTALKICK && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.horizontalKickStrengthModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_VERTICALKICK && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.verticalKickStrengthModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_HORIZONTALTHROW && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.horizontalThrowStrengthModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_VERTICALTHROW && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.verticalThrowStrengthModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_ROLLINGDISTANCE && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.rollingDistanceModifier = args[2].toDouble()
        }
        else if (command == BlockBallCommand.BALLMOD_GRAVITY && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.gravityModifier = args[2].toDouble()
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val ballMeta = (cache[0] as BukkitArena).meta.ballMeta.modifiers
        return ChatBuilder()
                .component("- Touch Strength (Horizontal): " + ballMeta.horizontalTouchModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_HORIZONTALTOUCH.command)
                .setHoverText("Changes the horizontal speed modifier when a player touches a ball.")
                .builder().nextLine()
                .component("- Touch Strength (Vertical): " + ballMeta.verticalTouchModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_VERTICALTOUCH.command)
                .setHoverText("Changes the vertical speed modifier when a player touches a ball.")
                .builder().nextLine()
                .component("- Kick Strength (Horizontal): " + ballMeta.horizontalKickStrengthModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_HORIZONTALKICK.command)
                .setHoverText("Changes the horizontal speed modifier when a player left clicks a ball.")
                .builder().nextLine()
                .component("- Kick Strength (Vertical): " + ballMeta.verticalKickStrengthModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_VERTICALKICK.command)
                .setHoverText("Changes the vertical speed modifier when a player left clicks a ball.")
                .builder().nextLine()
                .component("- Throw Strength (Horizontal): " + ballMeta.horizontalThrowStrengthModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_HORIZONTALTHROW.command)
                .setHoverText("Changes the horizontal speed modifier when a player throws the ball after grabbing it by rightclicking.")
                .builder().nextLine()
                .component("- Throw Strength (Vertical): " + ballMeta.verticalThrowStrengthModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_VERTICALTHROW.command)
                .setHoverText("Changes the vertical speed modifier when a player throws the ball after grabbing it by rightclicking.")
                .builder().nextLine()
                .component("- Rolling Distance modifier: " + ballMeta.rollingDistanceModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_ROLLINGDISTANCE.command)
                .setHoverText("Changes time modifier how long a ball keeps rolling.")
                .builder().nextLine()
                .component("- Gravity modifier: " + ballMeta.gravityModifier).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BALLMOD_GRAVITY.command)
                .setHoverText("Changes gravity modifier which decides how fast a ball falls onto the ground. Negative values will cause the ball to float upwards.")
                .builder().nextLine()
    }
}