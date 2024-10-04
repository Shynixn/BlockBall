package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.BlockBallLanguage

object BlockBallLanguageImpl : BlockBallLanguage {
  /** &cTeam Red **/
  override var winRedTitle : String = "&cTeam Red"

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team blue. **/
  override var joinTeamBlueMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Successfully joined team blue."

  /** &0&l[&f&lBlockBall&0&l]&7 Players can no longer kick the ball. **/
  override var refereeBallDisabled : String = "&0&l[&f&lBlockBall&0&l]&7 Players can no longer kick the ball."

  /** Waiting for the referee to resume the game... **/
  override var whistleTimeOutReferee : String = "Waiting for the referee to resume the game..."

  /** &0&l[&f&lBlockBall&0&l]&c This selection type is not known. **/
  override var selectionTypeDoesNotExistMessage : String = "&0&l[&f&lBlockBall&0&l]&c This selection type is not known."

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team referee. **/
  override var joinTeamRefereeMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Successfully joined team referee."

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded game %1$1s. **/
  override var reloadedGameMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Reloaded game %1$1s."

  /** &0&l[&f&lBlockBall&0&l]&7 A sign was added to the game. **/
  override var addedSignMessage : String = "&0&l[&f&lBlockBall&0&l]&7 A sign was added to the game."

  /** &9Team Blue &ahas won the match **/
  override var winBlueSubTitle : String = "&9Team Blue &ahas won the match"

  /** Execute &a/blockball referee startgame &fto start the lobby timer. **/
  override var waitingForRefereeToStartHint : String = "Execute &a/blockball referee startgame &fto start the lobby timer."

  /** &0&l[&f&lBlockBall&0&l]&7 Left the game. **/
  override var leftGameMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Left the game."

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded all games. **/
  override var reloadedAllGamesMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Reloaded all games."

  /** &0&l[&f&lBlockBall&0&l]&7 **/
  override var joinSignLine1 : String = "&0&l[&f&lBlockBall&0&l]&7"

  /** %blockball_game_stateDisplayName% **/
  override var joinSignLine2 : String = "%blockball_game_stateDisplayName%"

  /** &1Running **/
  override var gameStatusRunning : String = "&1Running"

  /** Creates a new arena for a BlockBall game. **/
  override var commandCreateToolTip : String = "Creates a new arena for a BlockBall game."

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %1$1s. **/
  override var noPermissionForGameMessage : String = "&0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %1$1s."

  /** &0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin. **/
  override var commandUsage : String = "&0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin."

  /** Transitions the game to the final period. Executing this command again stops it. **/
  override var commandRefereeStopGameToolTip : String = "Transitions the game to the final period. Executing this command again stops it."

  /** %blockball_game_players%/%blockball_game_maxPlayers% **/
  override var joinSignLine3 : String = "%blockball_game_players%/%blockball_game_maxPlayers%"

  /**  **/
  override var joinSignLine4 : String = ""

  /** &c%blockball_game_redScore% : &9%blockball_game_blueScore% **/
  override var scoreRedTitle : String = "&c%blockball_game_redScore% : &9%blockball_game_blueScore%"

  /** &0&l[&f&lBlockBall&0&l]&7 Players can kick the ball now. **/
  override var refereeBallEnabled : String = "&0&l[&f&lBlockBall&0&l]&7 Players can kick the ball now."

  /** Transitions to the next configured period. **/
  override var commandRefereeNextPeriodToolTip : String = "Transitions to the next configured period."

  /** Updates a location selection of a part of the arena. **/
  override var commandSelectionToolTip : String = "Updates a location selection of a part of the arena."

  /** Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  override var commandArmorToolTip : String = "Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game."

  /** &0&l[&f&lBlockBall&0&l]&7 **/
  override var leaveSignLine1 : String = "&0&l[&f&lBlockBall&0&l]&7"

  /** &fLeave **/
  override var leaveSignLine2 : String = "&fLeave"

