# PlaceholderAPI Integration

BlockBall provides extensive placeholder support for dynamic content in scoreboards, chat messages, signs, and other plugins. This guide covers all available placeholders and how to use them effectively.

## 🔧 Setup Requirements

BlockBall integrates seamlessly with **PlaceholderAPI** (PAPI), one of the most popular Bukkit plugins for dynamic text replacement.

### Installation
1. Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) on your server
2. Install BlockBall (placeholders work automatically)
3. Use placeholders in any PAPI-compatible plugin

### Basic Usage
In any PAPI-compatible context, use BlockBall placeholders like this:
```
%blockball_game_redScore%
%blockball_player_name%
%blockball_game_displayName%
```

---

## 🎯 Multi-Arena Support

!!! important "Arena-Specific Placeholders"
    Since BlockBall supports multiple arenas per server, external plugins need to specify which arena's data to display.

### External Plugin Format
When using placeholders in external plugins (like scoreboards, chat plugins, etc.), append the arena name:

```
%blockball_game_displayName_<arena_name>%
%blockball_game_redScore_stadium1%
%blockball_game_blueScore_mygame%
```

### Internal BlockBall Usage
Within BlockBall configurations (arena files, signs, etc.), use the base format:
```
%blockball_game_displayName%
%blockball_game_redScore%
```

---

## 🏟️ Game Context Placeholders

These placeholders provide information about the arena and current game state.

### Basic Arena Information

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_game_name%` | Arena identifier | `stadium1` |
| `%blockball_game_displayName%` | Formatted arena name | `&6Golden Stadium` |
| `%blockball_game_maxPlayers%` | Total player capacity | `10` |
| `%blockball_game_players%` | Current players in arena | `6` |

### Team Information

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_game_redDisplayName%` | Red team name | `&cFire Team` |
| `%blockball_game_redScore%` | Red team's current score | `3` |
| `%blockball_game_redPlayers%` | Players in red team | `3` |
| `%blockball_game_redMaxPlayers%` | Red team capacity | `5` |
| `%blockball_game_blueDisplayName%` | Blue team name | `&9Water Team` |
| `%blockball_game_blueScore%` | Blue team's current score | `2` |
| `%blockball_game_bluePlayers%` | Players in blue team | `3` |
| `%blockball_game_blueMaxPlayers%` | Blue team capacity | `5` |
| `%blockball_game_refereeDisplayName%` | Referee team name | `&7Officials` |

### Game Status

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_game_time%` | Remaining time | `5:30` |
| `%blockball_game_state%` | Current state | `RUNNING` |
| `%blockball_game_stateDisplayName%` | Colored state | `&aRunning` |
| `%blockball_game_isEnabled%` | Arena enabled status | `true` |
| `%blockball_game_isJoinAble%` | Can players join | `false` |
| `%blockball_game_remainingPlayers%` | Players needed to start | `2` |

### Ball Information

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_game_lastHitPlayerName%` | Last player to touch ball | `Steve` |
| `%blockball_game_lastHitPlayerTeam%` | That player's team | `&cRed Team` |
| `%blockball_game_secondLastHitPlayerName%` | Second-to-last player | `Alex` |
| `%blockball_game_secondLastHitPlayerTeam%` | That player's team | `&9Blue Team` |

---

## 👤 Player Context Placeholders

These placeholders provide information about individual players during BlockBall events.

### Basic Player Info

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_player_name%` | Player's name | `Steve` |
| `%blockball_player_team%` | Team identifier | `red` |
| `%blockball_player_teamDisplayName%` | Formatted team name | `&cFire Team` |

### Player Status

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_player_isInGame%` | In any BlockBall game | `true` |
| `%blockball_player_isInTeamRed%` | In red team | `true` |
| `%blockball_player_isInTeamBlue%` | In blue team | `false` |

### Positional Data

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_player_distanceOwnGoal%` | Distance to own goal | `15.7` |
| `%blockball_player_distanceEnemyGoal%` | Distance to enemy goal | `42.3` |
| `%blockball_player_distanceTeamBlueGoal%` | Distance to blue goal | `28.1` |
| `%blockball_player_distanceTeamRedGoal%` | Distance to red goal | `35.6` |

### Referee System

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%blockball_player_cardDisplay%` | Visual card display | `🟨🟨🟥` |
| `%blockball_player_yellowCards%` | Yellow card count | `2` |
| `%blockball_player_redCards%` | Red card count | `1` |

---

## 📊 Statistics Placeholders (Patreon Feature)

!!! note "Premium Feature"
    These placeholders require a Patreon subscription and provide detailed player statistics tracking.

### Goal Statistics

| Placeholder | Description | Scope |
|-------------|-------------|-------|
| `%blockball_player_goals%` | Total goals scored | All-time |
| `%blockball_player_goalsFull%` | Goals in completed games | All-time |
| `%blockball_player_goalsCurrent%` | Goals in current game | Current |
| `%blockball_player_ownGoals%` | Own goals scored | All-time |
| `%blockball_player_ownGoalsFull%` | Own goals in completed games | All-time |
| `%blockball_player_ownGoalsCurrent%` | Own goals in current game | Current |
| `%blockball_player_totalGoals%` | All goals + own goals | All-time |
| `%blockball_player_totalGoalsFull%` | Total in completed games | All-time |
| `%blockball_player_totalGoalsCurrent%` | Total in current game | Current |

