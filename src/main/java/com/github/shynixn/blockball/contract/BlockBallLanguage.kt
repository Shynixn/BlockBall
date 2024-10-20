package com.github.shynixn.blockball.contract

interface BlockBallLanguage {
  /** &cTeam Red **/
  var winRedTitle : String

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team blue. **/
  var joinTeamBlueMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Players can no longer kick the ball. **/
  var refereeBallDisabled : String

  /** Waiting for the referee to resume the game... **/
  var whistleTimeOutReferee : String

  /** &0&l[&f&lBlockBall&0&l]&c This selection type is not known. **/
  var selectionTypeDoesNotExistMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team referee. **/
  var joinTeamRefereeMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded game %1$1s. **/
  var reloadedGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 A sign was added to the game. **/
  var addedSignMessage : String

  /** &9Team Blue &ahas won the match **/
  var winBlueSubTitle : String

  /** Execute &a/blockball referee startgame &fto start the lobby timer. **/
  var waitingForRefereeToStartHint : String

  /** &0&l[&f&lBlockBall&0&l]&7 Left the game. **/
  var leftGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded all games. **/
  var reloadedAllGamesMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 **/
  var joinSignLine1 : String

  /** %blockball_game_stateDisplayName% **/
  var joinSignLine2 : String

  /** &1Running **/
  var gameStatusRunning : String

  /** Creates a new arena for a BlockBall game. **/
  var commandCreateToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %1$1s. **/
  var noPermissionForGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin. **/
  var commandUsage : String

  /** Transitions the game to the final period. Executing this command again stops it. **/
  var commandRefereeStopGameToolTip : String

  /** %blockball_game_players%/%blockball_game_maxPlayers% **/
  var joinSignLine3 : String

  /**  **/
  var joinSignLine4 : String

  /** &c%blockball_game_redScore% : &9%blockball_game_blueScore% **/
  var scoreRedTitle : String

  /** &0&l[&f&lBlockBall&0&l]&7 Players can kick the ball now. **/
  var refereeBallEnabled : String

  /** Transitions to the next configured period. **/
  var commandRefereeNextPeriodToolTip : String

  /** Updates a location selection of a part of the arena. **/
  var commandSelectionToolTip : String

  /** Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  var commandArmorToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 **/
  var leaveSignLine1 : String

  /** &fLeave **/
  var leaveSignLine2 : String

  /** %blockball_game_players%/%blockball_game_maxPlayers% **/
  var leaveSignLine3 : String

  /**  **/
  var leaveSignLine4 : String

  /** &0&l[&f&lBlockBall&0&l]&7 &cFailed to reload arena %1$1s. Recommended action: &e%2$1s **/
  var failedToReloadMessage : String

  /** 20 **/
  var winBlueFadeIn : String

  /** Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block. **/
  var commandSignToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&c GameType %1$1s does not exist. **/
  var gameTypeNotExistMessage : String

  /** All commands for the BlockBall plugin. **/
  var commandDescription : String

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission. **/
  var noPermissionMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 The BlockBall axe has been added to your inventory. **/
  var axeReceivedMessage : String

  /** &cTeam Red &ahas won the match **/
  var winRedSubTitle : String

  /** &0&l[&f&lBlockBall&0&l]&7 Click on the team to join the match. **/
  var hubGameJoinHeader : String

  /** &0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters. **/
  var maxLength20Characters : String

  /** &cTeam Red %blockball_game_redScore% : &9%blockball_game_blueScore% Team Blue **/
  var bossBarMessage : String

  /** %1$1s scored for &9Team Blue. **/
  var scoreBlueSubTitle : String

  /** Gives you the BlockBall selection axe. **/
  var commandAxeToolTip : String

  /** Lets the player executing the command join the game. The optional team argument allows to directly join a specific team. If the team is full, the other team will be chosen. If no team is specified, a random team will be selected. **/
  var commandJoinToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s does not exist. **/
  var gameDoesNotExistMessage : String

  /** Evaluated placeholder: %1$1s **/
  var commandPlaceHolderMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 &9[Team Blue] **/
  var hubGameJoinBlue : String

  /** &9Team Blue **/
  var winBlueTitle : String

  /** Toggles highlighting the important areas of your arena. **/
  var commandHighlightToolTip : String

  /** Sets a selected location for your arena. **/
  var commandSelectToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 Selection %1$1s was set. **/
  var selectionSetMessage : String

  /** Team Red **/
  var teamRedDisplayName : String

  /** Sets a gamerule in BlockBall. **/
  var commandGameRuleToolTip : String

  /** &4Disabled **/
  var gameStatusDisabled : String

