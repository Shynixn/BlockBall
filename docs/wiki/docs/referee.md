# Referee System (Patreon Feature)

The BlockBall referee system transforms your server into a professional soccer venue with complete match control. Perfect for tournaments, events, and competitive gameplay where you need human oversight and precise game management.

!!! info "Premium Feature"
    The referee system is exclusive to **Patreon supporters** who have funded this advanced feature. [Support development](https://www.patreon.com/Shynixn) to access these professional-grade tools! ❤️

## 🎯 What is Referee Mode?

Referee mode gives a designated admin or moderator complete control over match flow using specialized commands. Instead of automatic game progression, referees manage every aspect of the game manually.

### Key Capabilities
- **Match Control**: Start, pause, and stop games at will
- **Ball Management**: Place the ball anywhere on the field
- **Time Control**: Freeze time, skip periods, manage countdowns
- **Player Discipline**: Issue yellow/red cards and manage ejections
- **Professional Feel**: Creates authentic tournament atmosphere

### Perfect For
- **Server Events**: Special tournaments and competitions
- **Streaming**: Controlled matches for content creation
- **Training**: Teaching new players proper soccer mechanics
- **Competitions**: Prize tournaments requiring human oversight

---

## 🏗️ Setup Guide

### Prerequisites

Before setting up referee mode, ensure you have:
1. **Patreon subscription** for BlockBall
2. **Working minigame arena** (complete the [basic setup](game.md) first)
3. **Proper permissions** configured for referees

### Step-by-Step Setup

#### Step 1: Create a Standard Minigame Arena

!!! warning "Required Foundation"
    You **must** start with a working minigame arena before converting to referee mode. Follow the [game creation guide](game.md) completely first.

Ensure your arena has all required elements:
- ✅ Playing field defined
- ✅ Both team goals set
- ✅ Ball spawn point configured
- ✅ Team spawn points established
- ✅ Lobby spawn points configured
- ✅ Arena enabled and tested

#### Step 2: Set Referee Positions

**Field Referee Position**
Position yourself where the referee should spawn during active gameplay:
```bash
/blockball location game1 referee_spawnpoint
```

**Lobby Referee Position**  
Move to where the referee should wait during lobby phase:
```bash
/blockball location game1 referee_lobby
```

!!! tip "Positioning Tips"
    - **Field position**: Center of field or sideline for best visibility
    - **Lobby position**: Elevated platform or special area separate from players

#### Step 3: Convert to Referee Game

Transform your minigame into a referee-controlled match:
```bash
/blockball gamerule gameType game1 refereegame
```

#### Step 4: Enable and Test

Activate your referee arena:
```bash
/blockball toggle game1
```

Verify the conversion worked:
```bash
/blockball list
```

**Expected Output:**
```
game1 [My first game] [enabled] [refereegame]
```

---

## 🎮 Match Management

### Starting a Match

#### Player Join Process
Players join normally using existing methods:
```bash
/blockball join game1 red
/blockball join game1 blue
```

!!! important "Referee Required"
    The lobby countdown will **NOT** start until a referee joins the game.

#### Referee Join
A qualified admin joins as referee:
```bash
/blockball join game1 referee
```

**Required Permission**: `blockball.referee.join`

#### Match Initiation
Once ready, the referee starts the game:
```bash
/blockball referee startgame
```

This begins the lobby countdown and transitions to active gameplay.

---

## 🎯 Referee Commands Reference

### Game Flow Control

| Command | Purpose | Usage |
|---------|---------|-------|
| `/blockball referee startgame` | Begin lobby countdown | Use when all players are ready |
| `/blockball referee stop` | End current period/game | Transitions to final period on first use, stops completely on second |
| `/blockball referee nextperiod` | Skip to next configured period | Useful for half-time transitions |

### Match State Management

| Command | Purpose | Effect |
|---------|---------|--------|
| `/blockball referee whistleresume` | Resume active play | Ball becomes interactable, time resumes |
| `/blockball referee whistlestop` | Pause active play | Ball becomes inactive, players can't interact |
| `/blockball referee freezetime` | Freeze game timer | Stops countdown while keeping ball inactive |

### Ball Positioning

| Command | Purpose | Parameters |
|---------|---------|------------|
| `/blockball referee setball [x] [y] [z] [yaw] [pitch] [world]` | Teleport ball to exact coordinates | Optional: specify exact position |
| `/blockball referee setballrel [forward] [sideward]` | Position ball relative to referee | Forward/sideward distance from referee |

**Examples:**
```bash
# Place ball at referee's feet
/blockball referee setball

# Position ball 5 blocks forward, 2 blocks right
/blockball referee setballrel 5 2

# Exact coordinates for penalty kicks
/blockball referee setball 100 65 200 0 0 world
```

---

## 🟨🟥 Player Discipline System

### Card Management

Issue cards to track player behavior and infractions:

```bash
# Yellow card for minor infractions
/blockball referee yellowcard <player>

# Red card for major infractions  
/blockball referee redcard <player>
```

### Player Ejection

Remove disruptive players from the match:
```bash
/blockball referee kickplayer <player>
```

!!! note "Manual Tracking"
    The referee system displays cards but doesn't automatically eject players for accumulating cards. Referees must manually track infractions and eject players as needed.

### Card Display Integration

Show player cards on scoreboards by editing your scoreboard configuration:

**File**: `/plugins/BlockBall/scoreboard/blockball_scoreboard.yml`

```yaml
# Add to scoreboard lines
lines:
  - "Cards: %blockball_player_cardDisplay%"
  # Or show individual counts
  - "%blockball_player_redCards% Red, %blockball_player_yellowCards% Yellow"
```

**Visual Examples:**
- `🟨🟨🟥` - Two yellow cards, one red card
- `2 Red, 1 Yellow` - Numerical display

---

## 🔧 Advanced Referee Features

### Whistle Sound Integration

Create an authentic referee experience with whistle sounds:

#### Setup Process
1. **Install a Command Items plugin** (search "Command Items" on SpigotMC)
2. **Bind referee commands to items** in your inventory
3. **Add whistle sounds** using `/playsound` commands  
4. **Set referee inventory** to include these items automatically

#### Example Configuration
```yaml
# In your command items plugin config
referee_whistle:
  item: "TRIPWIRE_HOOK"
  name: "&6Referee Whistle"
  commands:
    - "/playsound entity.player.levelup master @a ~ ~ ~ 1.0 2.0"
    - "/blockball referee whistleresume"

referee_stop:
  item: "BARRIER"  
  name: "&cStop Play"
  commands:
    - "/playsound block.note_block.bass master @a ~ ~ ~ 1.0 0.5"
    - "/blockball referee whistlestop"
```

#### Apply to Referee Inventory
```bash
# Give yourself the items, then save as referee inventory
/blockball inventory game1 referee
```

### Custom Referee Announcements

Enhance the experience with professional announcements:

```yaml
# Add to arena configuration
refereeCommands:
  goalScored:
    - type: 'SERVER_PER_PLAYER'
      command: '/playsound entity.firework_rocket.blast master @a'
    - type: 'SERVER_PER_PLAYER'  
      command: '/broadcast &6GOAL! &f%blockball_player_name% scores for %blockball_player_teamDisplayName%!'
      
  cardIssued:
    - type: 'SERVER_PER_PLAYER'
      command: '/playsound block.anvil.land master @a'
    - type: 'SERVER_PER_PLAYER'
      command: '/broadcast &e%blockball_player_name% receives a card from the referee!'
```

---

## 🏆 Professional Tournament Setup

### Complete Referee Arena Configuration

```yaml
# Advanced referee arena example
arena:
  name: "tournament_final"
  displayName: "&6&lTournament Final"
  
game:
  type: "REFEREEGAME"
  maxScore: 2
  periods:
    - duration: 2700  # 45 minutes first half
    - duration: 900   # 15 minute break  
    - duration: 2700  # 45 minutes second half
    - duration: 1800  # 30 minutes extra time if tied
    
teams:
  red:
    displayName: "&c&lTeam Alpha"
    maxPlayers: 11
  blue:
    displayName: "&9&lTeam Beta"  
    maxPlayers: 11
  referee:
    displayName: "&7&lMatch Officials"
    
# Professional match commands
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode spectator %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/effect give %blockball_player_name% night_vision 999999 0'
    
# Reset players after match  
leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode survival %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/effect clear %blockball_player_name%'
```

### Match Day Workflow

1. **Pre-Match Setup**
   - Referees join and verify arena state
   - Test whistle commands and ball positioning
   - Confirm scoreboard and announcement systems

2. **Player Registration**
   - Teams join their respective sides
   - Referee verifies team compositions
   - Final equipment and rules briefing

3. **Match Control**
   - Referee starts official countdown
   - Manage play using whistle commands
   - Issue cards and handle infractions as needed
   - Control ball placement for set pieces

4. **Post-Match**
   - Official result announcement
   - Player statistics summary
   - Cleanup and arena reset

---

## 🔍 Troubleshooting

### Common Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| Lobby won't start | No referee joined | Ensure referee has joined before starting |
| Commands not working | Missing permissions | Grant `blockball.referee.join` permission |
| Ball not responding | Incorrect game state | Use whistle commands to control ball state |
| Cards not displaying | Scoreboard not configured | Add card placeholders to scoreboard config |

### Best Practices

!!! success "Referee Guidelines"
    - **Communicate clearly**: Use chat to explain decisions to players
    - **Be consistent**: Apply rules fairly to both teams
    - **Stay neutral**: Maintain impartial positioning and decisions
    - **Know the commands**: Practice referee commands before live matches

!!! tip "Technical Tips"  
    - **Test everything**: Run practice matches before important events
    - **Have backups**: Train multiple referees for coverage
    - **Document procedures**: Create referee handbooks for your server
    - **Monitor performance**: Watch for lag or command delays during matches

---

## 📚 Further Resources

### Training New Referees
- Create practice arenas for referee training
- Document your server's specific rules and procedures  
- Hold practice matches with experienced referees mentoring new ones

### Event Integration
- Coordinate with tournament brackets and scheduling
- Set up streaming overlays with referee information
- Create spectator areas and camera positions

### Community Building
- Establish referee certification programs
- Create referee uniforms using custom player skins
- Develop referee ranking and recognition systems

The referee system transforms BlockBall from a simple game into a professional soccer experience, perfect for creating memorable tournaments and competitive events on your server!






