# Clubs

BlockBall includes an integrated club system powered internally by **ShyGuild**. Clubs allow players to form teams, manage rosters, and join BlockBall games together. No extra plugin installation is required — BlockBall already ships with ShyGuild built in.

!!! info "LuckPerms Required"
    Clubs use automatic per-club permission management. **LuckPerms must be installed** for role permissions to be applied automatically when a player creates or joins a club.

---

## 🎯 Overview

A **club** is a named group of players with a structured role hierarchy. The built-in `blockball_club` template provides four roles out of the box:

| Role | Description |
|------|-------------|
| `owner` | Full control — can manage roles, kick members, invite players, and delete the club |
| `coach` | Can manage captains and players, invite and remove members, but cannot delete the club or promote owners |
| `captain` | Can invite new players and view the roster |
| `player` | Default role — can view the roster and leave the club |

When a club is created, the creator is automatically assigned the `owner` role and all role permissions are applied via LuckPerms.

---

## 🚀 Quick Start (Player Self-Service)

This scenario covers servers where **players manage their own clubs**.

### Required Permissions for Players
```yaml
# Grant these to all players (e.g. via your default group)
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

---

### Step 1: Create a Club

Any player with the required permissions can create their own club:

```bash
/blockballclub create blockball_club <name> <displayName>
```

| Parameter | Description |
|-----------|-------------|
| `<name>` | Internal club identifier — alphanumeric and hyphens only (e.g. `red-falcons`) |
| `<displayName>` | Display name shown in chat — use underscores for spaces (e.g. `Red_Falcons`) |

**Example:**
```bash
/blockballclub create blockball_club red-falcons Red_Falcons
```

The player who runs this command automatically becomes the club `owner` and all `owner` role permissions are applied via LuckPerms.

---

### Step 2: Invite Members

As an `owner` or `captain`, invite other online players to your club:

```bash
/blockballclub member invite <clubName> <player>
```

**Example:**
```bash
/blockballclub member invite red-falcons Steve
/blockballclub member invite red-falcons Alex
```

---

### Step 3: Accept an Invite

Invited players must accept before they are added to the club:

```bash
/blockballclub member accept <clubName>
```

**Example:**
```bash
/blockballclub member accept red-falcons
```

Accepted players are assigned the `player` role (the default role).

---

### Step 4: Assign Roles

Owners and coaches can promote members to higher roles:

```bash
/blockballclub role add <clubName> <role> [player]
```

**Example:**
```bash
# Promote Steve to captain
/blockballclub role add red-falcons captain Steve

# Promote Alex to coach
/blockballclub role add red-falcons coach Alex
```

---

### Step 5: View Club Roster

List all current members and their roles:

```bash
/blockballclub member list <clubName>
```

---

### Step 6: Leave or Delete a Club

**Leave a club:**
```bash
/blockballclub member leave <clubName>
```

!!! warning "Owner Restriction"
    You cannot leave a club if you are the only `owner`. Promote another member to `owner` first with `/blockballclub role add <clubName> owner <player>`.

**Delete a club (owner only):**
```bash
/blockballclub delete <clubName>
```

---

## 🛡️ Admin-Managed Clubs

This scenario covers servers where **staff or the server owner creates clubs** and distributes ownership to specific players afterwards.

### Required Permissions for Admins
```yaml
# Staff group — in addition to all player permissions above
- blockball.shyguild.cmd.member.add
- blockball.shyguild.cmd.template.list
- blockball.shyguild.cmd.reload
```

---

### Step 1: Check Available Templates

First, verify that the `blockball_club` template is loaded:

```bash
/blockballclub template list
```

You should see `blockball_club` in the output. If not, ensure `blockball_club.yml` exists in the `clubs/` folder and run `/blockballclub reload`.

---

### Step 2: Create the Club as Admin

An admin creates the club from the console or in-game:

```bash
/blockballclub create blockball_club red-falcons Red_Falcons
```

!!! note "Console vs In-Game"
    When created from the **console**, no owner is automatically assigned. When created **in-game by a player**, that player becomes the owner. For the admin-managed workflow, create the club in-game as a staff player, or assign the owner role manually in the next step.

---

### Step 3: Add Members Directly

Admins can bypass the invite system and add players directly:

```bash
/blockballclub member add <clubName> <player>
```

**Example:**
```bash
/blockballclub member add red-falcons Steve
/blockballclub member add red-falcons Alex
/blockballclub member add red-falcons Notch
```

All added players receive the `player` (default) role.

---

### Step 4: Assign the Owner Role

Grant a player ownership of the club so they can manage it going forward:

```bash
/blockballclub role add <clubName> owner <player>
```

**Example:**
```bash
/blockballclub role add red-falcons owner Steve
```

Steve now has all `owner` permissions for the `red-falcons` club (delete, role management, invite, kick) applied automatically via LuckPerms.

---

### Step 5: Optionally Assign Other Roles

Assign coach and captain roles to other members:

```bash
/blockballclub role add red-falcons coach Alex
/blockballclub role add red-falcons captain Notch
```

---

### Full Admin Workflow Example

```bash
# 1. Verify templates
/blockballclub template list

# 2. Create the club
/blockballclub create blockball_club red-falcons Red_Falcons

# 3. Add all members
/blockballclub member add red-falcons Steve
/blockballclub member add red-falcons Alex
/blockballclub member add red-falcons Notch

# 4. Assign owner
/blockballclub role add red-falcons owner Steve

