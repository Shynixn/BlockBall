# Interactive Signs

Create user-friendly join and leave signs that allow players to interact with BlockBall arenas through simple right-clicks. This guide covers everything from basic sign creation to advanced customization.

## 🎯 Overview

BlockBall signs provide an intuitive way for players to join games without memorizing commands. Players simply right-click signs to join arenas, switch teams, or leave games.

### Sign Types Available
- **Join Signs**: Join any available team automatically
- **Leave Signs**: Exit the current game  
- **Team-Specific Signs**: Join a specific team (Red/Blue)
- **Custom Signs**: Advanced configurations for special needs

---

## 🏗️ Creating Signs

### Step-by-Step Process

#### Step 1: Place a Sign
Place any type of sign block in your world where you want players to interact.

#### Step 2: Choose Sign Type
Execute the appropriate command for your desired sign functionality:

```bash
# General join sign (auto-assigns team)
/blockballsign add blockball_join_sign arena <game_name>

# Leave game sign
/blockballsign add blockball_leave_sign arena <game_name>

# Team-specific join signs
/blockballsign add blockball_join_red_sign arena <game_name>
/blockballsign add blockball_join_blue_sign arena <game_name>
```

#### Step 3: Apply to Sign
Right-click the sign you placed. The sign will automatically update with BlockBall formatting and functionality.

### Practical Examples

```bash
# Example: Create join sign for "stadium1" arena
/blockballsign add blockball_join_sign arena stadium1

# Example: Create red team sign for tournament arena
/blockballsign add blockball_join_red_sign arena tournament_final

# Example: Create leave sign (works for any game)
/blockballsign add blockball_leave_sign arena stadium1
```

---

## 🎨 Sign Types Reference

### General Join Sign
**Command**: `/blockballsign add blockball_join_sign arena <game>`

**Functionality**: 
- Joins any available team automatically
- Balances teams when possible
- Shows current game status

**Best For**: Main entrances, lobby areas

### Leave Sign  
**Command**: `/blockballsign add blockball_leave_sign arena <game>`

**Functionality**:
- Removes player from current game
- Works regardless of which arena they're in
- Safe exit from any BlockBall game

**Best For**: Exit areas, spawn points

### Team-Specific Signs

#### Red Team Join
**Command**: `/blockballsign add blockball_join_red_sign arena <game>`

**Functionality**: 
- Joins red team specifically
- Shows red team status and availability
- May reject if team is full

#### Blue Team Join
**Command**: `/blockballsign add blockball_join_blue_sign arena <game>`

**Functionality**:
- Joins blue team specifically  
- Shows blue team status and availability
- May reject if team is full

**Best For**: Team selection areas, faction-based servers

---

## 🛠️ Sign Management

### Removing Signs
Simply break the sign with your hand or any tool. The BlockBall functionality will be automatically removed.

### Editing Signs
To modify an existing sign:
1. **Break the current sign**
2. **Place a new sign** in the desired location
3. **Run the appropriate command** again
4. **Right-click the new sign** to apply

### Bulk Sign Creation
For large projects, you can create multiple signs efficiently:

```bash
# Create multiple join signs for the same arena
/blockballsign add blockball_join_sign arena stadium1
# Right-click first sign
# Right-click second sign  
# Right-click third sign...
```

!!! tip "Efficiency Tip"
    After running a sign command, you can right-click multiple signs to apply the same configuration to all of them.

---

## 🎨 Sign Customization

### Configuration Files

