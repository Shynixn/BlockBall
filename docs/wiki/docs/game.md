# Creating Your First Soccer Arena

This comprehensive guide will walk you through setting up a BlockBall soccer arena from start to finish. By the end, you'll have a fully functional game ready for players to enjoy!

## 🎯 Overview

Creating a BlockBall arena involves:
1. **Basic Setup** - Create the game and define play areas
2. **Team Configuration** - Set goals and spawn points  
3. **Game Activation** - Enable and test your arena
4. **Optional Enhancements** - Add advanced features

---

## ⚡ Quick Setup (Minimum Requirements)

### Step 1: Create Your Game

First, create a new arena configuration file:

```bash
/blockball create game1 My first game
```

!!! tip "Pro Tip"
    You can copy settings from an existing arena instead:
    ```bash
    /blockball copy existing_game new_game My new game
    ```

### Step 2: Verify Creation

Check that your game was created successfully:

```bash
/blockball list
```

**Expected Output:**
```
game1 [My first game] [disabled]
```

✅ **Success!** Your arena exists but is disabled (normal for new arenas).

### Step 3: Get Setup Tools

Obtain the selection tool for marking areas:

```bash
/blockball axe
```

**Enable visual highlighting** to see areas while editing:

```bash
/blockball highlight game1
```

!!! note "Visual Aid"
    Highlighting makes boundaries visible with particles/blocks, making setup much easier!

---

## 🏟️ Define Your Playing Areas

### Step 4: Mark the Playing Field

The playing field is where all soccer action happens.

1. **Select Corner A**: Left-click with the axe
2. **Select Corner B**: Right-click with the axe  
3. **Apply Selection**: Run the command

```bash
/blockball select game1 field
```

![Playing Field Selection](./assets/arena7.png)

!!! warning "Important"
    Make sure your field is large enough for players to move around comfortably!

### Step 5: Set Team Goals

Each team needs a goal area where points are scored.

#### Red Team Goal
1. Select the goal area with your axe (left-click + right-click)
2. Apply the selection:

```bash
/blockball select game1 red_goal
```

#### Blue Team Goal  
1. Select the opposite goal area
2. Apply the selection:

```bash
/blockball select game1 blue_goal
```

![Goal Selection Example](./assets/arena8.png)

### Step 6: Set Ball Spawn Point

Position yourself where you want the ball to appear when the game starts:

```bash
/blockball location game1 ball
```

!!! tip "Best Practice"
    Place the ball spawn at the center of your field for fair play.

---

## 🎮 Activate Your Game

### Step 7: Enable the Arena

Once all areas are defined, activate your game:

```bash
/blockball toggle game1
```

### Step 8: Verify Success

Confirm your arena is now active:

```bash
/blockball list
```

**Expected Output:**
```
game1 [My first game] [enabled]
```

🎉 **Congratulations!** Your basic arena is ready!

### Step 9: Test Your Arena

Players can now join in two ways:
- **Walk into the field** - Automatic join
- **Use command**: `/blockball join game1`

---

## 🔧 Enhanced Setup (Optional)

### Improve Player Experience

#### Set Leave Spawn Point
Prevent players from being teleported to random locations:

```bash
/blockball location game1 leave_spawnpoint
```

#### Configure Team Spawn Points
Give teams dedicated starting positions:

```bash
# Temporarily disable to enter the field
/blockball toggle game1

# Set team positions
/blockball location game1 red_spawnpoint
/blockball location game1 blue_spawnpoint

# Re-enable the game
/blockball toggle game1
```

---

## 🏆 Create a Minigame Mode

Transform your arena into a structured minigame with lobbies and time limits.

### Step 10: Convert to Minigame

```bash
/blockball gamerule gameType game1 minigame
```

!!! note "Error Messages"
    If you see errors, don't worry! We'll fix them in the next steps.

### Step 11: Set Lobby Spawn Points

Players need somewhere to wait before games start:

```bash
/blockball location game1 red_lobby
/blockball location game1 blue_lobby
```

### Step 12: Final Activation

Enable your completed minigame:

```bash
/blockball toggle game1
```

---

## 🎖️ Advanced Features

### Time Periods & Overtime
Configure half-times, breaks, and overtime rules by editing your `game1.yml` file in `/plugins/BlockBall/arena/`. See the [Customization Guide](customization.md) for details.

### Referee Mode (Patreon Feature)
Set up tournament-style games with full referee control. See our [Referee Guide](referee.md) for complete setup instructions.

---

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| Game won't enable | Ensure all required areas are set (field, goals, ball spawn) |
| Players can't join | Check that game is enabled and players have `blockball.join.*` permission |
| Ball doesn't appear | Verify ball spawn point is set within the playing field |
| Areas not visible | Use `/blockball highlight game1` to see boundaries |

---

## ✅ Next Steps

- **[Learn Commands](commands.md)** - Master all BlockBall commands
- **[Customize Settings](customization.md)** - Advanced configuration options  
- **[Set Permissions](permission.md)** - Control player access
- **[Add Signs](sign.md)** - Create join/leave signs for easy access