  /** This period has ended. Execute &a/blockball referee nextperiod. **/
  var nextPeriodRefereeHint : String

  /** &aThe game has ended in a draw **/
  var winDrawSubTitle : String

  /** 20 **/
  var winDrawFadeIn : String

  /** Lists all games you have created. **/
  var commandListToolTip : String

  /** &fDraw **/
  var winDrawTitle : String

  /** &0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first. **/
  var gameIsNotARefereeGame : String

  /** 60 **/
  var scoreBlueStay : String

  /** Team Blue **/
  var teamBlueDisplayName : String

  /** Resumes the game and sets the ball interactable. **/
  var commandRefereeWhistleResumeToolTip : String

  /** %1$1s scored for &cTeam Red. **/
  var scoreRedSubTitle : String

  /** &9%blockball_game_blueScore% : &c%blockball_game_redScore% **/
  var scoreBlueTitle : String

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s already exists. **/
  var gameAlreadyExistsMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c Team %1$1s does not exist. **/
  var teamDoesNotExistMessage : String

  /** This period has ended. You are now in overtime. **/
  var nextPeriodReferee : String

  /** You can resume the match by executing &a/blockball referee whistleresume. **/
  var whistleTimeOutRefereeHint : String

  /** 20 **/
  var scoreRedFadeIn : String

  /** Stops the game and sets the ball inactive. **/
  var commandRefereeWhistleStopToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 You have stopped the game and transitioned to the last configured period. **/
  var refereeStoppedGame : String

  /** Resolves a given placeholder. **/
  var commandPlaceHolderToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 Updated armor of game. **/
  var updatedArmorMessage : String

  /** 20 **/
  var winBlueFadeOut : String

  /** Teleports the ball to the position of the referee. **/
  var commandRefereeSetBallToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 RightClick on a sign to convert it into a game sign. **/
  var rightClickOnSignMessage : String

  /** Deletes a BlockBall game. **/
  var commandDeleteToolTip : String

  /** Waiting for the referee to start the game... **/
  var waitingForRefereeToStart : String

  /** &0&l[&f&lBlockBall&0&l]&7 &c[Team Red] **/
  var hubGameJoinRed : String

  /** 20 **/
  var winRedFadeIn : String

  /** 60 **/
  var winBlueStay : String

  /** &0&l[&f&lBlockBall&0&l]&7 Created game %1$1s. **/
  var gameCreatedMessage : String

  /** 20 **/
  var winDrawFadeOut : String

  /** 20 **/
  var scoreRedFadeOut : String

  /** 20 **/
  var scoreBlueFadeOut : String

  /** Lets the player executing the command leave the game. **/
  var commandLeaveToolTip : String

  /** Freezes the countdown and sets the ball inactive. **/
  var commandRefereeFreezeTimeToolTip : String

  /** 60 **/
  var winRedStay : String

  /** &0&l[&f&lBlockBall&0&l]&7 Updated a gamerule. **/
  var gameRuleChangedMessage : String

  /** 60 **/
  var scoreRedStay : String

  /** Copies the inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  var commandInventoryToolTip : String

  /** Enables or disables your game. If a game is disabled, nobody can join. **/
  var commandToggleToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&7 Toggled highlighting the important areas. **/
  var toggleHighlightMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn. **/
  var gameTypeRefereeOnlyForPatreons : String

  /** &0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %1$1s. **/
  var enabledArenaMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c This sign type is not known. **/
  var signTypeDoesNotExistMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team red. **/
  var joinTeamRedMessage : String

  /** &aJoin **/
  var gameStatusJoinAble : String

  /** &0&l[&f&lBlockBall&0&l]&7 Deleted game %1$1s. **/
  var deletedGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c Game is already full. **/
  var gameIsFullMessage : String

  /** Starts the game. **/
  var commandRefereeStartGameToolTip : String

  /** 60 **/
  var winDrawStay : String

  /** Allows to reload all games or a specific single one. **/
  var commandReloadToolTip : String

  /** &cTeam Red %blockball_game_redScore% : &9Team Blue %blockball_game_blueScore% **/
  var hologramMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with left click. **/
  var noLeftClickSelectionMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Updated inventory of game. **/
  var updatedInventoryMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with right click. **/
  var noRightClickSelectionMessage : String

  /** 20 **/
  var scoreBlueFadeIn : String

  /** &0&l[&f&lBlockBall&0&l]&c The command sender has to be a player! **/
  var commandSenderHasToBePlayer : String

  /** 20 **/
  var winRedFadeOut : String

  /** &0&l[&f&lBlockBall&0&l]&7 You have started the game. **/
  var refereeStartedGame : String
}
