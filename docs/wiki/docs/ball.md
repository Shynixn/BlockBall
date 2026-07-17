# Customizing the Ball

Each ball is defined in its own YAML file inside `/plugins/BlockBall/ball/`. Files cover appearance, physics, and player interactions. You can create as many ball types as you like and switch between them per arena.

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
    amount: "1"
    durability: "3"
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
  bounciness: 0.4
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.06
  airDrag: 0.002
  rollingFriction: 0.04
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
| `postImpulseTicks` | Ticks to wait before sampling the player's pitch/yaw for mid-flight steering. `0` disables it. |
| `postPitchInfluence` | How much camera pitch adds vertical force after `postImpulseTicks`. `0.0` = no steering, `1.0` = full impulse as vertical force. |
| `postPitchClampAngle` | Max pitch angle for steering. Minecraft: `-90°` = straight up, `+90°` = straight down. `0.0` disables downward steering; `90.0` allows it. |
| `postYawSpinScale` | How much horizontal mouse movement (yaw change) adds spin mid-flight. A 90° flick with scale `0.5` adds ~0.25 spin. Range: `0.1`–`1.0`. |
| `effectName` | Name of a particle/sound effect to play on trigger. Leave empty for no effect. |

#### Available Trigger Types

| Category | Types |
|----------|-------|
| Left Click | `LEFT_CLICK`, `JUMP_LEFT_CLICK`, `SNEAK_LEFT_CLICK`, `SPRINT_LEFT_CLICK` |
| Right Click | `RIGHT_CLICK`, `JUMP_RIGHT_CLICK`, `SNEAK_RIGHT_CLICK`, `SPRINT_RIGHT_CLICK` |
| Collision | `COLLIDE`, `JUMP_COLLIDE`, `SNEAK_COLLIDE`, `SPRINT_COLLIDE` |

---

## Built-In Ball Examples

Three pre-configured ball files are included in `/plugins/BlockBall/ball/`.

---

### `soccer_ball.yml` — Default Soccer Ball

The default ball used by all arenas unless specified otherwise. Walk into the ball to dribble, left-click to kick with camera-based steering.

#### How to Play

| Action | Input | Effect |
|--------|-------|--------|
| Dribble / Touch | Walk into ball (`COLLIDE`) | Nudges the ball forward at low height |
| Standard kick | `LEFT_CLICK` | Kicks the ball forward at a medium-low arc |
| Powered kick | `SPRINT` + `LEFT_CLICK` | Kicks the ball forward at a medium arc with extra oomph |

All interactions work across all hotbar slots (0–8).

**Mid-Flight Camera Steering**

Both kicks sample your camera 7 ticks after firing. Looking up adds lift (`postPitchInfluence`), swiping sideways adds curve spin (`postYawSpinScale`).

#### Configuration

```yaml
name: soccer_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: "1"
    durability: "3"
    displayName: null
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
    lore: null
    nbt: null
    component: null
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.4
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.06
  airDrag: 0.002
  rollingFriction: 0.04
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
  horizontalImpulse: 0.6
  verticalImpulse: 0.2
  spinImpulse: 0.0
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
  effectName: ""
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 0.9
  verticalImpulse: 0.5
  spinImpulse: 0.0
  postImpulseTicks: 7
  postPitchInfluence: 0.5
  postPitchClampAngle: 0.0
  postYawSpinScale: 1.0
  effectName: ball_kick
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 0.9
  verticalImpulse: 0.6
  spinImpulse: 0.0
  postImpulseTicks: 7
  postPitchInfluence: 0.5
  postPitchClampAngle: 0.0
  postYawSpinScale: 1.0
  effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: soccer_ball
```

---

### `curve_soccer_ball.yml` — Curve Soccer Ball

Combines camera-based steering (standing) with hotbar-based spin control (sprinting). **Standing**: `LEFT_CLICK` uses camera pitch/yaw for lift and curve. **Sprinting**: `SPRINT` + `LEFT_CLICK` uses the active hotbar slot for spin direction. `SNEAK` + `LEFT_CLICK` plays a short backpass.

#### How to Play

**Standing kick** — `LEFT_CLICK` samples your camera 7 ticks after firing. Looking up adds lift, swiping sideways adds spin.

**Power shot** — `SPRINT` + `LEFT_CLICK` uses the active hotbar slot for spin direction:

| Hotbar Slots | Power Shot (`SPRINT_LEFT_CLICK`) |
|---|---|
| Slots 1–3 (left) | Left-curving power shot |
| Slots 4–6 (center) | Straight power shot |
| Slots 7–9 (right) | Right-curving power shot |

!!! note "Hotbar Slots"
    The hotbar slot index is 0-based in configuration (`0` = slot 1, `8` = slot 9).

| Action | Input | Effect |
|--------|-------|--------|
| Dribble / Touch | Walk into ball (`COLLIDE`) | Short forward touch with no spin (all slots) |
| Standing kick | `LEFT_CLICK` (not sprinting) | Medium-arc kick with camera-based pitch/yaw steering |
| Power shot | `SPRINT` + `LEFT_CLICK` | High-arc curved or straight power shot based on hotbar slot |
| Backpass / Cushion | `SNEAK` + `LEFT_CLICK` | Short low reverse touch (h: -0.6) — all slots |

`curveMultiplier` determines how much spin bends the flight path. Default is `0.05`; raise it for sharper curves.

#### Configuration

