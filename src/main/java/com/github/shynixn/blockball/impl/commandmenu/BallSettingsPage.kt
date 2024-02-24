package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.entity.Particle
import com.github.shynixn.blockball.entity.Sound
import com.github.shynixn.blockball.enumeration.*

class BallSettingsPage : Page(BallSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 30
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.BALL
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
        val ballMeta = (cache[0] as Arena).meta.ballMeta
        if (command == MenuCommand.BALL_OPEN) {
            cache[5] = null
        } else if (command == MenuCommand.BALL_SKIN && args.size == 3) {
            ballMeta.skin = args[2]
        } else if (command == MenuCommand.BALL_SIZE_CALLBACK && args.size == 3) {
            ballMeta.size = BallSize.values()[args[2].toInt()]
        } else if (command == MenuCommand.BALL_SLIME) {
            ballMeta.isSlimeVisible = !ballMeta.isSlimeVisible
        } else if (command == MenuCommand.BALL_INTERACTION_HITBOX && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.interactionHitBoxSize = args[2].toDouble()
        } else if (command == MenuCommand.BALL_KICKPASS_HITBOX && args.size == 3 && args[2].toDoubleOrNull() != null) {
            ballMeta.kickPassHitBoxSize = args[2].toDouble()
        } else if (command == MenuCommand.BALL_INTERACT_COOLDOWN && args.size == 3 && args[2].toIntOrNull() != null) {
            ballMeta.interactionCoolDown = args[2].toInt()
        } else if (command == MenuCommand.BALL_KICKPASS_DELAY && args.size == 3 && args[2].toIntOrNull() != null) {
            ballMeta.kickPassDelay = args[2].toInt()
        } else if (command == MenuCommand.BALL_TOGGLE_ALWAYSBOUNCE) {
            ballMeta.alwaysBounce = !ballMeta.alwaysBounce
        } else if (command == MenuCommand.BALL_TOGGLE_ROTATING) {
            ballMeta.rotating = !ballMeta.rotating
        } else if (command == MenuCommand.BALL_PARTICLEACTION_CALLBACK && args.size == 3) {
            cache[5] = ballMeta.particleEffects[BallActionType.values()[args[2].toInt()]]
        } else if (command == MenuCommand.BALL_SOUNDACTION_CALLBACK && args.size == 3) {
            cache[5] = ballMeta.soundEffects[BallActionType.values()[args[2].toInt()]]
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
        val ballMeta = (cache[0] as Arena).meta.ballMeta
        val builder = ChatBuilder()
            .component("- Skin: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(ballMeta.skin).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALL_SKIN.command)
            .setHoverText("Changes the skin of the ball. Can be the name of a skin or a skin URL.")
            .builder().nextLine()
            .component("- Skin Size: " + ballMeta.size.name).builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_BALLSIZES.command)
            .setHoverText("Opens the selectionbox for ball sizes.")
            .builder().nextLine()
            .component("- Slime Visible: " + ballMeta.isSlimeVisible).builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BALL_SLIME.command)
            .setHoverText("Toggles if the slime hitbox is rendered instead of the ball helmet.")
            .builder().nextLine()
            .component("- Interaction Hitbox Size: " + ballMeta.interactionHitBoxSize).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALL_INTERACTION_HITBOX.command)
            .setHoverText("Changes the hitbox size when running into the ball.")
            .builder().nextLine()
            .component("- KickPass Hitbox Size: " + ballMeta.kickPassHitBoxSize).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALL_KICKPASS_HITBOX.command)
            .setHoverText("Changes the hitbox size when left or rightclicking the ball.")
            .builder().nextLine()
            .component("- Always Bounce: " + ballMeta.alwaysBounce).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BALL_TOGGLE_ALWAYSBOUNCE.command)
            .setHoverText("Should the ball always bounce of surfaces?")
            .builder().nextLine()
            .component("- Rotation Animation: " + ballMeta.rotating).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BALL_TOGGLE_ROTATING.command)
            .setHoverText("Should the ball play a rotation animation?")
            .builder().nextLine()
            .component("- KickPass Delay: " + ballMeta.kickPassDelay).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALL_KICKPASS_DELAY.command)
            .setHoverText("Delay in ticks until a kick or pass is executed.")
            .builder().nextLine()
            .component("- Interaction Cooldown: " + ballMeta.interactionCoolDown).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.BALL_INTERACT_COOLDOWN.command)
            .setHoverText("Cooldown in ticks until the next player can interact with the ball again.")
            .builder().nextLine()
            .component("- Ball Modifiers: ").builder()
            .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BALLMOD_OPEN.command)
            .setHoverText("Opens the page for ball modifiers.")
            .builder().nextLine()
            .component("- Sound Effect: ").builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_BALL_SOUNDEFFECTS.command)
            .setHoverText("Opens the selection page for action binders.")
            .builder().nextLine()
            .component("- Particle Effect: ").builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_BALL_PARTICLEFFECTS.command)
            .setHoverText("Opens the selection page for action binders.")
            .builder().nextLine()
        if (cache[5] != null && cache[5] is Sound) {
            builder.component("- Selected Sound-effect: ").builder().component(MenuClickableItem.PAGE.text)
                .setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SOUND_BALL.command)
                .setHoverText("Opens the page for editing sound effects.")
                .builder().nextLine()
        } else if (cache[5] != null && cache[5] is Particle) {
            builder.component("- Selected Particle-effect: ").builder().component(MenuClickableItem.PAGE.text)
                .setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.PARTICLE_BALL.command)
                .setHoverText("Opens the page for editing particle effects.")
                .builder().nextLine()
        }
        return builder
    }
}
