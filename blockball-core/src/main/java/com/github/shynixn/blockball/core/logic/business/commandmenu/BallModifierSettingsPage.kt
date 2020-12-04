package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity

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
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.BALLMODIFIER
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(
        player: P,
        command: MenuCommand,
        cache: Array<Any?>,
        args: Array<String>
    ): MenuCommandResult {
        val ballMeta = (cache[0] as Arena).meta.ballMeta.movementModifier
        if (command == MenuCommand.BALLMOD_HORIZONTALTOUCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.horizontalTouchModifier = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_VERTICALTOUCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.verticalTouchModifier = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_SHOTVELOCITY && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.shotVelocity = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_PASSVELOCITY && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.passVelocity = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_MAXSPIN && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.maximumSpinVelocity = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_MAXPITCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.maximumPitch = args[2].toInt()
        } else if (command == MenuCommand.BALLMOD_MINPITCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.minimumPitch = args[2].toInt()
        } else if (command == MenuCommand.BALLMOD_DEFAULTPITCH && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.defaultPitch = args[2].toInt()
        } else if (command == MenuCommand.BALLMOD_ROLLINGRESISTANCE && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.rollingResistance = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_AIRRESISTANCE && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.airResistance = args[2].toDouble()
        } else if (command == MenuCommand.BALLMOD_GRAVITY && args.size == 3 && args[2].toDoubleOrNull() != null) {
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
        val ballMeta = (cache[0] as Arena).meta.ballMeta.movementModifier
        return ChatBuilderEntity()
            .component("- Touch Strength (Horizontal): " + ballMeta.horizontalTouchModifier).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_HORIZONTALTOUCH.command)
            .setHoverText("Changes the horizontal speed modifier when a player touches the ball.")
            .builder().nextLine()
            .component("- Touch Strength (Vertical): " + ballMeta.verticalTouchModifier).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_VERTICALTOUCH.command)
            .setHoverText("Changes the vertical speed modifier when a player touches the ball.")
            .builder().nextLine()
            .component("- Shoot Strength: " + ballMeta.shotVelocity).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_SHOTVELOCITY.command)
            .setHoverText("Changes the power of shooting when a player left-click the ball.")
            .builder().nextLine()
            .component("- Pass Strength: " + ballMeta.passVelocity).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_PASSVELOCITY.command)
            .setHoverText("Changes the power of passing when a player right-click the ball.")
            .builder().nextLine()
            .component("- Maximum Spin Velocity: " + ballMeta.maximumSpinVelocity).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_MAXSPIN.command)
            .setHoverText("The maximum strength of spin that you can apply to the ball. Tilt your head left/right after shooting to make it curve.")
            .builder().nextLine()
            .component("- Maximum Pitch (Vertical): " + ballMeta.maximumPitch).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_MAXPITCH.command)
            .setHoverText("Changes the maximum angle of fire when a player kick or throws the ball.")
            .builder().nextLine()
            .component("- Minimum Pitch (Vertical): " + ballMeta.minimumPitch).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_MINPITCH.command)
            .setHoverText("Changes the minimum angle of fire when a player kick or throws the ball.")
            .builder().nextLine()
            .component("- Standard Pitch (Vertical): " + ballMeta.defaultPitch).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_DEFAULTPITCH.command)
            .setHoverText("Changes the angle of fire when a player kick or throws the ball without tilting his head up or down.")
            .builder().nextLine()
            .component("- Air Resistance: " + ballMeta.airResistance).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_AIRRESISTANCE.command)
            .setHoverText("A value from 0.0 - 1.0 which reduces the speed in the air.")
            .builder().nextLine()
            .component("- Rolling Resistance: " + ballMeta.rollingResistance).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_ROLLINGRESISTANCE.command)
            .setHoverText("A value from 0.0 - 1.0 which reduces the speed on the ground.")
            .builder().nextLine()
            .component("- Gravity Modifier: " + ballMeta.gravityModifier).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALLMOD_GRAVITY.command)
            .setHoverText("Changes gravity modifier which decides how fast a ball falls onto the ground. Negative values will cause the ball to float upwards.")
            .builder().nextLine()
    }
}
