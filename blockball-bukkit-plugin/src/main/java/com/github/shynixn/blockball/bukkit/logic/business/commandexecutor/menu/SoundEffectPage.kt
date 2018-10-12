package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.enumeration.EffectTargetType
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.blockball.bukkit.logic.business.extension.ChatBuilder
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
class SoundEffectPage : Page(SoundEffectPage.ID, MainConfigurationPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 21
    }

    override fun getPreviousIdFrom(cache: Array<Any?>): Int {
        return cache[4] as Int
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.SOUNDEFFECTS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as Arena
        if (command == BlockBallCommand.SOUND_DOUBLEJUMP) {
            cache[5] = arena.meta.doubleJumpMeta.soundEffect
            cache[4] = DoubleJumpPage.ID
        } else if (command == BlockBallCommand.SOUND_BALL) {
            cache[4] = BallSettingsPage.ID
        } else if (command == BlockBallCommand.SOUND_CALLBACK_TYPE) {
            val soundEffect = cache[5] as Sound
            soundEffect.name = args[2]
        } else if (command == BlockBallCommand.SOUND_CALLBACK_EFFECTING && args.size >= 3 && args[2].toIntOrNull() != null) {
            val soundEffect = cache[5] as Sound
            soundEffect.effectingType = EffectTargetType.values()[args[2].toInt()]
        } else if (command == BlockBallCommand.SOUND_PITCH && args[2].toDoubleOrNull() != null) {
            val soundEffect = cache[5] as Sound
            soundEffect.pitch = args[2].toDouble()
        } else if (command == BlockBallCommand.SOUND_VOLUME && args[2].toDoubleOrNull() != null) {
            val soundEffect = cache[5] as Sound
            soundEffect.volume = args[2].toDouble()
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val soundEffect = cache[5] as Sound
        return ChatBuilder()
                .component("- Effecting: " + soundEffect.effectingType).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_SOUND_EFFECTINGTYPES.command)
                .setHoverText("Opens the selectionbox for effecting types.")
                .builder().nextLine()
                .component("- Type: " + soundEffect.name).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.SOUND_CALLBACK_TYPE.command)
                .setHoverText("Changes the name of the sound.")
                .builder().nextLine()
                .component("- Volume: " + soundEffect.volume).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.SOUND_VOLUME.command)
                .setHoverText("Changes the volume of the sound.")
                .builder().nextLine()
                .component("- Pitch: " + soundEffect.pitch).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.SOUND_PITCH.command)
                .setHoverText("Changes the pitch of the sound.")
                .builder()
    }
}