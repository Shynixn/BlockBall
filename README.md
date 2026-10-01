# MoonXBall

> Fork of [Shynixn's BlockBall](https://github.com/Shynixn/BlockBall) rebranded as **MoonXBall**.
> Adds 1.8 client support (via ViaVersion), a "Join An Game" hotbar sword + Selection Area GUI,
> soft-depend hooks for Multiverse-Core and DecentHolograms, per-arena `BlockMxc.join.<arena>`
> permissions, a second config file (`MX_Blocball2.yml`), and a stable, performant
> scoreboard adapter for legacy 1.8.x clients.

| branch        | status                                                                                                                                                        | download                                                                 |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------| 
| master        | [![Build Status](https://github.com/Shynixn/BlockBall/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/Shynixn/BlockBall/actions)      | [Download latest release](https://github.com/Shynixn/BlockBall/releases) |
| development        | [![Build Status](https://github.com/Shynixn/BlockBall/actions/workflows/main.yml/badge.svg?branch=development)](https://github.com/Shynixn/BlockBall/actions) |                                                                          |

## Description

MoonXBall is a spigot plugin to play soccer games in Minecraft. It is a fork of
BlockBall v7.45.0 that adds:

- **1.8 client compatibility:** Legacy 1.8.x clients joining a 1.21.6 server
  via ViaVersion + ViaBackwards + ViaRewind get a 1.8-safe scoreboard, no hex
  colors, and version-tolerant Materials / Sounds / hand-event handling.
- **Join-An-Game sword + GUI:** Every lobby player receives a wooden sword in
  hotbar slot 4 (Bukkit index 3). Right- or left-click opens the "Selection
  Area" GUI listing all arenas with state, player count, min/max, and
  permission-locked display.
- **Per-arena permissions:** `BlockMxc.join.<arenaname>` (lowercase) plus a
  wildcard `BlockMxc.join.*`. Legacy `blockball.join.<arenaname>` is still
  honoured for backwards compatibility.
- **Arena display names:** Configurable `displayName` (already supported by
  the upstream SoccerArena entity) is used in GUI, scoreboard, messages, and
  holograms. Internal arena name remains the identity for storage and
  permissions.
- **Multiverse-Core hook (4.x + 5.x):** Loads arena worlds, teleports
  players respecting MV per-world rules. Falls back to Bukkit API when MV is
  absent.
- **DecentHolograms hook (2.8.x):** Create / update / delete holograms
  reflectively. Silently no-ops when DH is absent. All lines are 1.8-safe.
- **Second config file `MX_Blocball2.yml`:** All SPECIAL settings (join item,
  GUI, messages, sounds, hooks, permissions, legacy client behavior) live
  here. Auto-saved on first run, auto-merged on upgrade, reloaded by
  `/blockball reload`.

## Features

* Uses blocks as balls in minecraft
* Games are completely customizable
* Bukkit and Folia compatible
* 1.8.x client support via ViaVersion (legacy scoreboard adapter, hex
  down-sampling, version-tolerant Materials/Sounds/hand events)
* Join-An-Game wooden sword in lobby + Selection Area GUI
* Per-arena `BlockMxc.join.<arena>` permissions with wildcard
* Multiverse-Core 4.x/5.x soft-depend hook
* DecentHolograms 2.8.x soft-depend hook
* Auto-migrates `plugins/BlockBall/` to `plugins/MoonXBall/` on first run
  (zero data loss)

## Installation

* Drop the appropriate jar (see [Build Instructions](CHANGES.md#7-build-instructions))
  into `plugins/`.
* If upgrading from BlockBall: stop the server, replace the jar, restart.
  MoonXBall auto-renames `plugins/BlockBall/` to `plugins/MoonXBall/`.
* (Optional) Install ViaVersion, ViaBackwards, ViaRewind for 1.8 client support.
* (Optional) Install Multiverse-Core for world loading/teleport integration.
* (Optional) Install DecentHolograms for hologram features.
* Configure `MX_Blocball2.yml` for join item, GUI, hooks, permissions, and
  legacy-client behavior.
* Run `/blockball reload` to apply changes.

## Documentation

* Full changelog, architecture summary, build instructions, test checklist,
  known limitations and assumptions: [CHANGES.md](CHANGES.md).
* Original BlockBall documentation: https://shynixn.github.io/BlockBall/

## Screenshots

![alt tag](http://www.mediafire.com/convkey/3383/6zhpiiijhk022s5zg.jpg)
![alt tag](http://www.mediafire.com/convkey/a253/ur76bhb6doccomvzg.jpg)

# Licence

Take a look into the ``LICENCE`` file for further details.

* Shynixn Plugin Distribution LICENCE
* GNU GENERAL PUBLIC LICENSE Version 3