BlockBall uses an internal version of [ShyCommandSigns](https://shynixn.github.io/ShyCommandSigns/wiki/site/installation/) for sign functionality. You can customize appearance and behavior through configuration files.

**Configuration Location**: `/plugins/BlockBall/sign/`

### Basic Customization

#### Edit Sign Text
Modify the sign templates in the configuration folder:

```yaml
# Example: /plugins/BlockBall/sign/blockball_join_sign.yml
template:
  line1: "&6[BlockBall]"
  line2: "%arena_displayName%"
  line3: "&7Click to Join!"
  line4: "&a%arena_players%/%arena_maxPlayers%"
```

#### Apply Changes
After editing configuration files:
```bash
/blockballsign reload
# OR
/blockball reload
```

### Advanced Customization Options

#### Dynamic Sign Content
```yaml
# Show different text based on game state
template:
  line1: "&6[BlockBall]"
  line2: "%arena_displayName%"
  line3: "%arena_state_display%"
  line4: "&7%arena_players%/%arena_maxPlayers%"

# Custom state messages
states:
  JOINABLE: "&aClick to Join!"
  RUNNING: "&eGame in Progress"
  DISABLED: "&cArena Closed"
```

#### Team-Specific Styling
```yaml
# Red team sign customization
red_team_template:
  line1: "&c&l[RED TEAM]"
  line2: "%arena_displayName%"
  line3: "&7Join the Fire!"
  line4: "&c%red_players%/%red_maxPlayers% players"
  
# Blue team sign customization  
blue_team_template:
  line1: "&9&l[BLUE TEAM]"
  line2: "%arena_displayName%"
  line3: "&7Join the Water!"
  line4: "&9%blue_players%/%blue_maxPlayers% players"
```

#### Interactive Effects
```yaml
# Add particle effects and sounds
effects:
  click_sound: "entity.experience_orb.pickup"
  success_sound: "entity.player.levelup"
  failure_sound: "entity.villager.no"
  
particles:
  success: "VILLAGER_HAPPY"
  failure: "VILLAGER_ANGRY"
```

---

## 📍 Strategic Sign Placement

### Lobby Design

#### Central Hub Layout
```
    [Leave]
      |
[Red] [Join] [Blue]
      |
   [Arena Info]
```

#### Multiple Arena Setup
```
[Stadium 1] [Stadium 2] [Stadium 3]
[Join Red]  [Join Blue] [Leave All]
```

### Arena Integration

#### At Arena Entrance
- **Join sign**: Main entry point
- **Team selection**: Red and Blue options
- **Information**: Arena rules or status

#### Inside Arena
- **Leave signs**: Multiple exit points
- **Team switch**: Allow mid-game team changes (if enabled)

#### Spawn Area Integration
- **Return signs**: Easy access back to arenas
- **Status displays**: Show all arena states

---

## 🔧 Advanced Sign Features

### Multi-Arena Signs

Create signs that work with multiple arenas:

```yaml
# Configuration for multi-arena sign
multi_arena_sign:
  arenas:
    - stadium1
    - stadium2  
    - tournament
  behavior: "join_first_available"
  template:
    line1: "&6[BlockBall]"
    line2: "&7Quick Join"
    line3: "&aAny Arena"
    line4: "&7Click to play!"
```

### Conditional Signs

Signs that appear only under certain conditions:

```yaml
# VIP-only signs
vip_sign:
  permission: "blockball.vip"
  template:
    line1: "&6&l[VIP]"
    line2: "&7Exclusive Arena"
    line3: "&eVIP Members Only"
    line4: "&7Click to join!"
```

### Animated Signs

Create dynamic, updating signs:

```yaml
# Animated sign content
animated_join:
  animation:
    frames:
      - "&6>>> &lJOIN &6<<<"
      - "&e>>> &lJOIN &e<<<"
      - "&a>>> &lJOIN &a<<<"
    speed: 20  # ticks between frames
```

---

## 📊 Sign Integration Examples

### Scoreboard Integration
```yaml
# Sign that shows live match data
live_scoreboard_sign:
  template:
    line1: "%arena_displayName%"
    line2: "&c%red_score% &7- &9%blue_score%"
    line3: "&7Time: %time_remaining%"
    line4: "&7Click to spectate"
  update_interval: 20  # Update every second
```

### Economy Integration
```yaml
# Pay-to-play sign
premium_join:
  cost: 100
  template:
    line1: "&6[Premium Game]"
    line2: "&7Cost: $100"
    line3: "&aClick to pay & join"
    line4: "%arena_players%/%arena_maxPlayers%"
```

### Permission-Based Access
```yaml
# Different signs for different ranks
rank_based_signs:
  member:
    permission: "server.member"
    arenas: ["casual1", "casual2"]
  vip:
    permission: "server.vip"  
    arenas: ["vip_arena", "premium_stadium"]
  admin:
    permission: "server.admin"
    arenas: ["all"]
```

---

## 🔍 Troubleshooting

### Common Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| Sign doesn't work | Command not executed properly | Re-run the sign command and right-click again |
| Wrong text displaying | Configuration not reloaded | Use `/blockballsign reload` |
| Players can't click | Missing permissions | Check player has `blockball.command` |
| Sign breaks functionality | Incorrect arena name | Verify arena exists with `/blockball list` |

### Testing Signs

#### Verify Sign Functionality
1. **Check arena exists**: `/blockball list`
2. **Test with admin**: Right-click as administrator
3. **Check permissions**: Ensure players have required permissions
4. **Monitor console**: Look for error messages

#### Debug Information
```bash
# Test arena accessibility
/blockball join <arena_name>

# Check player permissions
/lp user <player> permission check blockball.command

# Verify sign configuration
# Check files in /plugins/BlockBall/sign/
```

---

## 💡 Best Practices

### Design Guidelines

!!! success "User Experience"
    - **Clear labeling**: Make sign purposes obvious
    - **Consistent styling**: Use the same color scheme across signs
    - **Strategic placement**: Put signs where players naturally look
    - **Provide feedback**: Use sounds and particles for interactions

!!! tip "Performance"
    - **Limit updates**: Don't update signs too frequently
    - **Group similar signs**: Use consistent templates to reduce file size
    - **Monitor usage**: Remove unused signs to reduce server load

### Maintenance

!!! warning "Regular Checks"
    - **Test functionality**: Verify signs work after plugin updates
    - **Update designs**: Refresh sign appearance for events or seasons
    - **Check permissions**: Ensure player access remains correct
    - **Monitor logs**: Watch for sign-related errors

### Security

!!! info "Access Control"
    - **Limit sign creation**: Only allow trusted staff to create signs
    - **Validate arenas**: Ensure signs point to existing, working arenas
    - **Permission boundaries**: Don't give players access beyond their rank

This sign system provides a professional, user-friendly interface that makes BlockBall accessible to all players regardless of their command knowledge!



