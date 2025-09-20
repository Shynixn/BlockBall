# PlaceHolders

The following placeholders are available in BlockBall and can also be used via PlaceHolderApi.

!!! note "PlaceHolder Api"
    As BlockBall supports multiple games per server, you need to specify the name of the game in external plugins. You can
    do this by appending the name of the game ``_game1`` ``_mygame``.
    This results into placeholders such as e.g. ``%blockball_game_displayName_game1%``
    or ``%blockball_game_blueScore_mygame%``. This is only relevant in external plugins. For placeholders in BlockBall, you
    can directly use the placeholders below.

| Game Context Placeholders                | Description                                                                 |
|------------------------------------------|-----------------------------------------------------------------------------|
| %blockball_game_name%                    | Id of the game                                                              |
| %blockball_game_displayName%             | DisplayName of the game                                                     |
| %blockball_game_maxPlayers%              | Max amount of players who can join this game                                |
| %blockball_game_players%                 | Current amount of players in this game                                      |
| %blockball_game_redDisplayName%          | Name of team red                                                            |
| %blockball_game_redScore%                | Score of the red team                                                       |
| %blockball_game_redPlayers%              | Amount of players who are currently in the red team                         |
| %blockball_game_redMaxPlayers%           | Max amount of players who can join the red team                             |
| %blockball_game_blueDisplayName%         | Name of team blue                                                           |
| %blockball_game_blueScore%               | Score of the blue team                                                      |
| %blockball_game_bluePlayers%             | Amount of players who are currently in the blue team                        |
| %blockball_game_blueMaxPlayers%          | Max amount of players who can join the blue team                            |
| %blockball_game_time%                    | Remaining time until the match ends                                         |
| %blockball_game_lastHitPlayerName%       | Name of the player who was the last one to hit the ball                     |
| %blockball_game_lastHitPlayerTeam%       | DisplayName of the team of the player who was the last one to hit the ball. |
| %blockball_game_secondLastHitPlayerName% | Name of the player who was the second to last to thit the ball              |
| %blockball_game_secondLastHitPlayerTeam% | DisplayName of the team of the player who was the second to last to hit the ball. 
| %blockball_game_state%                   | Returns JOINABLE,RUNNING,DISABLED                                           |
| %blockball_game_stateDisplayName%        | Returns the state color formatted from the language file                    |
| %blockball_game_isEnabled%               | true if the game is enabled, false if not                                   |
| %blockball_game_isJoinAble%              | true if the game is joinable, false if not                                  |
| %blockball_game_remainingPlayers%        | Remaining amount of players required to start a match in minigame mode      |
| %blockball_game_refereeDisplayName%      | Name of team referee                                                        |

| Player Context Placeholders             | Description                                                    |
|-----------------------------------------|----------------------------------------------------------------|
| %blockball_player_name%                 | Name of the player during a BlockBall event e.g. scoring goal  |
| %blockball_player_team%                 | Name of the team the player is currently in: red, blue         |
| %blockball_player_teamDisplayName%      | Display name of the team the player is currently in.           |   
| %blockball_player_isInGame%             | true if the player is in a game, false if not                  |
| %blockball_player_isInTeamRed%          | true if the player is in a game and in team red, false if not  |
| %blockball_player_isInTeamBlue%         | true if the player is in a game and in team blue, false if not |
| %blockball_player_distanceOwnGoal%      | Distance from the player to the goal of his own team           |
| %blockball_player_distanceEnemyGoal%    | Distance from the player to the goal of the enemy team         |
| %blockball_player_distanceTeamBlueGoal% | Distance from the player to the goal of team blue              |
| %blockball_player_distanceTeamRedGoal%  | Distance from the player to the goal of team red               |
| %blockball_player_cardDisplay%          | Displays the assigned cards from the referee                   |
| %blockball_player_yellowCards%          | Displays the amount of assigned yellow cards from the referee  |
| %blockball_player_redCards%             | Displays the amount of assigned red cards from the referee     |

