# Customizing the Ball

BlockBall separates ball behavior from arena configuration. Every ball is defined in its own YAML file inside the `/plugins/BlockBall/ball/` directory. Each file describes the visual appearance, the physics simulation, and exactly how players interact with the ball. You can create as many ball types as you like and switch between them per arena.

---

## Setting the Ball for an Arena

Open your arena file at `/plugins/BlockBall/arena/<arena_name>.yml` and set the `ballName` property to the `name` value of the ball file you want to use:

```yaml
# Unique identifier of the ball to use.
ballName: soccer_ball
```

The default value is `soccer_ball`. To switch to the curve soccer ball, for example:

```yaml
ballName: curve_soccer_ball
```

Reload the arena after saving:

```bash
/blockball reload
```

---

## Ball File Structure

A ball file has three top-level sections: `render`, `physics`, and `interactions`.

### `name`

The unique identifier for this ball. Must match the filename without the `.yml` extension and is used as the `ballName` value in the arena file.

```yaml
name: soccer_ball
```

---

### `render` — Visual Appearance

Controls how the ball looks in game.

| Property | Description                                                                                              |
|----------|----------------------------------------------------------------------------------------------------------|
| `modelScale` | Global scale of the rendered ball entity. You can create giant balls by using values such as `2.0`         |
| `visualItem.typeName` | The item type used as the ball skin, e.g. `PLAYER_HEAD,397`.                                             |
| `visualItem.skinBase64` | Base64-encoded texture JSON pointing to a Minecraft skin URL. This is how you change the ball's texture. |
| `rotationEnabled` | When `true`, the ball visually spins based on its velocity direction.                                    |
| `visualVerticalOffset` | Fine-tunes the vertical position of the rendered model relative to the physics entity.                   |
| `renderDistance` | Distance in blocks within which the ball is rendered for players.                                        |
| `slimeVisible` | Legacy option. When `true`, the underlying Slime physics entity is rendered. Keep this `false`.          |

```yaml
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: '1'
    durability: '3'
    displayName: null
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
    lore: null
    nbt: null
    component: null
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
```

---

### `physics` — Simulation Properties

Controls how the ball behaves in the world.

| Property | Description |
|----------|-------------|
| `interactionBoundsSize` | Diameter of the hitbox used for click and interact detection. Enable F3+B in-game to visualize it. |
| `collisionBoundsSize` | Diameter of the hitbox used for player body contact (touch/collide). |
| `verticalOffset` | Vertical shift applied to the physics simulation boundary. |
| `bounciness` | Coefficient of restitution. `0.0` = no bounce, `1.0` = perfectly elastic bounce. |
| `mass` | Simulated weight of the ball. Higher mass requires more impulse to change velocity. |
| `restVelocityThreshold` | Minimum speed before the ball is considered at rest and stops moving. |
| `gravityModifier` | Downward acceleration applied per tick when airborne. Higher values make the ball fall faster. |
| `airDrag` | Velocity reduction applied per tick while the ball is in the air. |
| `rollingFriction` | Velocity reduction applied per tick while the ball rolls along the ground. |
| `spinDrag` | Rate at which rotational spin decays per tick. `0.0` means spin never decays. |
| `curveMultiplier` | How strongly active spin curves the flight path. Higher values create sharper arcs. |
| `globalInteractionCooldownTicks` | Server-wide cooldown in ticks before another physics interaction can be processed. |
| `perPlayerInteractionCooldownTicks` | Per-player cooldown in ticks to prevent rapid-fire interaction exploits. |
| `fetchPlayerPositionsIntervalTicks` | How often (in ticks) player positions are refreshed for collision calculations. |

```yaml
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.7
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.07
  airDrag: 0.001
  rollingFriction: 0.1
  spinDrag: 0.05
  curveMultiplier: 0.05
  globalInteractionCooldownTicks: 20
  perPlayerInteractionCooldownTicks: 7
  fetchPlayerPositionsIntervalTicks: 2
```

---

### `interactions` — Player Controls

Defines a list of rules that map player inputs to ball actions. Rules are evaluated in order; the first matching rule is applied.

Each rule has:

| Property | Description |
|----------|-------------|
| `triggerType` | The player input that activates this rule. See trigger types below. |
| `conditionHotBarRangeStart` | First hotbar slot index (0–8) that allows this rule to fire. |
| `conditionHotBarRangeEnd` | Last hotbar slot index (0–8) that allows this rule to fire. Use `0`–`8` to match all slots. |
| `conditionGrabbedBySelf` | When `true`, this rule only fires if the triggering player is currently holding (grabbing) the ball. |
| `executionType` | The action to perform: `SHOOT` to launch the ball, `GRAB` to attach it to the player. |
| `horizontalImpulse` | Horizontal force applied to the ball in the player's facing direction. |
| `verticalImpulse` | Vertical (upward) force applied to the ball. |
| `spinImpulse` | Initial rotational spin around the Y-axis. Negative = left curve (slice), positive = right curve (hook). |
| `effectName` | Name of a particle/sound effect to play on trigger. Leave empty for no effect. |

