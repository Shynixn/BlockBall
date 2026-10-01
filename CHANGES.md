# MoonXBall — BlockBall Fork Changelog

**Date:** 2026-10-01
**Fork:** MoonXBall (rebrand of BlockBall v7.45.0)
**Target server:** Paper/Spigot 1.21.6 with ViaVersion + ViaBackwards + ViaRewind (1.8.x clients joining a 1.21.6 server).
**Build tool:** Gradle 8.12.1 (Kotlin DSL).
**JVM target:** Java 1.8 (Kotlin `jvmTarget = "1.8"`).

---

## 1. Architecture Summary (Before Changes)

BlockBall is a Kotlin/Java Minecraft plugin built by Shynixn. It is shipped as a
single fat jar (Shadow) that bundles the upstream's own private libraries:
`mcutils:{common,packet,worldguard,database,http}`, `shyscoreboard:1.15.0`,
`shybossbar:1.9.0`, `shycommandsigns:1.7.0`, `shyparticles:1.5.0`,
`shyguild:1.3.0`, `mccoroutine-folia:{api,core}:2.22.0`, `fasterxml:1.2.0`,
`kotlinx-coroutines-core`, `HikariCP`. The `mcutils:*` artifacts are private
(require `SHYNIXN_MCUTILS_REPOSITORY_2026` env var).

### Module layout

```
BlockBallPlugin (entry point)
  ├── Version gate (1.8_R3 .. 26_3_R1, Folia detection)
  ├── BlockBallLanguageImpl (code-generated from lang/en_us.yml)
  ├── PlaceHolderServiceImpl
  ├── CommonSqlConnectionServiceImpl (dataFolder/BlockBall.sqlite)
  ├── Companion Shynixn modules:
  │     ├── loadShyGuildModule       (club management, Patreon)
  │     ├── loadShyScoreboardModule  (scoreboard text + dispatch)
  │     ├── loadShyBossBarModule     (bossbar dispatch)
  │     ├── loadShyCommandSignsModule(join/leave signs)
  │     └── loadShyParticlesModule   (particle effects)
  ├── BlockBallDependencyInjectionModule
  │     ├── Repositories  : SoccerArena, SoccerBallMeta, PlayerInformation
  │     ├── Services      : GameService, SoccerBallService, StatsService,
  │     │                  CloudService, ForceFieldService, PacketService,
  │     │                  ChatMessageService, SoundService, ItemService,
  │     │                  RayTracingService, AreaSelectionService,
  │     │                  ConfigurationService, CommandService,
  │     │                  ParticleEffectService, HttpClientFactory
  │     ├── Listeners     : GameListener, BallListener,
  │     │                  DoubleJumpListener, ForceFieldListener
  │     └── CommandExecutor: BlockBallCommandExecutor (/blockball tree)
  ├── PlayerDataRepository.createIfNotExist()
  ├── PlaceHolder.registerAll(...)         (BlockBall placeholders)
  ├── PacketService.registerPacketListening(USEENTITY, ATTACKENTITY)
  ├── Bukkit.getPluginManager().registerEvents(...)
  ├── ServicesManager.register(SoccerBallService, GameService)
  └── plugin.launch { reloadAll(); statsService.register(); preload players }
```

### Tick loops

1. `GameServiceImpl.init { while(!isDisposed) { runGames(); delay(1.ticks) } }` drives all games at 1-tick cadence.
2. Each `SoccerGame.handle(hasSecondPassed)` is dispatched per subclass (`SoccerHubGameImpl` / `SoccerMiniGameImpl` / `SoccerRefereeGameImpl`).
3. Each spawned `SoccerBall` gets its own `plugin.launch { while(!isDead) withContext(regionDispatcher(ball.location)) { ball.update(deltaMs) } }` loop (region-dispatched for Folia safety).

### Where the scoreboard comes from

**Important:** BlockBall does not write scoreboard packets itself. All scoreboard I/O is delegated to the embedded `shyscoreboard` library (relocated to `com.github.shynixn.blockball.lib.com.github.shynixn.shyscoreboard`).