  /** %blockball_game_players%/%blockball_game_maxPlayers% **/
  override var leaveSignLine3 : String = "%blockball_game_players%/%blockball_game_maxPlayers%"

  /**  **/
  override var leaveSignLine4 : String = ""

  /** &0&l[&f&lBlockBall&0&l]&7 &cFailed to reload arena %1$1s. Recommended action: &e%2$1s **/
  override var failedToReloadMessage : String = "&0&l[&f&lBlockBall&0&l]&7 &cFailed to reload arena %1$1s. Recommended action: &e%2$1s"

  /** 20 **/
  override var winBlueFadeIn : String = "20"

  /** Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block. **/
  override var commandSignToolTip : String = "Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block."

  /** &0&l[&f&lBlockBall&0&l]&c GameType %1$1s does not exist. **/
  override var gameTypeNotExistMessage : String = "&0&l[&f&lBlockBall&0&l]&c GameType %1$1s does not exist."

  /** All commands for the BlockBall plugin. **/
  override var commandDescription : String = "All commands for the BlockBall plugin."

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission. **/
  override var noPermissionMessage : String = "&0&l[&f&lBlockBall&0&l]&c You do not have permission."

  /** &0&l[&f&lBlockBall&0&l]&7 The BlockBall axe has been added to your inventory. **/
  override var axeReceivedMessage : String = "&0&l[&f&lBlockBall&0&l]&7 The BlockBall axe has been added to your inventory."

  /** &cTeam Red &ahas won the match **/
  override var winRedSubTitle : String = "&cTeam Red &ahas won the match"

  /** &0&l[&f&lBlockBall&0&l]&7 Click on the team to join the match. **/
  override var hubGameJoinHeader : String = "&0&l[&f&lBlockBall&0&l]&7 Click on the team to join the match."

  /** &0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters. **/
  override var maxLength20Characters : String = "&0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters."

  /** &cTeam Red %blockball_game_redScore% : &9%blockball_game_blueScore% Team Blue **/
  override var bossBarMessage : String = "&cTeam Red %blockball_game_redScore% : &9%blockball_game_blueScore% Team Blue"

  /** %1$1s scored for &9Team Blue. **/
  override var scoreBlueSubTitle : String = "%1$1s scored for &9Team Blue."

  /** Gives you the BlockBall selection axe. **/
  override var commandAxeToolTip : String = "Gives you the BlockBall selection axe."

  /** Lets the player executing the command join the game. The optional team argument allows to directly join a specific team. If the team is full, the other team will be chosen. If no team is specified, a random team will be selected. **/
  override var commandJoinToolTip : String = "Lets the player executing the command join the game. The optional team argument allows to directly join a specific team. If the team is full, the other team will be chosen. If no team is specified, a random team will be selected."

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s does not exist. **/
  override var gameDoesNotExistMessage : String = "&0&l[&f&lBlockBall&0&l]&c Game %1$1s does not exist."

  /** &0&l[&f&lBlockBall&0&l]&7 &9[Team Blue] **/
  override var hubGameJoinBlue : String = "&0&l[&f&lBlockBall&0&l]&7 &9[Team Blue]"

  /** &9Team Blue **/
  override var winBlueTitle : String = "&9Team Blue"

  /** Toggles highlighting the important areas of your arena. **/
  override var commandHighlightToolTip : String = "Toggles highlighting the important areas of your arena."

  /** Sets a selected location for your arena. **/
  override var commandSelectToolTip : String = "Sets a selected location for your arena."

  /** &0&l[&f&lBlockBall&0&l]&7 Selection %1$1s was set. **/
  override var selectionSetMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Selection %1$1s was set."

  /** Team Red **/
  override var teamRedDisplayName : String = "Team Red"

  /** Sets a gamerule in BlockBall. **/
  override var commandGameRuleToolTip : String = "Sets a gamerule in BlockBall."

