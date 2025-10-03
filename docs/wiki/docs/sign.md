# Interactive Signs

Create user-friendly join and leave signs that allow players to interact with BlockBall arenas through simple right-clicks. This guide covers everything from basic sign creation to advanced customization.

## 🎯 Overview

BlockBall signs provide an intuitive way for players to join games without memorizing commands. Players simply right-click signs to join arenas, switch teams, or leave games.

### Sign Types Available

* **Join Signs**: Join any available team automatically
* **Leave Signs**: Exit the current game  
* **Team-Specific Signs**: Join a specific team (Red/Blue)
* **Custom Signs**: Advanced configurations for special needs

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

* Joins any available team automatically
* Balances teams when possible
* Shows current game status

**Best For**: Main entrances, lobby areas

### Leave Sign  
**Command**: `/blockballsign add blockball_leave_sign arena <game>`

**Functionality**:

* Removes player from current game
* Works regardless of which arena they're in
* Safe exit from any BlockBall game

**Best For**: Exit areas, spawn points

### Team-Specific Signs

#### Red Team Join
**Command**: `/blockballsign add blockball_join_red_sign arena <game>`

**Functionality**: 

* Joins red team specifically
* Shows red team status and availability
* May reject if team is full

#### Blue Team Join
**Command**: `/blockballsign add blockball_join_blue_sign arena <game>`

**Functionality**:

* Joins blue team specifically  
* Shows blue team status and availability
* May reject if team is full

**Best For**: Team selection areas, faction-based servers

---

## 🛠️ Sign Management

### Removing Signs
Simply break the sign with your hand or any tool. The BlockBall functionality will be automatically removed.

### Editing Signs
To modify an existing sign:

1. Break the current sign
2. Place a new sign** in the desired location
3. Run the appropriate command again
4. Right-click the new sign to apply
---

## 🎨 Sign Customization

### Configuration Files

BlockBall uses an internal version of [ShyCommandSigns](https://shynixn.github.io/ShyCommandSigns/wiki/site/installation/) for sign functionality. You can customize appearance and behavior through configuration files.