```yaml
name: curve_soccer_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: "1"
    durability: "3"
    displayName: null
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
    lore: null
    nbt: null
    component: null
  rotationEnabled: true
  visualVerticalOffset: -1.0
  renderDistance: 60
  slimeVisible: false
physics:
  interactionBoundsSize: 1.5
  collisionBoundsSize: 1.0
  verticalOffset: -0.3
  bounciness: 0.4
  mass: 1.0
  restVelocityThreshold: 0.01
  gravityModifier: 0.06
  airDrag: 0.002
  rollingFriction: 0.04
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
  horizontalImpulse: 0.6
  verticalImpulse: 0.2
  spinImpulse: 0.0
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
  effectName: ""
  # Standing kick — camera-based pitch/yaw steering (all slots)
- triggerType: LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 0.9
  verticalImpulse: 0.6
  spinImpulse: 0.0
  postImpulseTicks: 7
  postPitchInfluence: 0.5
  postPitchClampAngle: 0.0
  postYawSpinScale: 1.0
  effectName: ball_kick
  # Power shot — left slots → left spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 0
  conditionHotBarRangeEnd: 2
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.7
  spinImpulse: -2.0
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
  effectName: ball_kick
  # Power shot — center slots → no spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 3
  conditionHotBarRangeEnd: 5
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.7
  spinImpulse: 0.0
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
  effectName: ball_kick
  # Power shot — right slots → right spin
- triggerType: SPRINT_LEFT_CLICK
  conditionHotBarRangeStart: 6
  conditionHotBarRangeEnd: 8
  conditionGrabbedBySelf: false
  executionType: SHOOT
  horizontalImpulse: 1.0
  verticalImpulse: 0.7
  spinImpulse: 2.0
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
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
  postImpulseTicks: 0
  postPitchInfluence: 0.0
  postPitchClampAngle: 0.0
  postYawSpinScale: 0.0
  effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: curve_soccer_ball
```

---

### `hand_ball.yml` — Handball

Introduces a **grab mechanic** — pick up the ball and throw it with hotbar-controlled spin. Without the ball, clicking bumps it forward gently.

#### How to Play

**Pick up** — select **hotbar slot 6–9** and left-click the ball to trigger `GRAB`.

**Throw** — while holding the ball, `LEFT_CLICK` throws it. The active hotbar slot controls the direction:

| Hotbar Slots | Throw Direction |
|---|---|
| Slots 1–3 (left) | Left-curving throw |
| Slots 4–6 (center) | Straight throw |
| Slots 7–9 (right) | Right-curving throw |

All throws use `h: 1.3, v: 0.6`.

**Without the ball (slots 1–5)** — `LEFT_CLICK` does a gentle bump (`h: 0.3, v: 0.4`). If an opponent is carrying the ball, this knocks it loose.

**Steal from opponent (slots 6–9)** — `LEFT_CLICK` an opponent holding the ball to trigger `GRAB` and take it from them.

#### How to Play Summary

| Action | Input | Condition | Effect |
|--------|-------|-----------|--------|
| Bump | `LEFT_CLICK` (slots 1–5) | Not holding ball | Gentle forward bump; knocks ball loose from opponent |
| Grab | `LEFT_CLICK` (slots 6–9) | Not holding ball | Pick up / steal the ball |
| Throw left | `LEFT_CLICK` (slots 1–3) | Holding ball | Left-spin throw |
| Throw straight | `LEFT_CLICK` (slots 4–6) | Holding ball | Straight throw |
| Throw right | `LEFT_CLICK` (slots 7–9) | Holding ball | Right-spin throw |

#### Configuration

```yaml
name: hand_ball
render:
  modelScale: 1.0
  visualItem:
    typeName: PLAYER_HEAD,397
    amount: "1"
    durability: "3"
    displayName: null
    skinBase64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ==
    lore: null
    nbt: null
    component: null
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
    postImpulseTicks: 0
    postPitchInfluence: 0.0
    postPitchClampAngle: 0.0
    postYawSpinScale: 0.0
    effectName: ""
  # Grab — pick up the ball (slots 6–9)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 5
    conditionHotBarRangeEnd: 8
    conditionGrabbedBySelf: false
    executionType: GRAB
    horizontalImpulse: 0.0
    verticalImpulse: 0.0
    spinImpulse: 0.0
    postImpulseTicks: 0
    postPitchInfluence: 0.0
    postPitchClampAngle: 0.0
    postYawSpinScale: 0.0
    effectName: ""
  # Throw left — left spin (slots 1–3, holding ball)
  - triggerType: LEFT_CLICK
    conditionHotBarRangeStart: 0
    conditionHotBarRangeEnd: 2
    conditionGrabbedBySelf: true
    executionType: SHOOT
    horizontalImpulse: 1.3
    verticalImpulse: 0.6
    spinImpulse: -2.0
    postImpulseTicks: 0
    postPitchInfluence: 0.0
    postPitchClampAngle: 0.0
    postYawSpinScale: 0.0
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
    postImpulseTicks: 0
    postPitchInfluence: 0.0
    postPitchClampAngle: 0.0
    postYawSpinScale: 0.0
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
    postImpulseTicks: 0
    postPitchInfluence: 0.0
    postPitchClampAngle: 0.0
    postYawSpinScale: 0.0
    effectName: ball_kick
```

To use this ball in an arena:

```yaml
ballName: hand_ball
```

---

## Creating a Custom Ball

1. Create a new file in `/plugins/BlockBall/ball/`, e.g. `my_ball.yml`.
2. Set `name: my_ball` at the top.
3. Copy `render`, `physics`, and `interactions` from an existing ball as a starting point.
4. Adjust properties as needed.
5. Set `ballName: my_ball` in your arena file and reload.

!!! tip "Changing the Ball Texture"
    Find a Minecraft head texture on [minecraft-heads.com](https://minecraft-heads.com). Copy the **Value** (Base64) into `visualItem.skinBase64`.

!!! tip "Tuning Physics"
    Start with `gravityModifier`, `bounciness`, and `rollingFriction` — they have the biggest impact on feel. Small changes (~`0.01`) go a long way.