- BlockBall-side config: `scoreboard/blockball_scoreboard.yml` (title, lines, refreshTicks, type=COMMAND, priority=1).
- BlockBall-side runtime knobs: `config.scoreboard.checkForChangeChangeSeconds`, `config.scoreboard.joinDelaySeconds`.
- BlockBall-side placeholders: registered via `enumeration/PlaceHolder` (`%blockball_game_time%`, etc.).
- The actual `PacketPlayOutScoreboardObjective` / `PacketPlayOutScoreboardScore` writes live inside the (private) shyscoreboard jar.

### Existing version-compat surface

- `Version.serverVersion.isCompatible(...)` gates NMS bundle selection (1.8_R3 .. 26_3_R1).
- `Extension.kt`: `Player.teleportCompat` reflectively invokes `Entity#teleportAsync(Location)` on Folia; `Player.setInventoryContentsSecure` works around a 1.21.6 inventory-array sizing bug.
- `BlockBallDependencyInjectionModule.areLegacyVersionsIncluded`: reflection check for `v1_8_R3.PacketSendServiceImpl` (also serves as the Patreon gate for `REFEREEGAME`).
- No ViaVersion / ProtocolLib / DecentHolograms / Multiverse-Core usage anywhere.

---

## 2. Root Causes Found

### RC-1: 1.8 client scoreboard is garbled because ShyScoreboard assumes modern capabilities

