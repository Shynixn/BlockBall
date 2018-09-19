package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Particle
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
class ParticleEffectPage : Page(ParticleEffectPage.ID, MainConfigurationPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 20
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
        return PageKey.PARTICLEFFECTS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as Arena
        if (command == BlockBallCommand.PARTICLE_DOUBLEJUMP) {
            cache[5] = arena.meta.doubleJumpMeta.particleEffect
            cache[4] = DoubleJumpPage.ID
        } else if (command == BlockBallCommand.PARTICLE_BALL) {
            cache[4] = BallSettingsPage.ID
        } else if (command == BlockBallCommand.PARTICLE_CALLBACK_TYPE && args.size >= 3 && args[2].toIntOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.type = (ParticleType.values()[args[2].toInt()])
        } else if (command == BlockBallCommand.PARTICLE_CALLBACK_EFFECTING && args.size >= 3 && args[2].toIntOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.type = (ParticleType.values()[args[2].toInt()])
        } else if (command == BlockBallCommand.PARTICLE_AMOUNT && args[2].toIntOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.amount = (args[2].toInt())
        } else if (command == BlockBallCommand.PARTICLE_SPEED && args[2].toDoubleOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.speed = args[2].toDouble()
        } else if (command == BlockBallCommand.PARTICLE_OFFSET_X && args[2].toDoubleOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.offSetX = (args[2].toDouble())
        } else if (command == BlockBallCommand.PARTICLE_OFFSET_Y && args[2].toDoubleOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.offSetY = (args[2].toDouble())
        } else if (command == BlockBallCommand.PARTICLE_OFFSET_Z && args[2].toDoubleOrNull() != null) {
            val particleEffect = cache[5] as Particle
            particleEffect.offSetZ = (args[2].toDouble())
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val particleEffect = cache[5] as Particle
        return ChatBuilder()
                .component("- Effecting: " + particleEffect.type).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_PARTICLE_EFFECTINGTYPES.command)
                .setHoverText("Opens the selectionbox for effecting types.")
                .builder().nextLine()
                .component("- Type: " + particleEffect.type.name).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_PARTICLE_TYPES.command)
                .setHoverText("Opens the selectionbox for types.")
                .builder().nextLine()
                .component("- Amount: " + particleEffect.amount).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.PARTICLE_AMOUNT.command)
                .setHoverText("Changes the amount of particles.")
                .builder().nextLine()
                .component("- Speed: " + particleEffect.speed).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.PARTICLE_SPEED.command)
                .setHoverText("Changes the speed of the particles.")
                .builder().nextLine()
                .component("- Offset X: " + particleEffect.offSetX).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.PARTICLE_OFFSET_X.command)
                .setHoverText("Changes the offset X.")
                .builder().nextLine()
                .component("- Offset Y: " + particleEffect.offSetY).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.PARTICLE_OFFSET_Y.command)
                .setHoverText("Changes the offset Y.")
                .builder().nextLine()
                .component("- Offset Z: " + particleEffect.offSetZ).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.PARTICLE_OFFSET_Z.command)
                .setHoverText("Changes the offset Z.")
                .builder().nextLine()
    }
}