#### Available Trigger Types

| Category | Types |
|----------|-------|
| Left Click | `LEFT_CLICK`, `JUMP_LEFT_CLICK`, `SNEAK_LEFT_CLICK`, `SPRINT_LEFT_CLICK` |
| Right Click | `RIGHT_CLICK`, `JUMP_RIGHT_CLICK`, `SNEAK_RIGHT_CLICK`, `SPRINT_RIGHT_CLICK` |
| Collision | `COLLIDE`, `JUMP_COLLIDE`, `SNEAK_COLLIDE`, `SPRINT_COLLIDE` |

---

## Built-In Ball Examples

BlockBall ships with three pre-configured ball files that demonstrate different gameplay styles. They are all found in `/plugins/BlockBall/ball/`.

---

### `soccer_ball.yml` — Default Soccer Ball

This is the standard ball used by all arenas unless you specify otherwise. It offers straightforward controls: walk into the ball to dribble it forward, left-click to kick it, and sprint-click for a powerful shot.

#### How to Play

| Action | Input | Effect |
|--------|-------|--------|
| Dribble / Touch | Walk into ball (`COLLIDE`) | Nudges the ball forward at low height |
| Standard kick | `LEFT_CLICK` | Kicks the ball forward at a medium arc |
| Power shot | `SPRINT` + `LEFT_CLICK` | Blasts the ball with maximum height and force |

All interactions work regardless of which hotbar slot is selected (slots 0–8).

#### Configuration

```yaml
name: soccer_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: '1'
    durability: '3'
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.7
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.07
  airDrag: 0.001
  rollingFriction: 0.1
  spinDrag: 0.05
  curveMultiplier: 0.05
  globalInteractionCooldownTicks: 20
  perPlayerInteractionCooldownTicks: 7
  fetchPlayerPositionsIntervalTicks: 2
interactions:
- triggerType: COLLIDE
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.2
  spinImpulse: 0.0
  effectName: ''
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.6
  spinImpulse: 0.0
  effectName: ball_kick
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 1.0
  spinImpulse: 0.0
  effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: soccer_ball
```

---

### `curve_soccer_ball.yml` — Curve Soccer Ball

An advanced soccer ball that uses the hotbar slot to determine the direction of spin applied to the ball. Players who position the selected slot to the left, center, or right of their hotbar can curve the ball in different directions. Sprinting adds extra height and speed, and crouching while clicking lets you play a short backpass.

#### How to Play

The **active hotbar slot** controls the curve direction:

| Hotbar Slots | Kick (`LEFT_CLICK`) | Power Shot (`SPRINT_LEFT_CLICK`) |
|---|---|---|
| Slots 1–3 (left) | Left-curving kick | Left-curving power shot |
| Slots 4–6 (center) | Straight kick | Straight power shot |
| Slots 7–9 (right) | Right-curving kick | Right-curving power shot |

!!! note "Hotbar Slots"
    The hotbar slot index is 0-based in configuration (`0` = slot 1, `8` = slot 9).

| Action | Input | Effect |
|--------|-------|--------|
| Dribble / Touch | Walk into ball (`COLLIDE`) | Short forward touch with no spin (all slots) |
| Kick | `LEFT_CLICK` | Curved or straight kick depending on hotbar |
| Power shot | `SPRINT` + `LEFT_CLICK` | High-arc curved or straight power shot |
| Backpass / Cushion | `SNEAK` + `LEFT_CLICK` | Short low reverse touch (h: -0.6) — all slots |

The `curveMultiplier` in the physics section determines how dramatically the spin bends the flight path. The default is `0.05`; raise it to create sharper curves.

#### Configuration

```yaml
name: curve_soccer_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: '1'
    durability: '3'
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.7
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.07
  airDrag: 0.001
  rollingFriction: 0.1
  spinDrag: 0.05
  curveMultiplier: 0.05
  globalInteractionCooldownTicks: 20
  perPlayerInteractionCooldownTicks: 7
  fetchPlayerPositionsIntervalTicks: 2
interactions:
  # Dribble touch — no spin, all slots
- triggerType: COLLIDE
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.2
  spinImpulse: 0.0
  effectName: ''
  # Kick — left slots → left spin
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 2
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.6
  spinImpulse: -2.0
  effectName: ball_kick
  # Kick — center slots → no spin
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 3
  conditionHotBarRangeEnd: 5
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.6
  spinImpulse: 0.0
  effectName: ball_kick
  # Kick — right slots → right spin
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 6
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.6
  spinImpulse: 2.0
  effectName: ball_kick
  # Power shot — left slots → left spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 2
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 1.0
  spinImpulse: -2.0
  effectName: ball_kick
  # Power shot — center slots → no spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 3
  conditionHotBarRangeEnd: 5
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 1.0
  spinImpulse: 0.0
  effectName: ball_kick
  # Power shot — right slots → right spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 6
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 1.0
  spinImpulse: 2.0
  effectName: ball_kick
  # Sneak click — short backpass, all slots
- triggerType: SNEAK_LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: -0.6
  verticalImpulse: 0.2
  spinImpulse: 0.0
  effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: curve_soccer_ball
```

