Placeholders
============

The following placeholders are available in BlockBall and can also be used via PlaceHolderApi.

.. note:: PlaceHolders can be used in Messages, Signs, Scoreboards, BossBars and Holograms.

.. note:: In order to use BlockBall placeholders in external plugins, the player has to be in a BlockBall game or append the id of the arena to the placeholder e.g. "%blockball_game_blueScore_1%".

Placeholderlist
~~~~~~~~~~~~~~~

===================================      =====================================================================================================================================
Placeholder                              Description
===================================      =====================================================================================================================================
%blockball_game_name%                    Id of the game
%blockball_game_displayName%             DisplayName of the game
%blockball_game_maxPlayers%              Max amount of players who can join this game
%blockball_game_players%                 Current amount of players in this game
%blockball_game_redScore%                Score of the red team
%blockball_game_blueScore%               Score of the blue team
%blockball_game_redName%                 DisplayName of the red team
%blockball_game_blueName%                DisplayName of the blue team
%blockball_game_redColor%                Color of the red team
%blockball_game_blueColor%               Color of the blue team
%blockball_game_time%                    Remaining time until the match ends
%blockball_game_lastHitPlayerName%       Name of the player who was the last one to hit the ball, returns an empty text if no one has hit the ball yet
%blockball_game_state%                   Returns JOINABLE,RUNNING,DISABLED
%blockball_game_stateDisplayName%        Returns the state color formatted from the language file
%blockball_game_isEnabled%               true if the game is enabled, false if not
%blockball_game_isJoinAble%              true if the game is joinable, false if not
%blockball_game_remainingPlayers%        Remaining amount of players required to start a match in minigame mode
===================================      =====================================================================================================================================

==================================       ======================================================================
Team PlaceHolder                         Description
==================================       ======================================================================
%blockball_team_name%                    DisplayName of the team.
%blockball_team_color%                   Color of the team
%blockball_team_maxPlayers%              Max amount of player who can join this team
%blockball_team_players%                 Current amount of players in the team
==================================       ======================================================================

==================================       ======================================================================
Player Placeholder                       Description
==================================       ======================================================================
%blockball_player_isInGame%              true if the player is in a game, false if not
%blockball_player_isInTeamRed%           true if the player is in a game and in team red, false if not
%blockball_player_isInTeamBlue%          true if the player is in a game and in team blue, false if not
==================================       ======================================================================