  /** &4Disabled **/
  override var gameStatusDisabled : String = "&4Disabled"

  /** This period has ended. Execute &a/blockball referee nextperiod. **/
  override var nextPeriodRefereeHint : String = "This period has ended. Execute &a/blockball referee nextperiod."

  /** &aThe game has ended in a draw **/
  override var winDrawSubTitle : String = "&aThe game has ended in a draw"

  /** 20 **/
  override var winDrawFadeIn : String = "20"

  /** Lists all games you have created. **/
  override var commandListToolTip : String = "Lists all games you have created."

  /** &fDraw **/
  override var winDrawTitle : String = "&fDraw"

  /** &0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first. **/
  override var gameIsNotARefereeGame : String = "&0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first."

  /** 60 **/
  override var scoreBlueStay : String = "60"

  /** Team Blue **/
  override var teamBlueDisplayName : String = "Team Blue"

  /** Resumes the game and sets the ball interactable. **/
  override var commandRefereeWhistleResumeToolTip : String = "Resumes the game and sets the ball interactable."

  /** %1$1s scored for &cTeam Red. **/
  override var scoreRedSubTitle : String = "%1$1s scored for &cTeam Red."

  /** &9%blockball_game_blueScore% : &c%blockball_game_redScore% **/
  override var scoreBlueTitle : String = "&9%blockball_game_blueScore% : &c%blockball_game_redScore%"

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s already exists. **/
  override var gameAlreadyExistsMessage : String = "&0&l[&f&lBlockBall&0&l]&c Game %1$1s already exists."

  /** &0&l[&f&lBlockBall&0&l]&c Team %1$1s does not exist. **/
  override var teamDoesNotExistMessage : String = "&0&l[&f&lBlockBall&0&l]&c Team %1$1s does not exist."

  /** This period has ended. You are now in overtime. **/
  override var nextPeriodReferee : String = "This period has ended. You are now in overtime."

  /** You can resume the match by executing &a/blockball referee whistleresume. **/
  override var whistleTimeOutRefereeHint : String = "You can resume the match by executing &a/blockball referee whistleresume."

  /** 20 **/
  override var scoreRedFadeIn : String = "20"

  /** Stops the game and sets the ball inactive. **/
  override var commandRefereeWhistleStopToolTip : String = "Stops the game and sets the ball inactive."

  /** &0&l[&f&lBlockBall&0&l]&7 You have stopped the game and transitioned to the last configured period. **/
  override var refereeStoppedGame : String = "&0&l[&f&lBlockBall&0&l]&7 You have stopped the game and transitioned to the last configured period."

  /** &0&l[&f&lBlockBall&0&l]&7 Updated armor of game. **/
  override var updatedArmorMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Updated armor of game."

  /** 20 **/
  override var winBlueFadeOut : String = "20"

  /** Teleports the ball to the position of the referee. **/
  override var commandRefereeSetBallToolTip : String = "Teleports the ball to the position of the referee."

  /** &0&l[&f&lBlockBall&0&l]&7 RightClick on a sign to convert it into a game sign. **/
  override var rightClickOnSignMessage : String = "&0&l[&f&lBlockBall&0&l]&7 RightClick on a sign to convert it into a game sign."

  /** Deletes a BlockBall game. **/
  override var commandDeleteToolTip : String = "Deletes a BlockBall game."

  /** Waiting for the referee to start the game... **/
  override var waitingForRefereeToStart : String = "Waiting for the referee to start the game..."

  /** &0&l[&f&lBlockBall&0&l]&7 &c[Team Red] **/
  override var hubGameJoinRed : String = "&0&l[&f&lBlockBall&0&l]&7 &c[Team Red]"

  /** 20 **/
  override var winRedFadeIn : String = "20"

  /** 60 **/
  override var winBlueStay : String = "60"

  /** &0&l[&f&lBlockBall&0&l]&7 Created game %1$1s. **/
  override var gameCreatedMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Created game %1$1s."