# 5. Assign other roles
/blockballclub role add red-falcons coach Alex
/blockballclub role add red-falcons captain Notch

# 6. Verify
/blockballclub member list red-falcons
```

---

## 📋 Club Template Reference (`blockball_club.yml`)

The `blockball_club` template ships with BlockBall and defines the role hierarchy and permissions for every club created from it.

```yaml
name: "blockball_club"
maxPlayers: 30
defaultRole: "player"
roles:
  # The club owner has full control over the guild
  - name: "owner"
    allowPermissions:
      - "blockball.shyguild.guild.%blockball_guild_name%.delete"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.owner"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.owner"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.coach"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.coach"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.captain"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.captain"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.player"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.player"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.remove"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.invite"
      - "blockball.shyguild.guild.%blockball_guild_name%.leave"
    denyPermissions: []

  # Coaches can manage captains and players, but cannot delete the club or assign owners
  - name: "coach"
    allowPermissions:
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.captain"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.captain"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.add.player"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.remove.player"
      - "blockball.shyguild.guild.%blockball_guild_name%.role.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.remove"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.invite"
      - "blockball.shyguild.guild.%blockball_guild_name%.leave"
    denyPermissions: []

  # Captains can invite new players and view the roster, but cannot manage roles
  - name: "captain"
    allowPermissions:
      - "blockball.shyguild.guild.%blockball_guild_name%.role.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.invite"
      - "blockball.shyguild.guild.%blockball_guild_name%.leave"
    denyPermissions: []

  # Regular players can only view the roster and leave
  - name: "player"
    allowPermissions:
      - "blockball.shyguild.guild.%blockball_guild_name%.role.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.member.list"
      - "blockball.shyguild.guild.%blockball_guild_name%.leave"
    denyPermissions: []
```

The `%blockball_guild_name%` placeholder is automatically replaced with the actual club name when permissions are applied via LuckPerms.

---

## 📋 Complete Command Reference

All commands use `/blockballclub` (alias: `/blockballclub`). The base permission `blockball.shyguild.command` is required for every command.

| Command | Description | Permission |
|---------|-------------|-----------|
| `/blockballclub create <template> <name> <displayName>` | Create a new club | `blockball.shyguild.cmd.create` + `blockball.shyguild.template.<template>` |
| `/blockballclub delete <club>` | Delete a club | `blockball.shyguild.cmd.delete` + `blockball.shyguild.guild.<club>.delete` |
| `/blockballclub member add <club> <player>` | Add a player directly (admin) | `blockball.shyguild.cmd.member.add` + `blockball.shyguild.guild.<club>.member.add` |
| `/blockballclub member remove <club> <player>` | Remove a player from a club | `blockball.shyguild.cmd.member.remove` + `blockball.shyguild.guild.<club>.member.remove` |
| `/blockballclub member list <club>` | List all club members | `blockball.shyguild.cmd.member.list` + `blockball.shyguild.guild.<club>.member.list` |
| `/blockballclub member invite <club> <player>` | Invite a player to the club | `blockball.shyguild.cmd.member.invite` + `blockball.shyguild.guild.<club>.member.invite` |
| `/blockballclub member accept <club>` | Accept a club invite | `blockball.shyguild.cmd.member.accept` |
| `/blockballclub member leave <club>` | Leave a club | `blockball.shyguild.cmd.member.leave` + `blockball.shyguild.guild.<club>.member.leave` |
| `/blockballclub role add <club> <role> [player]` | Assign a role to a member | `blockball.shyguild.cmd.role.add` + `blockball.shyguild.guild.<club>.role.add.<role>` |
| `/blockballclub role remove <club> <role> [player]` | Remove a role from a member | `blockball.shyguild.cmd.role.remove` + `blockball.shyguild.guild.<club>.role.remove.<role>` |
| `/blockballclub role list <club> [player]` | List club roles or a player's roles | `blockball.shyguild.cmd.role.list` + `blockball.shyguild.guild.<club>.role.list` |
| `/blockballclub guild list` | List your joined clubs | `blockball.shyguild.cmd.guild.list` |
| `/blockballclub template list` | List all loaded templates | `blockball.shyguild.cmd.template.list` |
| `/blockballclub reload` | Reload club configurations | `blockball.shyguild.cmd.reload` |

!!! info "Two Permissions Per Action"
    Most actions require **two permissions**: a command-level permission (e.g. `blockball.shyguild.cmd.delete`) and a club-specific permission (e.g. `blockball.shyguild.guild.<club>.delete`). The club-specific permissions are handled automatically by LuckPerms via the role template — you do not need to assign them manually.

---

## 🔍 Troubleshooting

| Problem | Likely Cause | Solution |
|---------|--------------|----------|
| "No permission" on `/blockballclub` | Missing base permission | Add `blockball.shyguild.command` |
| Can't create a club | Missing create or template permission | Add `blockball.shyguild.cmd.create` and `blockball.shyguild.template.blockball_club` |
| Role permissions not applied | LuckPerms not installed | Install LuckPerms |
| Template not found | `blockball_club.yml` missing | Ensure the file exists in the `clubs/` folder and run `/blockballclub reload` |
| Owner can't delete club | Missing guild-level permission | Ensure LuckPerms applied `blockball.shyguild.guild.<club>.delete` — try `/blockballclub reload` |
| Can't leave as owner | Only owner in club | Assign `owner` role to another member first, then leave |
