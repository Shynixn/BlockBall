# Advanced Interactions & Integration

BlockBall provides extensive customization options for player interactions, permissions, and integration with other plugins. This guide covers everything from basic command restrictions to complex permission-based workflows.

## 🎯 Overview

While BlockBall includes built-in settings for common restrictions (commands, block breaking, game modes), you can create sophisticated interaction systems using:

- **Custom Commands**: Execute actions when players join/leave games
- **Permission-Based Control**: Dynamic permission changes during gameplay  
- **Region Integration**: Area-based restrictions and protections
- **External Plugin Hooks**: Integration with economy, chat, and other systems

---

## 🔧 Custom Command Execution

### Basic Command Setup

Execute custom commands automatically when players join or leave games by editing your arena configuration file:

**File Location**: `/plugins/BlockBall/arena/<arena_name>.yml`

```yaml
# Commands executed when players join
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/say Welcome %blockball_player_name% to the arena!'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode adventure %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/experience add %blockball_player_name% 1'

# Commands executed when players leave  
leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/say Goodbye %blockball_player_name%!'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode survival %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/heal %blockball_player_name%'
```

### Command Types

| Type | Description | Use Case |
|------|-------------|----------|
| `SERVER_PER_PLAYER` | Execute with server permissions | Commands players don't have permission for |
| `PER_PLAYER` | Execute with player permissions | Commands the player could run themselves |

!!! tip "Permission Levels"
    Use `SERVER_PER_PLAYER` for administrative commands (gamemode, teleport, etc.) and `PER_PLAYER` for actions the player should be able to do themselves.

### Team-Specific Commands

Configure different commands for each team:

```yaml
# Red team join commands
teams:
  red:
    joinCommands:
      - type: 'SERVER_PER_PLAYER'
        command: '/kit red_team %blockball_player_name%'
      - type: 'SERVER_PER_PLAYER'  
        command: '/effect give %blockball_player_name% strength 999999 0'
    leaveCommands:
      - type: 'SERVER_PER_PLAYER'
        command: '/clear %blockball_player_name%'

# Blue team join commands  
  blue:
    joinCommands:
      - type: 'SERVER_PER_PLAYER'
        command: '/kit blue_team %blockball_player_name%'
      - type: 'SERVER_PER_PLAYER'
        command: '/effect give %blockball_player_name% speed 999999 0'
```

---

## 🛡️ Permission-Based Interaction Control

### Dynamic Permission Management

The most powerful way to control player behavior during games is through dynamic permission changes using a permission plugin like **LuckPerms**.

#### Setup Process

1. **Create a BlockBall permission group**
2. **Configure group permissions** 
3. **Add/remove players from group** via join/leave commands
4. **Apply game-specific restrictions**

#### Step-by-Step LuckPerms Integration

**Step 1: Create Permission Group**
```bash
/luckperms creategroup blockball_ingame
```

**Step 2: Configure Group Permissions**
```bash
# Prevent block breaking during games
/luckperms group blockball_ingame permission set worldguard.region.bypass.* false

# Allow only specific commands
/luckperms group blockball_ingame permission set essentials.spawn false
/luckperms group blockball_ingame permission set essentials.home false

# Enable game-specific features
/luckperms group blockball_ingame permission set blockball.shyscoreboard.scoreboard.game true
```

**Step 3: Arena Configuration**
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent add blockball_ingame'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode adventure %blockball_player_name%'

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent remove blockball_ingame'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode survival %blockball_player_name%'
```

### Advanced Permission Scenarios

#### Tournament Mode Setup
```yaml
# Tournament-specific permissions
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent add tournament_participant'
  - type: 'SERVER_PER_PLAYER'
    command: '/mute %blockball_player_name% global'  # Prevent global chat
  - type: 'SERVER_PER_PLAYER'
    command: '/vanish %blockball_player_name% off'   # Ensure visibility

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent remove tournament_participant'
  - type: 'SERVER_PER_PLAYER'
    command: '/unmute %blockball_player_name% global'
```

#### VIP Experience Enhancement
```yaml
# VIP players get special treatment
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if entity %blockball_player_name%[tag=vip] run effect give %blockball_player_name% jump_boost 999999 1'
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if entity %blockball_player_name%[tag=vip] run title %blockball_player_name% subtitle "VIP Perks Active!"'
```

---

## 🏰 Region-Based Interactions

### WorldGuard Integration

Protect your arenas and control player behavior using WorldGuard regions:

#### Basic Arena Protection
```bash
# Create region around arena
/region define blockball_stadium1 

# Set region flags
/region flag blockball_stadium1 pvp allow
/region flag blockball_stadium1 build deny
/region flag blockball_stadium1 chest-access deny
/region flag blockball_stadium1 use deny
```

#### Dynamic Region Management
```yaml
# Add player to region on join
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/region addmember blockball_stadium1 %blockball_player_name%'

