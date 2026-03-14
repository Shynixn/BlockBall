# Permission System

BlockBall uses a comprehensive permission system to control player access to features and commands. This guide helps you configure permissions correctly for different user roles on your server.

## 🔐 Permission Levels

BlockBall uses three permission levels:

* **👤 User Level**: All players who should be able to play BlockBall
* **🟡 Admin/User Level**: Trusted players or staff members. Review carefully before granting to regular players
* **🔴 Admin Level**: Permissions that should only be given to trusted staff

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

# Club system (if clubs are enabled on your server)
- blockball.shyguild.command
- blockball.shyguild.cmd.create
- blockball.shyguild.cmd.delete
- blockball.shyguild.cmd.guild.list
- blockball.shyguild.cmd.role.add
- blockball.shyguild.cmd.role.remove
- blockball.shyguild.cmd.role.list
- blockball.shyguild.cmd.member.invite
- blockball.shyguild.cmd.member.accept
- blockball.shyguild.cmd.member.leave
- blockball.shyguild.cmd.member.remove
- blockball.shyguild.cmd.member.list
- blockball.shyguild.template.blockball_club
```

### Admin Permissions
```yaml
# Arena management (staff only)
- blockball.edit
- blockball.referee.join

# Club administration (staff only)
- blockball.shyguild.cmd.member.add
- blockball.shyguild.cmd.template.list
- blockball.shyguild.cmd.reload

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
- blockball.cloud
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

## ⚽ Club Permissions

BlockBall includes an integrated club system (powered by ShyGuild internally). Club permissions are split into two layers — see the [Clubs guide](club.md) for full setup instructions.

!!! info "LuckPerms Required"
    Club-specific guild permissions (e.g. `blockball.shyguild.guild.<club>.delete`) are managed **automatically by LuckPerms** through role templates. You only need to assign the command-level permissions manually.

### Club Command Permissions

These control which commands a player can execute. Assign them to your player/staff groups.

| Permission | Level | Description |
|------------|-------|-------------|
| `blockball.shyguild.command` | **User** | Access the `/blockballclub` command base |
| `blockball.shyguild.cmd.create` | **User** | Create a new club |
| `blockball.shyguild.cmd.delete` | **User** | Delete a club |
| `blockball.shyguild.cmd.guild.list` | **User** | List all clubs the player has joined |
| `blockball.shyguild.cmd.role.add` | **User** | Assign a role to a club member |
| `blockball.shyguild.cmd.role.remove` | **User** | Remove a role from a club member |
| `blockball.shyguild.cmd.role.list` | **User** | List roles in a club or a player's roles |
| `blockball.shyguild.cmd.member.invite` | **User** | Invite a player to a club |
| `blockball.shyguild.cmd.member.accept` | **User** | Accept a pending club invite |
| `blockball.shyguild.cmd.member.leave` | **User** | Leave a club |
| `blockball.shyguild.cmd.member.remove` | **User** | Remove a member from a club |
| `blockball.shyguild.cmd.member.list` | **User** | List all members of a club |
| `blockball.shyguild.template.blockball_club` | **User** | Use the `blockball_club` template when creating a club |
| `blockball.shyguild.cmd.member.add` | **Admin** | Add a player directly to a club (bypasses invite) |
| `blockball.shyguild.cmd.template.list` | **Admin** | List all loaded club templates |
| `blockball.shyguild.cmd.reload` | **Admin** | Reload club configurations |

### Club-Specific Guild Permissions

These permissions are **per-club** and are automatically granted and revoked by LuckPerms when a player's role changes within that club. Do not assign these manually.

| Permission | Granted To | Description |
|------------|------------|-------------|
| `blockball.shyguild.guild.<club>.delete` | `owner` | Delete the club |
| `blockball.shyguild.guild.<club>.role.add.<role>` | `owner`, `coach` | Assign a specific role in the club |
| `blockball.shyguild.guild.<club>.role.remove.<role>` | `owner`, `coach` | Remove a specific role in the club |
| `blockball.shyguild.guild.<club>.role.list` | `owner`, `coach`, `captain`, `player` | List roles in the club |
| `blockball.shyguild.guild.<club>.member.add` | — (admin only) | Add members directly to the club |
| `blockball.shyguild.guild.<club>.member.remove` | `owner`, `coach` | Remove a member from the club |
| `blockball.shyguild.guild.<club>.member.list` | `owner`, `coach`, `captain`, `player` | View the club roster |
| `blockball.shyguild.guild.<club>.member.invite` | `owner`, `coach`, `captain` | Invite players to the club |
| `blockball.shyguild.guild.<club>.member.leave` | `owner`, `coach`, `captain`, `player` | Leave the club |

!!! tip "How the two layers work together"
    A player needs **both** a command permission **and** the matching club-specific permission for most actions. For example, to delete the club `red-falcons` a player needs `blockball.shyguild.cmd.delete` (assigned to their group) **and** `blockball.shyguild.guild.red-falcons.delete` (assigned automatically by LuckPerms when they hold the `owner` role in that club).

### Club Permission Scenarios

**Scenario: Server with player-created clubs**
```yaml
default:
  - blockball.shyguild.command
  - blockball.shyguild.cmd.create
  - blockball.shyguild.cmd.delete
  - blockball.shyguild.cmd.guild.list
  - blockball.shyguild.cmd.role.add
  - blockball.shyguild.cmd.role.remove
  - blockball.shyguild.cmd.role.list
  - blockball.shyguild.cmd.member.invite
  - blockball.shyguild.cmd.member.accept
  - blockball.shyguild.cmd.member.leave
  - blockball.shyguild.cmd.member.remove
  - blockball.shyguild.cmd.member.list
  - blockball.shyguild.template.blockball_club
```

**Scenario: Admin-managed clubs (staff sets up clubs, hands off ownership)**
```yaml
default:
  # Players can only accept invites, view, and leave
  - blockball.shyguild.command
  - blockball.shyguild.cmd.guild.list
  - blockball.shyguild.cmd.member.accept
  - blockball.shyguild.cmd.member.leave
  - blockball.shyguild.cmd.member.list
  - blockball.shyguild.cmd.role.list

staff:
  # Staff can create clubs and add members directly
  - blockball.shyguild.command
  - blockball.shyguild.cmd.create
  - blockball.shyguild.cmd.delete
  - blockball.shyguild.cmd.guild.list
  - blockball.shyguild.cmd.role.add
  - blockball.shyguild.cmd.role.remove
  - blockball.shyguild.cmd.role.list
  - blockball.shyguild.cmd.member.add
  - blockball.shyguild.cmd.member.remove
  - blockball.shyguild.cmd.member.list
  - blockball.shyguild.cmd.member.invite
  - blockball.shyguild.cmd.member.accept
  - blockball.shyguild.cmd.member.leave
  - blockball.shyguild.cmd.template.list
  - blockball.shyguild.cmd.reload
  - blockball.shyguild.template.blockball_club
```

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
| Can't use `/blockballclub` | Missing club base permission | Add `blockball.shyguild.command` |
| Club role permissions not applied | LuckPerms not installed | Install LuckPerms and reload |

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