# Permission System

BlockBall uses a comprehensive permission system to control player access to features and commands. This guide helps you configure permissions correctly for different user roles on your server.

## 🎯 Permission Levels Overview

BlockBall permissions are organized into three main levels:

### 🟢 User Level
**Who gets this**: All players who should be able to play BlockBall  
**Purpose**: Basic gameplay functionality

### 🟡 Admin/User Level  
**Who gets this**: Trusted players or staff members  
**Purpose**: Enhanced features that could be abused if given to everyone  
**Caution**: Review carefully before granting to regular players

### 🔴 Admin Level
**Who gets this**: Server administrators and staff only  
**Purpose**: Arena management and server administration

---

## 🚀 Quick Setup (Recommended Permissions)

For most servers, these permissions provide the best balance of functionality and security:

### Essential Player Permissions
```yaml
# Basic BlockBall access (required for all players)
- blockball.command
- blockball.join.*

# Display permissions (recommended for all players)  
- blockball.shyscoreboard.scoreboard.*
- blockball.shybossbar.bossbar.*
- blockball.shyparticles.effect.visible.*
```

### Admin Permissions
```yaml
# Arena management (staff only)
- blockball.edit
- blockball.referee.join

# System administration (staff only)
- blockball.shyscoreboard.command
- blockball.shyscoreboard.reload
- blockball.shybossbar.command  
- blockball.shybossbar.reload
- blockball.shycommandsigns.command
- blockball.shycommandsigns.add
- blockball.shycommandsigns.reload
- blockball.shyparticles.command
- blockball.shyparticles.reload
- blockball.shyparticles.list
- blockball.shyparticles.play
- blockball.shyparticles.stop
- blockball.shyparticles.follow
- blockball.shyparticles.followother
- blockball.shyparticles.stopfollow
- blockball.shyparticles.stopfollowother
```

---

## 📋 Complete Permission Reference

### Core Gameplay Permissions

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.command` | **User** | Access to `/blockball` command base |
| `blockball.join.*` | **User** | Join any arena (requires `blockball.command`) |
| `blockball.join.[arena_name]` | **User** | Join specific arena only |

**Example Arena-Specific Permissions:**
```yaml
# Allow joining only "stadium1" arena
- blockball.join.stadium1

# Allow joining multiple specific arenas  
- blockball.join.stadium1
- blockball.join.arena2
- blockball.join.tournament
```

### Administrative Permissions

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.edit` | **Admin** | Create, modify, and delete arenas |
| `blockball.referee.join` | **Admin** | Access referee mode and commands |

### Enhanced Player Permissions

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.command.staff` | **Admin/User** | Execute commands while in-game |
| `blockball.game.inventory` | **Admin/User** | Open/use inventories during games |

!!! warning "Legacy Permissions"
    `blockball.command.staff` and `blockball.game.inventory` are legacy permissions that will be replaced in future versions. Use with caution.

---

## 🎨 Scoreboard Permissions

BlockBall includes an integrated scoreboard system with granular permissions:

### Player Scoreboard Access

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shyscoreboard.scoreboard.*` | **User** | See all scoreboards |
| `blockball.shyscoreboard.scoreboard.[name]` | **User** | See specific scoreboard only |

### Scoreboard Administration

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shyscoreboard.command` | **Admin** | Access `/blockballscoreboard` command |
| `blockball.shyscoreboard.reload` | **Admin** | Reload scoreboard configurations |
| `blockball.shyscoreboard.add` | **Admin** | Add scoreboards to players |
| `blockball.shyscoreboard.set` | **Admin** | Set player scoreboards |
| `blockball.shyscoreboard.remove` | **Admin** | Remove scoreboards from players |
| `blockball.shyscoreboard.update` | **Admin** | Refresh scoreboards manually |

**Usage Examples:**
```bash
# Show specific scoreboard to player
/blockballscoreboard add Steve game_stats

# Remove all scoreboards from player
/blockballscoreboard remove Alex

# Refresh all scoreboards
/blockballscoreboard update
```

---

## 🎯 Boss Bar Permissions

Control access to BlockBall's boss bar displays:

### Player Boss Bar Access

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shybossbar.bossbar.*` | **User** | See all boss bars |
| `blockball.shybossbar.bossbar.[name]` | **User** | See specific boss bar only |