# Remove player from region on leave
leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/region removemember blockball_stadium1 %blockball_player_name%'
```

### GriefPrevention Integration
```yaml
# Grant temporary build rights in arena claim
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/trust %blockball_player_name%'

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/untrust %blockball_player_name%'
```

---

## 🔗 Plugin Integration Examples

### Economy Integration (Vault)

#### Entry Fee System
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/eco take %blockball_player_name% 100'
  - type: 'SERVER_PER_PLAYER'
    command: '/tell %blockball_player_name% Entry fee of $100 charged!'

# Winner reward (use with goal/win events)
winCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/eco give %blockball_player_name% 500'
```

#### Performance-Based Rewards
```yaml
# Reward system based on actions
goalCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/eco give %blockball_player_name% 50'
  - type: 'SERVER_PER_PLAYER'
    command: '/broadcast %blockball_player_name% scored and earned $50!'
```

### Chat Plugin Integration

#### Team-Only Chat
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/channel join team_%blockball_player_team%'
  - type: 'SERVER_PER_PLAYER'
    command: '/channel focus team_%blockball_player_team%'

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/channel leave team_%blockball_player_team%'
  - type: 'SERVER_PER_PLAYER'
    command: '/channel focus global'
```

#### Match Announcements
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/broadcast %blockball_player_name% joined %blockball_game_displayName% on %blockball_player_teamDisplayName%!'

goalCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/discord broadcast **GOAL!** %blockball_player_name% scored for %blockball_player_teamDisplayName%!'
```

### Statistics Integration

#### PlayerPoints Integration
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/points take %blockball_player_name% 5'

goalCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/points give %blockball_player_name% 10'

winCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/points give %blockball_player_name% 25'
```

---

## 🎨 Advanced Customization Examples

### Seasonal Events
```yaml
# Halloween theme
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/effect give %blockball_player_name% invisibility 10 0'
  - type: 'SERVER_PER_PLAYER'
    command: '/summon bat ~ ~ ~ {Tags:["blockball_decoration"]}'
  - type: 'SERVER_PER_PLAYER'
    command: '/playsound entity.witch.ambient master %blockball_player_name%'
```

### Skill-Based Matching
```yaml
# Check player skill level and adjust accordingly
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if score %blockball_player_name% skill matches 1..10 run effect give %blockball_player_name% speed 999999 1'
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if score %blockball_player_name% skill matches 90..100 run effect give %blockball_player_name% slowness 999999 0'
```

### Multi-Language Support
```yaml
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if entity %blockball_player_name%[tag=lang_spanish] run tellraw %blockball_player_name% {"text":"¡Bienvenido al juego!","color":"green"}'
  - type: 'SERVER_PER_PLAYER'
    command: '/execute if entity %blockball_player_name%[tag=lang_english] run tellraw %blockball_player_name% {"text":"Welcome to the game!","color":"green"}'
```

---

## 🔧 Troubleshooting & Best Practices

### Common Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| Commands not executing | Wrong command type | Use `SERVER_PER_PLAYER` for admin commands |
| Permission errors | Missing plugin permissions | Grant server permission to execute commands |
| Players stuck in game state | Leave commands failed | Add fallback commands and manual cleanup |
| Performance issues | Too many commands | Optimize and combine commands where possible |

### Best Practices

!!! success "Performance Tips"
    - **Minimize command count**: Combine multiple effects into single commands when possible
    - **Use conditional execution**: Only run commands when necessary using `/execute if`
    - **Test thoroughly**: Always test command sequences before deploying

!!! warning "Security Considerations"
    - **Validate player input**: Be careful with placeholder usage in commands
    - **Limit command scope**: Don't give players unintended permissions
    - **Monitor execution**: Log important command executions for debugging

!!! info "Integration Guidelines"
    - **Check plugin compatibility**: Ensure your plugins work together
    - **Plan your workflow**: Design the player experience before implementing
    - **Document your setup**: Keep notes on complex command interactions

---

## 📚 Configuration Templates

### Complete Tournament Setup
```yaml
# Tournament arena with full integration
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent add tournament_player'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode adventure %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/region addmember tournament_arena %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/eco take %blockball_player_name% 50'
  - type: 'SERVER_PER_PLAYER'
    command: '/channel join tournament'

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent remove tournament_player'
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode survival %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/region removemember tournament_arena %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/heal %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/channel leave tournament'
```

### Casual Server Setup
```yaml
# Simple casual game setup
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode adventure %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/effect give %blockball_player_name% saturation 999999 255'

leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode survival %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/effect clear %blockball_player_name%'
  - type: 'SERVER_PER_PLAYER'
    command: '/heal %blockball_player_name%'
```

This interaction system gives you complete control over the player experience, allowing you to create anything from simple casual games to complex tournament environments with full plugin integration!