### Game Statistics

| Placeholder | Description |
|-------------|-------------|
| `%blockball_player_games%` | Games started |
| `%blockball_player_gamesFull%` | Games completed |
| `%blockball_player_wins%` | Games won |
| `%blockball_player_losses%` | Games lost |
| `%blockball_player_draws%` | Games drawn |

### Calculated Ratios

| Placeholder | Description |
|-------------|-------------|
| `%blockball_player_winrate%` | Win percentage (all games) |
| `%blockball_player_winrateFull%` | Win percentage (completed only) |
| `%blockball_player_goalsPerGame%` | Average goals per game |
| `%blockball_player_goalsPerGameFull%` | Average goals (completed games) |
| `%blockball_player_ownGoalsPerGame%` | Average own goals per game |
| `%blockball_player_ownGoalsPerGameFull%` | Average own goals (completed) |
| `%blockball_player_totalGoalsPerGame%` | Average total goals per game |
| `%blockball_player_totalGoalsPerGameFull%` | Average total (completed) |

---

## 🏆 Leaderboard Placeholders (Patreon Feature)

Create dynamic leaderboards using these placeholders. Replace `top_1` with `top_2`, `top_3`, etc., for different rankings.

### Goal Leaderboards

| Placeholder | Description |
|-------------|-------------|
| `%blockball_leaderboard_goals_name_top_1%` | Top goal scorer name |
| `%blockball_leaderboard_goals_value_top_1%` | Top goal scorer count |
| `%blockball_leaderboard_goalsFull_name_top_1%` | Top scorer (completed games) |
| `%blockball_leaderboard_goalsFull_value_top_1%` | Their goal count |

### Game Performance Leaderboards

| Placeholder | Description |
|-------------|-------------|
| `%blockball_leaderboard_wins_name_top_1%` | Most wins player |
| `%blockball_leaderboard_wins_value_top_1%` | Their win count |
| `%blockball_leaderboard_winrate_name_top_1%` | Highest win rate player |
| `%blockball_leaderboard_winrate_value_top_1%` | Their win rate |
| `%blockball_leaderboard_games_name_top_1%` | Most active player |
| `%blockball_leaderboard_games_value_top_1%` | Their game count |

### Performance Ratios

| Placeholder | Description |
|-------------|-------------|
| `%blockball_leaderboard_goalsPerGame_name_top_1%` | Best goals/game ratio |
| `%blockball_leaderboard_goalsPerGame_value_top_1%` | Their ratio |
| `%blockball_leaderboard_goalsPerGameFull_name_top_1%` | Best ratio (completed) |
| `%blockball_leaderboard_goalsPerGameFull_value_top_1%` | Their ratio |

---

## 💡 Practical Usage Examples

### Scoreboard Integration
```yaml
# In your scoreboard plugin configuration
lines:
  - "&6=== &lBlockBall &6==="
  - "&cRed: &f%blockball_game_redScore% &7| &9Blue: &f%blockball_game_blueScore%"
  - "&7Time: &f%blockball_game_time%"
  - "&7Players: &f%blockball_game_players%/%blockball_game_maxPlayers%"
  - ""
  - "&7Last Hit: &f%blockball_game_lastHitPlayerName%"
```

### Chat Announcements
```yaml
# Goal scored message
- "%blockball_player_name% &ahas scored for %blockball_player_teamDisplayName%!"
- "&7Score: &c%blockball_game_redScore% &7- &9%blockball_game_blueScore%"
```

### Sign Display
```yaml
# Join sign format
lines:
  - "&6[BlockBall]"
  - "%blockball_game_displayName%"
  - "&7Players: %blockball_game_players%/%blockball_game_maxPlayers%"
  - "%blockball_game_stateDisplayName%"
```

### Statistics Display
```yaml
# Player stats (Patreon)
- "&6Your Stats:"
- "&7Goals: &f%blockball_player_goals%"
- "&7Win Rate: &f%blockball_player_winrate%%"
- "&7Games: &f%blockball_player_games%"
```

---

## 🔍 Testing Placeholders

Use the built-in command to test placeholder values:

```bash
/blockball placeholder %blockball_game_redScore%
/blockball placeholder %blockball_player_name%
```

This helps verify that placeholders are working correctly and returning expected values.

---

## ⚠️ Troubleshooting

### Common Issues

| Problem | Solution |
|---------|----------|
| Placeholder shows as text | Install PlaceholderAPI |
| Empty/null values | Check arena name suffix for external plugins |
| Outdated information | Use `/blockball reload` to refresh |
| Statistics not working | Requires Patreon version |

### Best Practices

!!! tip "Performance"
    - Avoid excessive placeholder usage in high-frequency updates
    - Cache placeholder values when possible
    - Use specific placeholders rather than checking everything

!!! info "Formatting"
    - Placeholders return raw values - apply formatting in your display system
    - Color codes work in `displayName` placeholders
    - Numeric values can be formatted using your scoreboard/chat plugin

This comprehensive placeholder system allows you to create rich, dynamic displays that enhance the BlockBall experience for your players!
