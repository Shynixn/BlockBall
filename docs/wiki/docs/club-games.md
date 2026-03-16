# Club vs Club Games

By default, BlockBall arenas are open to all players via `blockball.join.*`. The club system adds a second layer of arena access so you can create **dedicated club arenas** that only club members can use — or mix both open and club-only arenas on the same server.

!!! info "Premium Feature"
    The club system is exclusive to **Patreon supporters** who have funded this advanced feature. [Support development](https://www.patreon.com/Shynixn) to access these tools! ❤️

## How Arena Access Works

| Permission | Who has it | Effect |
|------------|-----------|--------|
| `blockball.join.*` | All players | Can join any open arena |
| `blockball.join.<arena>` | All players (specific) | Can join that one arena only |
| `blockball.club.join.*` | Club members (via LuckPerms role) | Can join any club arena |
| `blockball.club.join.<arena>` | Club members (via LuckPerms role) | Can join that one club arena only |
| `blockball.club.start.<arena>` | Trusted club roles (via LuckPerms role) | Can **start** a club game on that arena |

## Joining a Club Game

To enter a club arena, use the club join command instead of the regular join:

```bash
/blockball club join <arena>
```

**Starting a club game** requires `blockball.club.start.<arena>`. The first club member with that permission who joins opens the game session. After that, all other club members who have `blockball.club.join.<arena>` can join normally with the same command — no start permission needed for them.

---

## Setting Up Club Arenas (Step by Step)

### Step 1: Decide on your arena layout

Choose which arenas should be open to everyone and which should be club-exclusive:

```
stadium1   →  open arena   (blockball.join.stadium1)
club-arena →  club only    (blockball.club.join.club-arena)
```

You can also have arenas that are **both** — grant `blockball.join.<arena>` to all players and `blockball.club.join.<arena>` to club members.

---

### Step 2: Assign open arena permissions to players

For normal arenas, grant join permissions to your default player group as usual:

```yaml
default:
  - blockball.command
  - blockball.join.stadium1
  # or blockball.join.* for all open arenas
```

Do **not** add `blockball.join.<arena>` for club-only arenas — that would let anyone in.

---

### Step 3: Add club arena permissions to `blockball_club.yml`

Open `plugins/BlockBall/clubs/blockball_club.yml` and add the arena permissions to each role.

- **All roles** (`owner`, `coach`, `captain`, `player`) need `blockball.club.join.<arena>` so every club member can join.
- **Trusted roles** (`owner`, `coach`, `captain`) additionally need `blockball.club.start.<arena>` so they can open a game session.

```yaml
name: "blockball_club"
maxPlayers: 30
defaultRole: "player"
roles:
  - name: "owner"
    allowPermissions:
      # ... club management permissions ...
      - "blockball.club.join.club-arena"
      - "blockball.club.start.club-arena"
    denyPermissions: []

  - name: "coach"
    allowPermissions:
      # ... club management permissions ...
      - "blockball.club.join.club-arena"
      - "blockball.club.start.club-arena"
    denyPermissions: []

  - name: "captain"
    allowPermissions:
      # ... club management permissions ...
      - "blockball.club.join.club-arena"
      - "blockball.club.start.club-arena"
    denyPermissions: []

  - name: "player"
    allowPermissions:
      # ... club management permissions ...
      - "blockball.club.join.club-arena"
      # no blockball.club.start — regular players cannot open a game session
    denyPermissions: []
```

For **multiple club arenas**, add one line per arena to each role:

```yaml
- "blockball.club.join.club-arena"
- "blockball.club.join.club-arena-2"
- "blockball.club.start.club-arena"
- "blockball.club.start.club-arena-2"
```

After editing the file, reload with:

```bash
/blockballclub reload
```

LuckPerms will then automatically apply the updated permissions to all current club members.

---

### Step 4: Complete permission overview for this setup

```yaml
# Default group — open arenas only, no club arenas
default:
  - blockball.command
  - blockball.join.stadium1

# Club members get club arena access automatically via LuckPerms + blockball_club.yml
# (blockball.club.join.club-arena is applied when they join a club)

# If you want to restrict completely to club games only — remove blockball.join.* from default
# and let the club template handle all arena access.
```

!!! tip "Club-only server"
    If you want **all** games to be club games, simply don't grant any `blockball.join.*` permissions to the default group at all. All arena access then flows exclusively through the club template permissions, ensuring every match is between organised clubs.

!!! warning "Reload after template changes"
    Whenever you edit `blockball_club.yml`, run `/blockballclub reload`. The updated permissions are applied to existing club members by LuckPerms automatically.