---

### `hand_ball.yml` — Handball

A handball-inspired configuration that introduces a **grab mechanic**. Players can pick up the ball and carry it, then throw it with spin control based on their active hotbar slot — much like a handball game. Without the ball in hand, clicking simply bumps the ball forward gently.

#### How to Play

**Step 1 — Pick up the ball**

When you are near the ball and do not have it, select **hotbar slot 6–9** and left-click the ball. This triggers a `GRAB` action and the ball attaches to you.

**Step 2 — Throw the ball**

Once you are holding the ball, use `LEFT_CLICK` to throw it. The active hotbar slot controls the throw direction:

| Hotbar Slots | Throw Direction |
|---|---|
| Slots 1–3 (left) | Left-curving throw |
| Slots 4–6 (center) | Straight throw |
| Slots 7–9 (right) | Right-curving throw |

All throws have `horizontalImpulse: 1.3` and `verticalImpulse: 0.6` — a strong, arcing pass.

**Without the ball in hand (slots 1–5)**

Left-clicking the ball without holding it will do a short, gentle bump (`h: 0.3, v: 0.4`). If an opponent is currently carrying the ball, the bump knocks it out of their hand and sends it forward — useful for interceptions and quick deflections.

**Grabbing the ball from an opponent (slots 6–9)**

If another player is holding the ball, you can steal it by selecting slots 6–9 and left-clicking them. The `GRAB` action releases the ball from their possession and immediately attaches it to you.

#### How to Play Summary

| Action | Input | Condition | Effect |
|--------|-------|-----------|--------|
| Bump | `LEFT_CLICK` (slots 1–5) | Not holding ball | Gentle forward bump; knocks the ball out of an opponent's hand if they are carrying it |
| Grab | `LEFT_CLICK` (slots 6–9) | Not holding ball | Pick up the ball; steals it directly from an opponent if they are currently holding it |
| Throw left | `LEFT_CLICK` (slots 1–3) | Holding ball | Throw with left spin |
| Throw straight | `LEFT_CLICK` (slots 4–6) | Holding ball | Throw straight |
| Throw right | `LEFT_CLICK` (slots 7–9) | Holding ball | Throw with right spin |

#### Configuration

```yaml
name: hand_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: '1'
    durability: '3'
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.7
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.07
  airDrag: 0.001
  rollingFriction: 0.1
  spinDrag: 0.05
  curveMultiplier: 0.05
  globalInteractionCooldownTicks: 20
  perPlayerInteractionCooldownTicks: 7
  fetchPlayerPositionsIntervalTicks: 2
interactions:
  # Bump without ball — gentle push (slots 1–5)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 0
    conditionHotBarRangeEnd: 4
    conditionGrabbedBySelf: false
    executionType: SHOOT
    horizontalImpulse: 0.3
    verticalImpulse: 0.4
    spinImpulse: 0.0
    effectName: ''
  # Grab — pick up the ball (slots 6–9)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 5
    conditionHotBarRangeEnd: 8
    conditionGrabbedBySelf: false
    executionType: GRAB
    horizontalImpulse: 0.0
    verticalImpulse: 0.0
    spinImpulse: 0.0
    effectName: ''
  # Throw left — left spin (slots 1–3, holding ball)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 0
    conditionHotBarRangeEnd: 2
    conditionGrabbedBySelf: true
    executionType: SHOOT
    horizontalImpulse: 1.3
    verticalImpulse: 0.6
    spinImpulse: -2.0
    effectName: ball_kick
  # Throw straight (slots 4–6, holding ball)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 3
    conditionHotBarRangeEnd: 5
    conditionGrabbedBySelf: true
    executionType: SHOOT
    horizontalImpulse: 1.3
    verticalImpulse: 0.6
    spinImpulse: 0.0
    effectName: ball_kick
  # Throw right — right spin (slots 7–9, holding ball)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 6
    conditionHotBarRangeEnd: 8
    conditionGrabbedBySelf: true
    executionType: SHOOT
    horizontalImpulse: 1.3
    verticalImpulse: 0.6
    spinImpulse: 2.0
    effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: hand_ball
```

---

## Creating a Custom Ball

1. Create a new file in `/plugins/BlockBall/ball/`, for example `my_ball.yml`.
2. Set `name: my_ball` at the top of the file.
3. Copy the `render`, `physics`, and `interactions` sections from one of the example files as a starting point.
4. Adjust the properties to your liking.
5. Set `ballName: my_ball` in your arena file and reload the arena.

!!! tip "Changing the Ball Texture"
    Find a Minecraft head texture you like on a site like [minecraft-heads.com](https://minecraft-heads.com). Copy the **Value** field (a Base64 string) and paste it into `visualItem.skinBase64`.

!!! tip "Tuning Physics"
    Start with `gravityModifier`, `bounciness`, and `rollingFriction` — these three values have the biggest impact on how the ball feels. Small changes (e.g. `0.01`) can make a noticeable difference.
