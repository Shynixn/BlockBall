package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallExecuteActionType
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.repository.Element

@Comment(
    "###############",
    "",
    "Configuration profile for a physical ball instance in the BlockBall engine.",
    "",
    "###############"
)
class SoccerBallMeta : Element {
    @Comment("Unique identifier for this ball configuration template used within the plugin system.")
    override var name: String = "soccer_ball"

    @Comment("Visual settings defining how the ball looks, its scale, and layout offsets.")
    var render = RenderMeta()

    @Comment("Physical and kinematic properties governing gravity, friction, drag, and collision bounds.")
    var physics = PhysicsMeta()

    @Comment("Gameplay modifiers determining how players interact with the ball via hotbars and clicks.")
    val interactions: MutableList<InteractionMeta> = ArrayList()

    init {
        interactions.add(InteractionMeta().also {
            it.triggerType = BallTriggerActionType.COLLIDE
            it.executionType = BallExecuteActionType.SHOOT
        })
    }

    class InteractionMeta {
        @Comment(
            "The input trigger required to execute this action. Available types:",
            " - Clicks: LEFT_CLICK, JUMP_LEFT_CLICK, SNEAK_LEFT_CLICK, SPRINT_LEFT_CLICK",
            " - Right Clicks: RIGHT_CLICK, JUMP_RIGHT_CLICK, SNEAK_RIGHT_CLICK, SPRINT_RIGHT_CLICK",
            " - Collisions: COLLIDE, JUMP_COLLIDE, SNEAK_COLLIDE, SPRINT_COLLIDE"
        )
        var triggerType: BallTriggerActionType = BallTriggerActionType.LEFT_CLICK

        @Comment("The starting index (0-8) of the player's hotbar range allowed to trigger this action.")
        var conditionHotBarRangeStart: Int = 0

        @Comment("The ending index (0-8) of the player's hotbar range allowed to trigger this action.")
        var conditionHotBarRangeEnd: Int = 8

        @Comment("If set to true then this action is only executed when the ball is grabbed.")
        var conditionGrabbed : Boolean = false

        @Comment("The mechanical action applied to the ball upon a successful trigger. Available types: SHOOT, GRAB")
        var executionType: BallExecuteActionType = BallExecuteActionType.SHOOT

        @Comment("The instantaneous horizontal impulse vector applied to the ball.")
        var horizontalImpulse: Double = 1.0

        @Comment("The instantaneous vertical impulse vector applied to the ball.")
        var verticalImpulse: Double = 1.0

        @Comment("The particle/sound effect name to play when this interaction is successfully triggered.")
        var effectName: String = ""
    }

    class PhysicsMeta {
        @Comment("The bounding diameter used for tracking player click/interact selections. View via F3 + B.")
        var interactionBoundsSize: Double = 1.5

        @Comment("The bounding diameter used for physical player cross-contact and touch-handling.")
        var collisionBoundsSize: Double = 1.0

        @Comment("Vertical positioning offset on the Y-axis to align the physical simulation boundaries.")
        var verticalOffset: Double = -0.3

        @Comment("The coefficient of restitution (0.0 to 1.0). 0.0 means no bounce, 1.0 means perfectly elastic bounce back.")
        var bounciness: Double = 0.7

        @Comment("The simulated weight of the ball. Higher mass requires more impulse force to shift velocity.")
        var mass: Double = 1.0

        @Comment("The minimum speed threshold. If velocity falls below this value, the ball comes to a complete rest.")
        var restVelocityThreshold: Double = 0.01

        @Comment("Downward acceleration constant applied per tick when the ball is airborne.")
        var gravityModifier: Double = 0.07

        @Comment("Velocity dampening coefficient applied per tick while airborne (Air Drag).")
        var airDrag: Double = 0.001

        @Comment("Velocity dampening coefficient applied per tick while rolling along the ground (Surface Friction).")
        var rollingFriction: Double = 0.1

        @Comment("The physical ceiling capping maximum angular spin velocity.")
        var maxSpinVelocity: Double = 0.08

        @Comment("Velocity dampening modifier applied continuously to the ball's angular spin vector.")
        var spinDampening: Double = 0.05

        @Comment("The maximum pitch angle constraint for launch trajectories.")
        var maxLaunchPitch: Int = 60

        @Comment("The minimum pitch angle constraint for launch trajectories.")
        var minLaunchPitch: Int = 0

        @Comment("The default baseline pitch angle used when calculations are omitted or neutral.")
        var defaultLaunchPitch: Int = 20

        @Comment("Global cooldown in server ticks before the ball can process another physics interaction.")
        var globalInteractionCooldownTicks: Int = 20

        @Comment("Per-player input throttling cooldown in ticks to prevent rapid-fire physics exploits.")
        var perPlayerInteractionCooldownTicks: Int = 7

        @Comment("Interval in ticks for refreshing player position data used in collision and interaction calculations.")
        var fetchPlayerPositionsIntervalTicks : Int = 2
    }
    class RenderMeta {
        @Comment("The global scale modifier transforming the physical scale of the rendered model entity.")
        var modelScale: Double = 1.0

        @Comment("The underlying item structure holding texture, metadata, and skin maps for rendering.")
        var visualItem: Item = Item().also {
            it.typeName = "PLAYER_HEAD"
            it.durability = "3"
            it.skinBase64 =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ=="
        }

        @Comment("Enables or disables visual procedural rotation based on the ball's velocity vector.")
        var rotationEnabled: Boolean = true

        @Comment("Visual model offset matching the entity presentation strictly to your texture profile.")
        var visualVerticalOffset: Double = -1.0

        @Comment("Render radius where the ball is visible to players. Beyond this distance, the ball will not be rendered.")
        var renderDistance : Int = 60

        @Comment("Legacy fallback: Determines if the physics-simulation Slime entity should be rendered visible.")
        var slimeVisible: Boolean = false
    }
}