| Stats Placeholders (Patreon Only)        | Description                                                                       |
|------------------------------------------|-----------------------------------------------------------------------------------|
| %blockball_player_goals%                 | Amount of goals a player has scored                                               |
| %blockball_player_goalsFull%             | Amount of goals a player has scored by playing full games.                        |
| %blockball_player_goalsCurrent%          | Amount of goals a player has scored during the current game.                      |
| %blockball_player_ownGoals%              | Amount of own goals a player has scored                                           |
| %blockball_player_ownGoalsFull%          | Amount of own goals a player has scored by playing full games.                    |
| %blockball_player_ownGoalsCurrent%       | Amount of own goals a player has scored during the current game.                  |
| %blockball_player_totalGoals%            | Amount of goals and own goals a player has scored                                 |
| %blockball_player_totalGoalsFull%        | Amount of goals and own goals a player has scored by playing full games.          |
| %blockball_player_totalGoalsCurrent%     | Amount of goals and own goals a player has scored during the current game.        |
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

!!! note "LeaderBoard"
Replace **top_1** with **top_2** or **top_3** etc. to build a leaderboard.

| LeaderBoard Placeholders (Patreon Only)                 | Description                                                                                  |
|---------------------------------------------------------|----------------------------------------------------------------------------------------------|
| %blockball_leaderboard_goals_name_top_1%                | The name of the player which has the most amount of scored goals                             |
| %blockball_leaderboard_goals_value_top_1%               | The most amount of scored goals                                                              |
| %blockball_leaderboard_goalsFull_name_top_1%            | The name of the player which has the most amount of scored goals by playing full games       |
| %blockball_leaderboard_goalsFull_value_top_1%           | The most amount of scored goals by playing full games                                        |
| %blockball_leaderboard_ownGoals_name_top_1%             | The name of the player which has the most amount of own goals                                |
| %blockball_leaderboard_ownGoals_value_top_1%            | The most amount of own goals                                                                 |
| %blockball_leaderboard_ownGoalsFull_name_top_1%         | The name of the player which has the most amount of own goals by playing full games          |
| %blockball_leaderboard_ownGoalsFull_value_top_1%        | The most amount of own goals by playing full games                                           |
| %blockball_leaderboard_totalGoals_name_top_1%           | The name of the player which has the most amount of total goals                              |
| %blockball_leaderboard_totalGoals_value_top_1%          | The most amount of total goals                                                               |
| %blockball_leaderboard_totalGoalsFull_name_top_1%       | The name of the player which has the most amount of total goals by playing full games        |
| %blockball_leaderboard_totalGoalsFull_value_top_1%      | The most amount of total goals by playing full games                                         |
| %blockball_leaderboard_games_name_top_1%                | The name of the player which has the most amount of joined games                             |
| %blockball_leaderboard_games_value_top_1%               | The most amount of joined games                                                              |
| %blockball_leaderboard_gamesFull_name_top_1%            | The name of the player which has the most amount of fully played games                       |
| %blockball_leaderboard_gamesFull_value_top_1%           | The most amount of fully played games                                                        |
| %blockball_leaderboard_wins_name_top_1%                 | The name of the player which has the most amount of wins                                     |
| %blockball_leaderboard_wins_value_top_1%                | The most amount wins                                                                         |
| %blockball_leaderboard_losses_name_top_1%               | The name of the player which has the most amount of losses                                   |
| %blockball_leaderboard_losses_value_top_1%              | The most amount losses                                                                       |
| %blockball_leaderboard_draws_name_top_1%                | The name of the player which has the most amount of draws                                    |
| %blockball_leaderboard_draws_value_top_1%               | The most amount draws                                                                        |
| %blockball_leaderboard_winrate_name_top_1%              | The name of the player which has the highest winrate                                         |
| %blockball_leaderboard_winrate_value_top_1%             | The highest winrate                                                                          |
| %blockball_leaderboard_goalsPerGame_name_top_1%         | The name of the player which has the most amount of goals per game                           |
| %blockball_leaderboard_goalsPerGame_value_top_1%        | The most amount of goals per game                                                            |
| %blockball_leaderboard_goalsPerGameFull_name_top_1%     | The name of the player which has the most amount of goals per game by playing full games     |
| %blockball_leaderboard_goalsPerGameFull_value_top_1%    | The most amount of goals per game by playing full games                                      |
| %blockball_leaderboard_ownGoalsPerGame_name_top_1%      | The name of the player which has the most amount of own goals per game                       |
| %blockball_leaderboard_ownGoalsPerGame_value_top_1%     | The most amount of own goals per game                                                        |
| %blockball_leaderboard_ownGoalsPerGameFull_name_top_1%  | The name of the player which has the most amount of own goals per game by playing full games |
| %blockball_leaderboard_ownGoalsPerGameFull_value_top_1% | The most amount of own goals per game by playing full games                                  |