### Boss Bar Administration

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shybossbar.command` | **Admin** | Access `/blockballbossbar` command |
| `blockball.shybossbar.reload` | **Admin** | Reload boss bar configurations |
| `blockball.shybossbar.add` | **Admin** | Add boss bars to players |
| `blockball.shybossbar.set` | **Admin** | Set player boss bars |
| `blockball.shybossbar.remove` | **Admin** | Remove boss bars from players |
| `blockball.shybossbar.update` | **Admin** | Refresh boss bars manually |

---

## 🏷️ Sign System Permissions

Manage interactive BlockBall signs:

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shycommandsigns.command` | **Admin** | Access `/blockballsign` command |
| `blockball.shycommandsigns.add` | **Admin** | Create join/leave signs |
| `blockball.shycommandsigns.reload` | **Admin** | Reload sign configurations |
| `blockball.shycommandsigns.server` | **Admin** | Server teleport commands |
| `blockball.shycommandsigns.manipulateother` | **Admin** | Modify other players via signs |

**Sign Creation Example:**
```bash
# Create join sign for stadium1
/blockballsign add blockball_join_sign arena stadium1

# Create team-specific join signs
/blockballsign add blockball_join_red_sign arena stadium1
/blockballsign add blockball_join_blue_sign arena stadium1
```

---

## ✨ Particle System Permissions

Control access to BlockBall's particle effects:

### Player Particle Access

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shyparticles.effect.visible.*` | **User** | See all particle effects |
| `blockball.shyparticles.effect.visible.[name]` | **User** | See specific particle effect only |

### Particle Administration

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shyparticles.command` | **Admin** | Access `/blockballparticle` command |
| `blockball.shyparticles.reload` | **Admin** | Reload particle configurations |
| `blockball.shyparticles.list` | **Admin** | List available particle effects |
| `blockball.shyparticles.play` | **Admin** | Play particle effects |
| `blockball.shyparticles.stop` | **Admin** | Stop particle effects |
| `blockball.shyparticles.follow` | **Admin** | Make particles follow yourself |
| `blockball.shyparticles.followother` | **Admin** | Make particles follow other players |
| `blockball.shyparticles.stopfollow` | **Admin** | Stop particles following yourself |
| `blockball.shyparticles.stopfollowother` | **Admin** | Stop particles following other players |
| `blockball.shyparticles.effect.start.[name]` | **Admin** | Start specific particle effect by name |

---

## 🎮 Game-Specific Permission Scenarios

### Scenario 1: Public Server with Multiple Arenas
```yaml
# All players can join any arena
default_group:
  - blockball.command
  - blockball.join.*
  - blockball.shyscoreboard.scoreboard.*
  - blockball.shyparticles.effect.visible.*
```

### Scenario 2: Tournament Server with Restricted Access
```yaml
# Players can only join assigned tournament arenas
tournament_red:
  - blockball.command
  - blockball.join.tournament_red_arena
  - blockball.shyscoreboard.scoreboard.*
  - blockball.shybossbar.bossbar.*
  - blockball.shyparticles.effect.visible.*

tournament_blue:
  - blockball.command  
  - blockball.join.tournament_blue_arena
  - blockball.shyscoreboard.scoreboard.*
  - blockball.shybossbar.bossbar.*
  - blockball.shyparticles.effect.visible.*

# Referees get full control
referees:
  - blockball.command
  - blockball.referee.join
```

### Scenario 3: Training Server for New Players
```yaml
# Beginners get limited access
beginners:
  - blockball.command
  - blockball.join.training_arena
  
# Advanced players get more arenas
advanced:
  - blockball.command
  - blockball.join.training_arena
  - blockball.join.competitive_arena
```

---

## 🔍 Permission Troubleshooting

### Common Issues

| Problem | Likely Cause | Solution |
|---------|--------------|----------|
| "No permission" error | Missing `blockball.command` | Add base command permission |
| Can't join arenas | Missing join permissions | Add `blockball.join.*` or specific arena |
| Scoreboards not showing | Missing display permissions | Add scoreboard permissions |
| Admin commands not working | Missing admin permissions | Add `blockball.edit` or specific admin perms |

---

## 💡 Best Practices

### Security Guidelines

!!! warning "Admin Permission Security"
    - Never give `blockball.edit` to regular players
    - Be cautious with `blockball.command.staff` and `blockball.game.inventory`
    - Review permissions regularly as your server grows

### Performance Optimization

!!! tip "Efficient Permission Structure"
    - Use wildcard permissions (`blockball.join.*`) for simplicity
    - Create permission groups rather than individual assignments
    - Test permission changes on a development server first

### User Experience

!!! success "Player-Friendly Setup"
    - Grant scoreboard and boss bar permissions to all players
    - Use descriptive group names that make sense to your staff
    - Document your permission structure for other administrators

This permission system gives you complete control over who can access BlockBall features while maintaining security and performance on your server!