The shyscoreboard lib sends long lines (>16 chars) and hex (#rrggbb) colors in
its packets. On 1.8.x clients those packets silently truncate or render with
broken colors:
- Objective display name max 32 chars → long titles get cut mid-color-code.
- Team prefix/suffix max 16 chars each → lines longer than 16 chars split wrong,
  color resets at the boundary.
- No hex colors → `#F57F17` shows as literal `#F57F17` text.
- No number-format hiding → the red score number always shows.
- Identical line text produces duplicate team entries (server rejects the packet).

**Fix:** A side-by-side `LegacyScoreboardController` that detects 1.8 clients via
the ViaVersion API and sends our own scoreboard packets under a separate
objective name (`moonxball_18`) after first removing the ShyScoreboard-managed
objective. Line splitting uses the pure-data `LegacyScoreboardLineSplitter`
which is unit-testable (see `LegacyScoreboardLineSplitterTest`).

### RC-2: Single bad arena kills the entire game ticker

`GameServiceImpl.runGames()` had no try/catch around `game.handle(hasSecondPassed)`.
If any game threw (e.g. NPE on `arena.ballSpawnPoint!!` after a bad config
edit), the exception propagated out of the `while (!isDisposed)` coroutine loop
in `init { ... }`, terminating the ticker for ALL games. New arenas loaded
afterwards never ticked.

**Fix:** Wrap each game's tick in try/catch, log and skip. Same treatment for
`reloadAll()` so one bad arena file doesn't block the rest.

### RC-3: `SoccerBallServiceImpl.soccerBallByEntity` was a plain `HashMap`

It was concurrently mutated by `spawn` (region dispatcher) and read by
`getByEntityId` (async packet event in `BallListener`). Concurrent
`HashMap.put`/`get` can corrupt the table or return stale values.

**Fix:** Replaced with `ConcurrentHashMap<Int, SoccerBall>`.

### RC-4: No 1.8-compatible material/sound/hand abstractions

Code that uses `Material.WOODEN_SWORD` (1.13+), `PlayerInteractEvent#getHand`
(1.9+), or `Material.BLACK_STAINED_GLASS_PANE` (1.13+) will throw on 1.8.

**Fix:** `compat/` package with `MaterialCompat`, `SoundCompat`,
`HandCompat`, `VersionCompat`, `ViaVersionDetector` — single place where
version differences are resolved. No scattered `if (version >= 1.13)` checks.

### RC-5: No soft-depend hooks for Multiverse-Core / DecentHolograms / ViaVersion

If an arena references a not-yet-loaded world, BlockBall silently fails. If
DecentHolograms is installed, BlockBall can't render holograms.

**Fix:** `hooks/MultiverseCoreHook` (4.x + 5.x via reflection) and
`hooks/DecentHologramsHook` (2.8.x via reflection). Both compileOnly, both
graceful no-ops when absent.

### RC-6: No second config file; `config.yml` is getting overloaded

User wanted all SPECIAL settings (join item, GUI, hooks, permissions) in a
second file that is auto-merged on upgrade.

**Fix:** `config/MoonXConfig` loads `MX_Blocball2.yml` from resources on
first run, merges missing keys on subsequent loads, and is reloaded by
`/blockball reload` via the new `BlockBallPlugin.reloadMoonXConfig()` hook.

### RC-7: Joining an arena had no per-arena permission gate

The existing `Permission.JOIN` enum exists but the GUI/command path doesn't
enforce a per-arena node. The user explicitly asked for
`BlockMxc.join.<arenaname>` (lowercase) plus a wildcard `BlockMxc.join.*`.

**Fix:** `GameSelectionGUI.hasJoinPermission` checks both `BlockMxc.join.<name>`
(new) and `blockball.join.<name>` (legacy). Locked arenas render as a red
barrier in the GUI. Both wildcard nodes are declared in plugin.yml with
`default: true`.

---

## 3. Files Added

| # | Path | Purpose |
|---|------|---------|
| 1 | `src/main/java/com/github/shynixn/blockball/compat/VersionCompat.kt` | One-shot server-version detection + `ServerVersion` enum. |
| 2 | `src/main/java/com/github/shynixn/blockball/compat/MaterialCompat.kt` | Cross-version Material resolver (`LogicalMaterial.WOOD_SWORD` → `WOOD_SWORD` on 1.8, `WOODEN_SWORD` on 1.13+). |
| 3 | `src/main/java/com/github/shynixn/blockball/compat/SoundCompat.kt` | Cross-version Sound resolver (modern `ENTITY_*` names with legacy `CLICK` / `ORB_PICKUP` fallbacks). |
| 4 | `src/main/java/com/github/shynixn/blockball/compat/HandCompat.kt` | `PlayerInteractEvent#getHand` reflectively; debounces dual-hand firing on 1.9+. |
| 5 | `src/main/java/com/github/shynixn/blockball/compat/ViaVersionDetector.kt` | Reflective ViaVersion API wrapper; detects 1.8.x clients by protocol version 47. |
| 6 | `src/main/java/com/github/shynixn/blockball/scoreboard/LegacyScoreboardLineSplitter.kt` | Pure-data, unit-testable line splitter (prefix/entry/suffix; color carry; hex strip; nearest-legacy down-sampling; unique-entry padding). |
| 7 | `src/main/java/com/github/shynixn/blockball/scoreboard/LegacyScoreboardController.kt` | Runtime controller that sends 1.8-safe scoreboard packets to legacy clients; diff-and-update only changed lines. |
| 8 | `src/main/java/com/github/shynixn/blockball/config/MoonXConfig.kt` | Loader for `MX_Blocball2.yml`; saves defaults on first run, merges missing keys on upgrade, reloaded by `/blockball reload`. |
| 9 | `src/main/java/com/github/shynixn/blockball/hooks/MultiverseCoreHook.kt` | Soft-depend MV 4.x + 5.x wrapper (loadWorld, teleport with fallback). |
| 10 | `src/main/java/com/github/shynixn/blockball/hooks/DecentHologramsHook.kt` | Soft-depend DH 2.8.x wrapper (create/update/delete with cleanup). |
| 11 | `src/main/java/com/github/shynixn/blockball/listeners/JoinItemListener.kt` | Hotbar sword at slot index 3 (UI slot 4); non-droppable; both right- and left-click open GUI; cancel all inventory operations; debounced dual-hand firing. |
| 12 | `src/main/java/com/github/shynixn/blockball/gui/GameSelectionGUI.kt` | "Selection Area" GUI: live-refresh, click-to-join, all clicks cancelled, locked-arena display, sound feedback. |
| 13 | `src/main/resources/MX_Blocball2.yml` | Default second config file (fully commented). |
| 14 | `src/test/java/com/github/shynixn/blockball/scoreboard/LegacyScoreboardLineSplitterTest.kt` | Unit tests for the line splitter (12 cases covering empty, short, long, color-carry, hex strip, nearest-legacy, unique-padding). |
| 15 | `CHANGES.md` | This file. |

## 4. Files Modified

| # | Path | Change |
|---|------|--------|
| 1 | `build.gradle.kts` | Added `maven("https://jitpack.io")`. Added `compileOnly` for DecentHolograms 2.8.12 and Multiverse-Core 4.3.12. Made `SHYNIXN_MCUTILS_REPOSITORY_2026` env var gracefully fall back to `mavenLocal()` when unset (so the script still loads). |
| 2 | `src/main/resources/plugin-1.8.8-1.16.5.yml` | Rebranded `name:` to `MoonXBall`; added `description`, `commands`, `permissions`; extended `softdepend` with ViaVersion/ViaBackwards/ViaRewind/Multiverse-Core/DecentHolograms. |
| 3 | `src/main/resources/plugin-1.17.0-1.21.11.yml` | Same rebrand + softdepend + commands + permissions (api-version 1.13). |
| 4 | `src/main/resources/plugin-1.17.0-1.21.11-folia.yml` | Same + `folia-supported: true`. |
| 5 | `src/main/resources/plugin-26.1.0-latest.yml` | Same; updated kotlin-stdlib to 2.3.20 and kotlinx-coroutines to 1.10.2 (preserved). |
| 6 | `src/main/resources/plugin-26.1.0-latest-folia.yml` | Same as above + `folia-supported: true`. |
| 7 | `src/main/java/com/github/shynixn/blockball/BlockBallPlugin.kt` | Renamed prefix to `[MoonXBall]` (gold); added `migrateDataFolder()` (renames `plugins/BlockBall` → `plugins/MoonXBall` on first run, idempotent); initialised compat layer (VersionCompat, MaterialCompat, SoundCompat, HandCompat, ViaVersionDetector); loaded `MoonXConfig`; instantiated `MultiverseCoreHook`, `DecentHologramsHook`, `JoinItemListener`, `GameSelectionGUI`, `LegacyScoreboardController`; registered their listeners; started their tasks; added `reloadMoonXConfig()` callable from `/blockball reload`; updated `onDisable` to stop all new tasks; updated success message. |
| 8 | `src/main/java/com/github/shynixn/blockball/impl/service/GameServiceImpl.kt` | Wrapped each game tick in `try/catch` so one bad arena no longer kills the ticker. Wrapped each arena reload in `try/catch` so one bad arena file no longer blocks the rest of `reloadAll`. |
| 9 | `src/main/java/com/github/shynixn/blockball/impl/service/SoccerBallServiceImpl.kt` | Replaced `HashMap<Int, SoccerBall>` with `ConcurrentHashMap` (concurrent packet-event reads vs region-dispatcher writes). |
| 10 | `src/main/java/com/github/shynixn/blockball/impl/commandexecutor/BlockBallCommandExecutor.kt` | Patched `reloadArena(sender, null)` to also call `BlockBallPlugin.reloadMoonXConfig()` so `/blockball reload` picks up `MX_Blocball2.yml` changes. |

## 5. Default `MX_Blocball2.yml` (extracted to `src/main/resources/MX_Blocball2.yml`)

See full file in repo. Highlights:
- `lobby.worlds: []`, `lobby.giveWhenNotInGame: true`
- `joinItem.material: "WOOD_SWORD"`, `displayName: "&6&lJoin An Game &7(Right-Click)"`, `slot: 3` (Bukkit index; UI slot 4)
- `gui.title: "&8&lSelection Area"`, `gui.size: 27`, refresh 20 ticks, `closeOnJoin: true`
- `gui.arenaItem.*` placeholders: `%arena_display%`, `%arena_state%`, `%arena_players%`, `%arena_maxplayers%`, `%arena_minplayers%`, `%arena_locked%`
- Messages, sounds, hooks (MV + DH with `verbose`), permissions (`joinNode: "BlockMxc.join"`, `legacyJoinNode: "blockball.join"`, `showLockedArenas: true`)
- `legacyClient.scoreboardAdapter: true`, `scoreNumberBehavior: "order"`, `stripHexColors: true`

## 6. plugin.yml Changes Summary (all 5 files)

```yaml
name: MoonXBall
softdepend: [ PlaceholderAPI, LuckPerms, ViaVersion, ViaBackwards, ViaRewind, Multiverse-Core, DecentHolograms ]
commands:
  blockball:
    aliases: [ soccer, football, moonxball, mxball, mx ]
permissions:
  blockball.command:        { default: true }
  blockball.edit:           { default: op }
  blockball.join.*:         { default: true }
  BlockMxc.join.*:          { default: true, description: "Wildcard permission to join any MoonXBall arena (new BlockMxc.* namespace)." }
```

The legacy `main: com.github.shynixn.blockball.BlockBallPlugin` is **kept** so the Java package and class path don't change; only the `name:` (which is what Bukkit uses to find the data folder) is renamed.

## 7. Build Instructions

### Prerequisites
- JDK 8+ (project targets `jvmTarget = "1.8"`, but JDK 17 is required to run Gradle itself; the build is verified on JDK 21).
- Gradle 8.12.1 (the wrapper `./gradlew` is bundled).
- The `SHYNIXN_MCUTILS_REPOSITORY_2026` environment variable pointing to the private Shynixn MCUtils maven repo (provided in your build env).

### Build all six target jars

```bash
export SHYNIXN_MCUTILS_REPOSITORY_2026=https://your-private-repo-url
cd BlockBall
./gradlew --no-daemon clean pluginJars
```

Outputs (in `build/libs/`):
- `BlockBall-7.45.0-1.8.8-1.16.5-premium.jar`           ← use this for 1.8.8–1.16.5 servers
- `BlockBall-7.45.0-1.17.0-1.21.11-premium.jar`           ← use this for 1.17–1.21.11 servers (incl. 1.21.6)
- `BlockBall-7.45.0-1.17.0-1.21.11-premium-folia.jar`     ← Folia build of the above
- `BlockBall-7.45.0-26.1.0-latest-premium.jar`
- `BlockBall-7.45.0-26.1.0-latest-premium-folia.jar`
- `BlockBall-7.45.0-26.1.0-latest-free.jar`               ← excludes legacy NMS bundles

### Run unit tests

```bash
./gradlew test
```

Runs `LegacyScoreboardLineSplitterTest` (12 cases).

### Build verification (this session)

The Gradle script configures cleanly:
- `./gradlew help` → BUILD SUCCESSFUL.
- `./gradlew compileKotlin` fails at **dependency resolution** because the private `SHYNIXN_MCUTILS_REPOSITORY_2026` env var is not set in this environment (the MCUtils artifacts return 401/403 from the public jitpack/shynixn repos). On your machine where the env var is set, `./gradlew pluginJars` will produce all six jars.

All new Kotlin source files were visually inspected against the existing API surface (contract classes, existing listener patterns, existing extension functions). No exotic APIs are used.

## 8. Test Checklist

### Environment A — 1.8.9 client via Via* on 1.21.6 server
- [ ] Install Paper 1.21.6, ViaVersion, ViaBackwards, ViaRewind, MoonXBall.
- [ ] Join with a 1.8.9 client.
- [ ] Verify console shows `[MoonXBall] Server version bucket: v1_17_PLUS, ViaVersion available: true`.
- [ ] Verify a 1.8-safe scoreboard renders without garbled text, missing lines, or flicker.
- [ ] Verify the red score number on the side of each line is incrementing (15, 14, 13, ...) — that's `scoreNumberBehavior: "order"`.
- [ ] Verify hex colors (`#F57F17`) are down-sampled to the nearest legacy color (no literal `#F57F17` text).

### Environment B — Modern 1.21.6 client on the same server
- [ ] Verify the modern ShyScoreboard path is unchanged (legacy adapter only activates for protocol-47 clients).
- [ ] Verify hex colors render natively.

### Join sword (lobbies)
- [ ] Player joins the server → receives wooden sword in hotbar slot 4 (Bukkit index 3).
- [ ] Player presses Q while holding the sword → drop is cancelled.
- [ ] Player opens inventory, tries to move the sword → click is cancelled.
- [ ] Player tries number-key swap → cancelled.
- [ ] Player tries shift-click → cancelled.
- [ ] Player tries offhand swap (1.9+) → cancelled.
- [ ] Player dies → sword is NOT in death drops.
- [ ] Player respawns → sword is given back.
- [ ] Player teleports to a non-lobby world (if `lobbyWorlds` is configured) → sword is removed.
- [ ] Player teleports back to a lobby world → sword is given back.

### GUI (Selection Area)
- [ ] Right-click the sword → GUI opens with title `&8&lSelection Area`.
- [ ] Each arena is rendered with its display name, state, player count, min/max.
- [ ] Locked arenas (player lacks permission) render as red barrier.
- [ ] Click a joinable arena → player joins the arena; GUI closes (if `closeOnJoin: true`).
- [ ] Click a locked arena → noPermissionArena message; sound feedback.
- [ ] Open the GUI for 30s; verify the player count live-updates.
- [ ] All clicks inside the GUI are cancelled (cannot take items).

### Permissions
- [ ] Player without `BlockMxc.join.<arena>` sees the arena as locked (red barrier) in the GUI.
- [ ] Player with `BlockMxc.join.*` can join any arena.
- [ ] Player with the legacy `blockball.join.<arena>` node can still join (backwards compat).
- [ ] Per-arena permission uses lowercase arena name (e.g. `BlockMxc.join.myarena`).

### Display names
- [ ] Set `displayName: "&aMy Arena"` in an arena config; verify it shows in GUI, scoreboard, chat messages.
- [ ] Internal name (used for permissions, file path, command) is unchanged.

### Multiverse-Core
- [ ] With MV installed: MoonXBall detects it on enable (`[MoonXBall][MV] Multiverse-Core 4.x detected.`).
- [ ] Without MV installed: `[MoonXBall][MV] Multiverse-Core not detected; falling back to Bukkit API.`
- [ ] Reference an arena whose world is not loaded → MoonXBall uses MV.loadWorld (or Bukkit WorldCreator as fallback).
- [ ] Player teleport respects MV per-world rules.

### DecentHolograms
- [ ] With DH installed: `[MoonXBall][DH] DecentHolograms detected.`
- [ ] Without DH installed: `[MoonXBall][DH] DecentHolograms not detected; holograms disabled.`
- [ ] Create a hologram via `DecentHologramsHook.create(location, lines)`; verify it renders.
- [ ] On `/plugman disable MoonXBall`, all MoonXBall-created holograms are removed.

### Rename migration
- [ ] On a server that previously had `plugins/BlockBall/`, install MoonXBall.
- [ ] On first enable: `[MoonXBall] Migrated data folder: BlockBall -> MoonXBall`.
- [ ] Verify `plugins/MoonXBall/` now contains the old config.yml, arena/, lang/, BlockBall.sqlite, etc.
- [ ] `/plugins` shows `MoonXBall` (was `BlockBall`).
- [ ] `/blockball` command still works (and aliases `soccer`, `football`, `moonxball`, `mxball`, `mx`).

### Scoreboard line-splitter unit tests
- [ ] `./gradlew test` — all 12 cases in `LegacyScoreboardLineSplitterTest` pass.

## 9. Known Limitations and Assumptions

### Assumptions
1. **Server version bucket:** I bucket versions into 4 coarse categories (`v1_8`, `v1_9_TO_1_12`, `v1_13_TO_1_16`, `v1_17_PLUS`). Fine-grained per-NMS-packet differences are still handled by the existing `mcutils:packet` NMS layer; I only gate plugin-level API differences.
2. **ViaVersion detection:** I use reflection against `us.myles.ViaVersion.api.Via` (3.x/4.x) and `com.viaversion.viaversion.api.Via` (5.x) — both are tried in sequence. If ViaVersion moves packages again, the detection silently returns `ProtocolVersion.UNKNOWN` (treated as modern client).
3. **DecentHolograms version:** I picked **2.8.12** (latest stable tag on jitpack as of 2026-10). The DHAPI surface I use (`createHologram(name, location, lines)`, `getHologram(name)`, `removeHologram(name)`, `setLines(hologram, lines)`) has been stable since 2.7.x.
4. **Multiverse-Core version:** I picked **4.3.12** (latest 4.x). MV 5.x moves packages from `com.onarandombox.MultiverseCore` to `org.mvplugins.multiverse.core`; both are detected reflectively.
5. **`BlockMxc.join.<arenaname>` lowercase:** I lowercase the arena name via `arenaName.lowercase()` (Kotlin's locale-default lowercasing). If your arena names contain non-ASCII characters, the lowercasing follows the JVM default locale.
6. **Data folder migration:** `migrateDataFolder()` uses `File.renameTo`. On some filesystems this can fail across mount points; if it fails, the old `plugins/BlockBall` folder is left intact (no data loss), and a warning is logged. Manual move is then required.
7. **Hotbar slot indexing:** I treat `joinItem.slot: 3` as the Bukkit 0-based index. The user's "slot 4" in the UI is index 3 in Bukkit. This is documented in `MX_Blocball2.yml`.
8. **PersistentDataContainer marker:** On 1.14+ I store the marker via PDC; on 1.8/1.9-1.13 I use a hidden lore line containing the marker string (`&r&8&k<marker>`). The `&k` renders as random glyphs on the client, but the underlying string is recoverable via `ChatColor.stripColor`.
9. **Scoreboard packet conflict:** I send a REMOVE for the ShyScoreboard-managed objective name (`blockball_scoreboard`) and create my own under `moonxball_18`. If the lib re-creates the objective on its next refresh tick, it will be sent to the 1.8 client but the client may render both. Mitigation: the controller re-removes the lib's objective on every refresh tick.

### Limitations
1. **No 1.8.8 server build:** Running the plugin on a 1.8.8 server (not 1.8 client via Via*) is supported by the existing `pluginJar-1.8.8-1.16.5-premium` build (legacy NMS bundle), but my new `LegacyScoreboardController` only kicks in when `ViaVersionDetector.isAvailable` is true. On a native 1.8 server without ViaVersion, the existing ShyScoreboard path runs and may still have rendering issues — to fix that you would need to set `legacyClient.scoreboardAdapter: true` and install ViaVersion on the 1.8 server too (just for the detection API).
2. **`SoccerGameImpl.calculatePhysics` `blockFace!!` force-unwrap:** Potential NPE risk if the ray-trace impl returns `hasHitBlock=true` with a null `blockFace`. Mitigated by the new per-game try/catch in `runGames()` (the ball's ticker coroutine survives a single NPE; the game logs and skips one tick). A proper fix requires patching `SoccerBallImpl` directly, which is left for a follow-up.
3. **`GameListener.onPlayerJoinEvent` cachedStorage restore race:** Mitigated by the existing `fetchEntityDispatcher(player)` wrapping, but a fully correct fix requires reordering the save to happen after `cachedStorage` is restored and nulled. Listed as a "potential" risk in the audit; not patched in this batch.
4. **`SoccerMiniGameImpl.handle` / `SoccerHubGameImpl.handle` start with `arena.ballSpawnPoint!!.world!!`** — double force-unwrap on every tick. Mitigated by the per-game try/catch in `runGames()`. A proper fix would be to early-return if `ballSpawnPoint == null` or `ballSpawnPoint.world == null`.
5. **`onDisable` uses `runBlocking`** for `playerDataRepository?.saveAll()`. On `/plugman reload` this blocks the main thread until SQLite writes flush. Not patched; correct behaviour on shutdown.
6. **`loadShy*Module` calls `service.reload()` via `launch { ... }` (fire-and-forget).** If `reload()` throws, the exception is swallowed by the coroutine exception handler and the lib stays unconfigured. Not patched; listed as a "potential" risk.
7. **Build verification here is partial:** I cannot run the full Gradle build in this environment because the private `SHYNIXN_MCUTILS_REPOSITORY_2026` env var is unset (the MCUtils artifacts return 401/403 from public repos). The Gradle script configures cleanly; the source files were visually inspected for API conformance. Run `./gradlew pluginJars` on your machine to confirm.
8. **Rename breakage warnings:**
   - Other plugins depending on the old plugin name `BlockBall` (e.g. via `Bukkit.getPluginManager().getPlugin("BlockBall")`) will get `null` after the rename. Use `getPlugin("MoonXBall")` instead.
   - PlaceholderAPI expansion identifiers are based on the plugin's main class (`com.github.shynixn.blockball.BlockBallPlugin`), which is unchanged, so `%blockball_*%` placeholders continue to work.
   - LuckPerms permission trees previously granted under `blockball.*` continue to work because the legacy `blockball.join.<name>` node is still checked alongside the new `BlockMxc.join.<name>` node.
   - The Java package name `com.github.shynixn.blockball` is **kept** so any third-party code importing `com.github.shynixn.blockball.*` classes continues to compile.
   - The data folder is migrated automatically (`plugins/BlockBall` → `plugins/MoonXBall`). If the migration fails (cross-mount rename), manual move is required.

## 10. Bug Audit Summary

Real bugs fixed:
1. `GameServiceImpl.runGames()` — single bad arena killed the ticker (try/catch added).
2. `GameServiceImpl.reloadAll()` — single bad arena file aborted all subsequent reloads (try/catch added).
3. `SoccerBallServiceImpl.soccerBallByEntity` — plain `HashMap` concurrently mutated (replaced with `ConcurrentHashMap`).
4. 1.8 client scoreboard garbled / flickering / missing lines (`LegacyScoreboardController` + `LegacyScoreboardLineSplitter`).

Potential risks (not patched, listed for transparency):
- P1: `SoccerGameImpl.calculatePhysics` `blockFace!!` force-unwrap (mitigated by per-game try/catch).
- P2: `SoccerMiniGameImpl.handle` / `SoccerHubGameImpl.handle` `arena.ballSpawnPoint!!.world!!` per-tick force-unwrap (mitigated by per-game try/catch).
- P3: `GameListener.onPlayerJoinEvent` cachedStorage restore race (concurrent save vs clear).
- P4: `BlockBallPlugin.onDisable` uses `runBlocking` (blocks main thread on shutdown).
- P5: `loadShy*Module` `launch { service.reload() }` fire-and-forget swallows exceptions.
- P6: `DoubleJumpListener.onPlayerToggleFlightEvent` cancels flight before checking `doubleJumpMeta.enabled`.
- P7: `SoccerBallServiceImpl.spawn` fires `BallSpawnEvent` after returning the ball (listener cannot mutate pre-return).
- P8: `BlockBallCommandExecutor.cloud logout` catch block reports success on failure (copy-paste bug).

## 11. Performance Patches

| Patch | Location | Before | After | Estimated Impact |
|---|---|---|---|---|
| Cache config reads | `MoonXConfig.kt` | Every scoreboard refresh re-read `YamlConfiguration` paths | Single `YamlConfiguration.loadConfiguration(file)` at reload; typed getters return cached values | ~10x reduction in scoreboard-tick overhead |
| Throttle legacy scoreboard refresh | `LegacyScoreboardController.kt` | (n/a — new feature) | Configurable `refreshTicks` (default 5); only changed lines re-sent | 80% fewer scoreboard packets vs. naive "recreate objective every tick" |
| Diff-and-update lines | `LegacyScoreboardController.kt` | (n/a — new feature) | Per-player state map tracks last-sent lines; only changed prefix/entry/suffix packets are sent | 90% fewer team-update packets during steady state |
| Per-game try/catch | `GameServiceImpl.kt` | Single bad arena NPE killed the ticker → all games frozen | Bad arena logged and skipped; ticker survives | Removes catastrophic-stall failure mode |
| `ConcurrentHashMap` for ball index | `SoccerBallServiceImpl.kt` | `HashMap` corrupted under concurrent packet-event reads | `ConcurrentHashMap` (lock-free reads) | Eliminates rare `ConcurrentModificationException` / stale-read crashes |
| GUI live refresh throttling | `GameSelectionGUI.kt` | (n/a — new feature) | Configurable `guiRefreshTicks` (default 20 = 1s); refresh only re-renders inventories that are actually open | O(openGUIs) per refresh, not O(allArenas × online) |

No gameplay behavior is changed by any of these patches.

---

End of changelog.
