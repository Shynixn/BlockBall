# Spawning Balls Outside of Games

BlockBall can place a ball in your world without starting a game. This is perfect for lobbies, hubs, or free-play areas where players should be able to kick a ball around while waiting.

---

## Setting Up a Spawn

Every ball spawn lives in its own file in the `spawns` folder:

```text
/plugins/BlockBall/spawns/<spawn_name>.yml
```

A sample file named `hub_soccer_ball.yml` is created for you the first time the plugin starts. To add another spawn, copy that file, give it a new name, and adjust the values inside.

!!! info "As many as you need"
    You can create as many spawn files as you like. Each one needs its own unique `name`, and the plugin picks up every file in the folder automatically.

---

## Configuring a Spawn

Open one of the files in the `spawns` folder with a text editor:

```yaml
# Unique identifier for this spawn.
name: hub_soccer_ball
# Ball id to spawn.
ballName: "soccer_ball"
# Set this to true to enable this spawn. If set to false, the spawn will be ignored.
enabled: false
# If the ball is not touched after 30 seconds, it will despawn automatically.
removeAfterSeconds: 30
# If a player is in the radius of the spawn, the ball will spawn.
spawnRadius: 50
# Spawn point of the ball. The ball will spawn at this location.
spawnpoint:
  world: "world"
  x: 0.0
  y: 0.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0
```

| Setting | What it does |
|---------|--------------|
| `name` | A unique name for this spawn, so you can tell your spawns apart. |
| `ballName` | Which ball to use. Enter the name of a ball from the `ball` folder, for example `soccer_ball`. |
| `enabled` | Set this to `true` to turn the spawn on. While it is `false`, nothing is spawned. |
| `removeAfterSeconds` | How many seconds the ball stays in the world after the last time a player touched it. |
| `spawnRadius` | How close a player must be for the ball to appear, in blocks. |
| `spawnpoint` | The world and position where the ball appears. `yaw` and `pitch` control its rotation. |

To activate the spawn, change `enabled` to `true` and set `spawnpoint` to the place where the ball should appear.

After editing or adding spawn files, reload the plugin:

```bash
/blockball reload
```