  /** 20 **/
  override var winDrawFadeOut : String = "20"

  /** 20 **/
  override var scoreRedFadeOut : String = "20"

  /** 20 **/
  override var scoreBlueFadeOut : String = "20"

  /** Lets the player executing the command leave the game. **/
  override var commandLeaveToolTip : String = "Lets the player executing the command leave the game."

  /** Freezes the countdown and sets the ball inactive. **/
  override var commandRefereeFreezeTimeToolTip : String = "Freezes the countdown and sets the ball inactive."

  /** 60 **/
  override var winRedStay : String = "60"

  /** &0&l[&f&lBlockBall&0&l]&7 Updated a gamerule. **/
  override var gameRuleChangedMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Updated a gamerule."

  /** 60 **/
  override var scoreRedStay : String = "60"

  /** Copies the inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  override var commandInventoryToolTip : String = "Copies the inventory of the player executing the command. This copy will be applied to players when they join a game."

  /** Enables or disables your game. If a game is disabled, nobody can join. **/
  override var commandToggleToolTip : String = "Enables or disables your game. If a game is disabled, nobody can join."

  /** &0&l[&f&lBlockBall&0&l]&7 Toggled highlighting the important areas. **/
  override var toggleHighlightMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Toggled highlighting the important areas."

  /** &0&l[&f&lBlockBall&0&l]&c The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn. **/
  override var gameTypeRefereeOnlyForPatreons : String = "&0&l[&f&lBlockBall&0&l]&c The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn."

  /** &0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %1$1s. **/
  override var enabledArenaMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %1$1s."

  /** &0&l[&f&lBlockBall&0&l]&c This sign type is not known. **/
  override var signTypeDoesNotExistMessage : String = "&0&l[&f&lBlockBall&0&l]&c This sign type is not known."

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team red. **/
  override var joinTeamRedMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Successfully joined team red."

  /** &aJoin **/
  override var gameStatusJoinAble : String = "&aJoin"

  /** &0&l[&f&lBlockBall&0&l]&7 Deleted game %1$1s. **/
  override var deletedGameMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Deleted game %1$1s."

  /** &0&l[&f&lBlockBall&0&l]&c Game is already full. **/
  override var gameIsFullMessage : String = "&0&l[&f&lBlockBall&0&l]&c Game is already full."

  /** Starts the game. **/
  override var commandRefereeStartGameToolTip : String = "Starts the game."

  /** 60 **/
  override var winDrawStay : String = "60"

  /** Allows to reload all games or a specific single one. **/
  override var commandReloadToolTip : String = "Allows to reload all games or a specific single one."

  /** &cTeam Red %blockball_game_redScore% : &9Team Blue %blockball_game_blueScore% **/
  override var hologramMessage : String = "&cTeam Red %blockball_game_redScore% : &9Team Blue %blockball_game_blueScore%"

  /** &0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with left click. **/
  override var noLeftClickSelectionMessage : String = "&0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with left click."

  /** &0&l[&f&lBlockBall&0&l]&7 Updated inventory of game. **/
  override var updatedInventoryMessage : String = "&0&l[&f&lBlockBall&0&l]&7 Updated inventory of game."

  /** &0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with right click. **/
  override var noRightClickSelectionMessage : String = "&0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with right click."

  /** 20 **/
  override var scoreBlueFadeIn : String = "20"

  /** &0&l[&f&lBlockBall&0&l]&c The command sender has to be a player! **/
  override var commandSenderHasToBePlayer : String = "&0&l[&f&lBlockBall&0&l]&c The command sender has to be a player!"

  /** 20 **/
  override var winRedFadeOut : String = "20"

  /** &0&l[&f&lBlockBall&0&l]&7 You have started the game. **/
  override var refereeStartedGame : String = "&0&l[&f&lBlockBall&0&l]&7 You have started the game."
}
