# Command Reference

BlockBall provides a comprehensive set of commands for arena management, gameplay, and administration. All commands require the appropriate permissions.

!!! tip "Getting Help"
    Access the in-game help system anytime:
    ```bash
    /blockball help 1
    ```

---

## 🏗️ Arena Management Commands

### Create & Copy Arenas

#### `/blockball create`
Creates a new arena from scratch.

```bash
/blockball create <name> <displayName>
```

**Parameters:**
- `name` - Unique identifier for the arena (no spaces)
- `displayName` - User-friendly name (supports color codes)

**Example:**
```bash
/blockball create stadium1 &6Golden Stadium
```

#### `/blockball copy`
Duplicates an existing arena with all its settings.

```bash
/blockball copy <source> <name> <displayName>
```

**Parameters:**
- `source` - Name of existing arena to copy
- `name` - New arena identifier  
- `displayName` - Display name for the new arena

**Example:**
```bash
/blockball copy stadium1 stadium2 &bBlue Stadium
```

### Arena Control

#### `/blockball delete`
Permanently removes an arena and its configuration.

```bash
/blockball delete <name>
```

!!! warning "Destructive Action"
    This action cannot be undone. Make sure to backup your arena files if needed.

#### `/blockball list`
Shows all arenas and their current status.

```bash
/blockball list [player]
```

**Output Example:**
```
stadium1 [Golden Stadium] [enabled]
arena2 [Practice Field] [disabled]
```

#### `/blockball toggle`
Enables or disables an arena.

```bash
/blockball toggle <name>
```

- **Enabled**: Players can join and play
- **Disabled**: Arena is inaccessible to players

---

## 🎮 Player Commands

### Joining & Leaving

#### `/blockball join`
Join a specific game, optionally choosing a team.

```bash
/blockball join <name> [team] [player]
```

**Parameters:**
- `name` - Arena name to join
- `team` - `red`, `blue`, or `referee` (optional)
- `player` - Target player (admin only)

**Examples:**
```bash
/blockball join stadium1
/blockball join stadium1 red
/blockball join stadium1 blue Steve
```

#### `/blockball leave`
Leave the current game.

```bash
/blockball leave [player]
```

---

## 🔧 Setup & Configuration Commands

### Selection Tools

#### `/blockball axe`
Get the selection tool for marking areas.

```bash
/blockball axe
```

**Usage:**
- **Left-click**: Select first corner (Point A)
- **Right-click**: Select second corner (Point B)

#### `/blockball select`
Apply your axe selection to define arena areas.

```bash
/blockball select <name> <type>
```

**Area Types:**
- `field` - Main playing area
- `red_goal` - Red team's goal
- `blue_goal` - Blue team's goal

**Example:**
```bash
/blockball select stadium1 field
```

#### `/blockball location`
Set specific points using your current position.

```bash
/blockball location <name> <type>
```

**Location Types:**
- `ball` - Where the ball spawns
- `red_spawnpoint` - Red team spawn
- `blue_spawnpoint` - Blue team spawn  
- `red_lobby` - Red team lobby (minigames)
- `blue_lobby` - Blue team lobby (minigames)
- `referee_spawnpoint` - Referee position
- `referee_lobby` - Referee lobby
- `leave_spawnpoint` - Where players go when leaving

### Visual Aids

#### `/blockball highlight`
Toggle visual boundaries for easier arena editing.

```bash
/blockball highlight <name>
```

Shows particle effects or blocks around defined areas.

---

## ⚙️ Customization Commands

### Team Equipment

#### `/blockball inventory`
Copy your current inventory as the team's default gear.

```bash
/blockball inventory <name> <team>
```

**Teams:** `red` or `blue`

#### `/blockball armor`
Copy your current armor as the team's default equipment.

```bash
/blockball armor <name> <team>
```

### Game Rules

#### `/blockball gamerule`
Modify arena settings and game mechanics.

```bash
/blockball gamerule <key> <name> <value>
```

**Common Game Rules:**
- `gameType` - `hubgame`, `minigame`, or `refereegame`
- `maxScore` - Points needed to win
- `duration` - Game length in seconds

**Example:**
```bash
/blockball gamerule gameType stadium1 minigame
```

---

## 🛡️ Referee Commands (Patreon Feature)

Control match flow during tournaments and events.

### Game Control

| Command | Description |
|---------|-------------|
| `/blockball referee startgame` | Start lobby countdown |
| `/blockball referee stop` | End current period/game |
| `/blockball referee whistleresume` | Resume play, enable ball |
| `/blockball referee whistlestop` | Pause game, disable ball |
| `/blockball referee freezetime` | Freeze timer and ball |
| `/blockball referee nextperiod` | Move to next game period |

### Ball Control

| Command | Description |
|---------|-------------|
| `/blockball referee setball [x] [y] [z] [yaw] [pitch] [world]` | Teleport ball to position |
| `/blockball referee setballrel [forward] [sideward]` | Set ball relative to referee |

### Player Management

| Command | Description |
|---------|-------------|
| `/blockball referee yellowcard <player>` | Give yellow card |
| `/blockball referee redcard <player>` | Give red card |
| `/blockball referee kickplayer <player>` | Remove player from game |

---

## 🏷️ Signs & Integration

#### `/blockball sign`
Create interactive signs for easy player access.

```bash
/blockball sign <name> <type>
```

**Sign Types:**
- `join` - Join any available team
- `leave` - Leave current game
- `team_red` - Join red team specifically  
- `team_blue` - Join blue team specifically

**Usage:**
1. Run the command
2. Right-click any sign to convert it

---

## 🔧 System Commands

#### `/blockball placeholder`
Test placeholder values for scoreboards and displays.

```bash
/blockball placeholder <placeholder>
```

**Example:**
```bash
/blockball placeholder %blockball_game_redScore%
```

#### `/blockball reload`
Reload arena configurations without restarting.

```bash
/blockball reload [name]
```

- **No name**: Reload all arenas
- **With name**: Reload specific arena

---

## 💡 Pro Tips

!!! tip "Command Efficiency"
    - Use tab completion to see available options
    - Most commands support partial arena name matching
    - Commands are case-insensitive for convenience

!!! warning "Permission Requirements"
    - Regular players need `blockball.command` for basic commands
    - Arena editing requires `blockball.edit`
    - Referee commands need `blockball.referee.join`

!!! info "Admin Features"
    Many commands accept a player parameter when you have admin permissions, allowing you to manage other players' arena participation.
