# PlaceHolders

The following placeholders are available in BlockBall and can also be used via PlaceHolderApi.

!!! note "PlaceHolder Api"
    As BlockBall supports multiple games per server, you need to specify the name of the game in external plugins. You can
    do this by appending the name of the game ``_game1`` ``_mygame``.
    This results into placeholders such as e.g. ``%blockball_game_displayName_game1%``
    or ``%blockball_game_blueScore_mygame%``. This is only relevant in external plugins. For placeholders in BlockBall, you
    can directly use the placeholders below.

| Game Context Placeholders          | Description                                                                                                   |
|------------------------------------|---------------------------------------------------------------------------------------------------------------|
| %blockball_game_name%              | Id of the game                                                                                                |
| %blockball_game_displayName%       | DisplayName of the game                                                                                       |
| %blockball_game_maxPlayers%        | Max amount of players who can join this game                                                                  |
| %blockball_game_players%           | Current amount of players in this game                                                                        |
| %blockball_game_redScore%          | Score of the red team                                                                                         |
| %blockball_game_blueScore%         | Score of the blue team                                                                                        |
| %blockball_game_time%              | Remaining time until the match ends                                                                           |
| %blockball_game_lastHitPlayerName% | Name of the player who was the last one to hit the ball, returns an empty text if no one has hit the ball yet |
| %blockball_game_lastHitPlayerTeam% | DisplayName of the team of the player who was the last one to hit the ball.                                   |
| %blockball_game_state%             | Returns JOINABLE,RUNNING,DISABLED                                                                             |
| %blockball_game_stateDisplayName%  | Returns the state color formatted from the language file                                                      |
| %blockball_game_isEnabled%         | true if the game is enabled, false if not                                                                     |
| %blockball_game_isJoinAble%        | true if the game is joinable, false if not                                                                    |
| %blockball_game_remainingPlayers%  | Remaining amount of players required to start a match in minigame mode                                        |

| Player Context Placeholders     | Description                                                                                                   |
|---------------------------------|---------------------------------------------------------------------------------------------------------------|
| %blockball_player_name%         | Name of the player during a BlockBall event e.g. scoring goal                                                 |
| %blockball_player_isInGame%     | true if the player is in a game, false if not                                                                 |
| %blockball_player_isInTeamRed%  | true if the player is in a game and in team red, false if not                                                 |
| %blockball_player_isInTeamBlue% | true if the player is in a game and in team blue, false if not                                                | |

| Stats Placeholders (Patreon Only)        | Description                                                                       |
|------------------------------------------|-----------------------------------------------------------------------------------|
| %blockball_player_goals%                 | Amount of goals a player has scored                                               |
| %blockball_player_goalsFull%             | Amount of goals a player has scored by playing full games.                        |
| %blockball_player_ownGoals%              | Amount of own goals a player has scored                                           |
| %blockball_player_ownGoalsFull%          | Amount of own goals a player has scored by playing full games.                    |
| %blockball_player_totalGoals%            | Amount of goals and own goals a player has scored                                 |
| %blockball_player_totalGoalsFull%        | Amount of goals and own goals a player has scored by playing full games.          |
| %blockball_player_games%                 | Amount of games a player has started playing                                      |
| %blockball_player_gamesFull%             | Amount of games a player has fully played                                         |
| %blockball_player_wins%                  | Amount of wins a player has got by playing                                        |
| %blockball_player_losses%                | Amount of losses a player has got by playing                                      |
| %blockball_player_draws%                 | Amount of draws a player has got by playing                                       |
| %blockball_player_winrate%               | Ratio between amount of games a player has started playing and wins               |
| %blockball_player_winrateFull%           | Ratio between amount of games a player has fully played and wins                  |
| %blockball_player_goalsPerGame%          | Ratio between amount of games a player has started playing and scored goals       |
| %blockball_player_goalsPerGameFull%      | Ratio between amount of games a player has fully played and scored goals          |
| %blockball_player_ownGoalsPerGame%       | Ratio between amount of games a player has started playing and scored own goals   |
| %blockball_player_ownGoalsPerGameFull%   | Ratio between amount of games a player has fully played and scored own goals      |
| %blockball_player_totalGoalsPerGame%     | Ratio between amount of games a player has started playing and scored total goals |
| %blockball_player_totalGoalsPerGameFull% | Ratio between amount of games a player has fully played and scored total goals    |
