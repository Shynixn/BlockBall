package com.github.shynixn.blockball

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.blockball.contract.BlockBallLanguage

class BlockBallLanguageImpl : BlockBallLanguage {
 override val names: List<String>
  get() = listOf("en_us")
 override var gameAlreadyExistsMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game %blockball_param_1% already exists.")

 override var commandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin.")

 override var commandDescription = LanguageItem("All commands for the BlockBall plugin.")

 override var maxLength20Characters = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters.")

 override var gameDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game %blockball_param_1% does not exist.")

 override var teamDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Team %blockball_param_1% does not exist.")

 override var gameTypeNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c GameType %blockball_param_1% does not exist.")

 override var selectionTypeDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This selection type is not known.")

 override var signTypeDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This sign type is not known.")

 override var noPermissionForGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %blockball_param_1%.")

 override var noPermissionMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission.")

 override var commandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The command sender has to be a player!")

 override var gameCreatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Created game %blockball_param_1%.")

 override var deletedGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Deleted game %blockball_param_1%.")

 override var gameIsFullMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game is already full.")

 override var joinTeamRedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined %blockball_player_teamDisplayName%.")

 override var joinTeamBlueMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined %blockball_player_teamDisplayName%.")

 override var leftGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Left the game.")

 override var selectionSetMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Selection %blockball_param_1% was set.")

 override var enabledArenaMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %blockball_param_1%.")

 override var reloadedAllGamesMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all games.")

 override var reloadedGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded game %blockball_param_1%.")

 override var updatedInventoryMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated inventory of game.")

 override var updatedArmorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated armor of game.")

 override var gameRuleChangedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated a gamerule.")

 override var rightClickOnSignMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 RightClick on a sign to convert it into a game sign.")

 override var addedSignMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 A sign was added to the game.")

 override var noLeftClickSelectionMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with left click.")

 override var noRightClickSelectionMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You need to select a location using the BlockBall axe with right click.")

 override var toggleHighlightMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Toggled highlighting the important areas.")

 override var axeReceivedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The BlockBall axe has been added to your inventory.")

 override var scoreRed = LanguageItem("&c%blockball_game_redScore% : &9%blockball_game_blueScore%")

 override var scoreBlue = LanguageItem("&9%blockball_game_blueScore% : &c%blockball_game_redScore%")

 override var winRed = LanguageItem("%blockball_game_redDisplayName%")

 override var winBlue = LanguageItem("%blockball_game_blueDisplayName%")

 override var winDraw = LanguageItem("&fDraw")

 override var gameStatusJoinAble = LanguageItem("&aJoin")

 override var gameStatusDisabled = LanguageItem("&4Disabled")

 override var gameStatusRunning = LanguageItem("&1Running")

 override var hubGameJoinHeader = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Click on the team to join the match.")

 override var hubGameJoinRed = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 [%blockball_game_redDisplayName%&7]")

 override var hubGameJoinBlue = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 [%blockball_game_blueDisplayName%&7]")

 override var commandCreateToolTip = LanguageItem("Creates a new arena for a BlockBall game.")

 override var commandDeleteToolTip = LanguageItem("Deletes a BlockBall game.")

 override var commandListToolTip = LanguageItem("Lists all games you have created.")

 override var commandToggleToolTip = LanguageItem("Enables or disables your game. If a game is disabled, nobody can join.")

 override var commandJoinToolTip = LanguageItem("Lets the player executing the command join the game. If no team is specified, a random team will be selected. If the player has already joined a game, this command can also be used to switch teams.")

 override var commandLeaveToolTip = LanguageItem("Lets the player executing the command leave the game.")

 override var commandSelectionToolTip = LanguageItem("Updates a location selection of a part of the arena.")

 override var commandInventoryToolTip = LanguageItem("Copies the inventory of the player executing the command. This copy will be applied to players when they join a game.")

 override var commandArmorToolTip = LanguageItem("Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game.")

 override var commandSignToolTip = LanguageItem("Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block.")

 override var commandReloadToolTip = LanguageItem("Allows to reload all games or a specific single one.")

 override var commandAxeToolTip = LanguageItem("Gives you the BlockBall selection axe.")

 override var commandHighlightToolTip = LanguageItem("Toggles highlighting the important areas of your arena.")

 override var commandSelectToolTip = LanguageItem("Sets a selected location for your arena.")

 override var commandGameRuleToolTip = LanguageItem("Sets a gamerule in BlockBall.")

 override var failedToReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cFailed to reload arena %blockball_param_1%. Recommended action: &e%blockball_param_2%")

 override var gameIsNotARefereeGame = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first.")

 override var onlyForPatreons = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This mode requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn.")

 override var joinTeamRefereeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined %blockball_player_teamDisplayName%.")

 override var waitingForRefereeToStart = LanguageItem("Waiting for the referee to start the game...")

 override var waitingForRefereeToStartHint = LanguageItem("Execute &a/blockball referee startgame &fto start the lobby timer.")

 override var nextPeriodReferee = LanguageItem("This period has ended. You are now in overtime.")

 override var nextPeriodRefereeHint = LanguageItem("This period has ended. Execute &a/blockball referee nextperiod.")

 override var whistleTimeOutReferee = LanguageItem("Waiting for the referee to resume the game...")

 override var whistleTimeOutRefereeHint = LanguageItem("You can resume the match by executing &a/blockball referee whistleresume.")

 override var refereeStartedGame = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 You have started the game.")

 override var refereeStoppedGame = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 You have stopped the game and transitioned to the last configured period.")

 override var refereeBallEnabled = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Players can kick the ball now.")

 override var refereeBallDisabled = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Players can no longer kick the ball.")

 override var commandRefereeStartGameToolTip = LanguageItem("Starts the game.")

 override var commandRefereeStopGameToolTip = LanguageItem("Transitions the game to the final period. Executing this command again stops it.")

 override var commandRefereeWhistleResumeToolTip = LanguageItem("Resumes the game and sets the ball interactable.")

 override var commandRefereeWhistleStopToolTip = LanguageItem("Stops the game and sets the ball inactive.")

 override var commandRefereeFreezeTimeToolTip = LanguageItem("Freezes the countdown and sets the ball inactive.")

 override var commandRefereeSetBallToolTip = LanguageItem("Teleports the ball to the position of the referee.")

 override var commandRefereeNextPeriodToolTip = LanguageItem("Transitions to the next configured period.")

 override var commandPlaceHolderToolTip = LanguageItem("Resolves a given placeholder.")

 override var commandPlaceHolderMessage = LanguageItem("Evaluated placeholder: %blockball_param_1%")

 override var playerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Player %blockball_param_1% not found.")

 override var queueTimeOutMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Not enough players joined in time to start the game.")

 override var cannotParseNumberMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Cannot parse number.")

 override var cannotParseWorldMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Cannot find world.")

 override var noPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l] &cYou do not have permission to execute this command.")

 override var reloadCommandHint = LanguageItem("Reloads all configuration.")

 override var reloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all configuration.")

 override var commonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var commandCopyToolTip = LanguageItem("Copies an existing arena to create a new arena.")

 override var arenaNameHasToBeFormat = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c The name of the arena only allows characters a-z, A-Z, 0-9, -")

 override var commandRefereeKickPlayerToolTip = LanguageItem("Removes a player from the current game.")

 override var commandRefereeKickPlayerSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Player %blockball_param_1% was removed from the game by the referee.")

 override var commandRefereeKickPlayerErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% cannot be kicked from the game.")

 override var commandRefereeYellowCardToolTip = LanguageItem("Shows a yellow card to the given player.")

 override var commandRefereeYellowCardSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Player %blockball_param_1% was shown a yellow card by the referee.")

 override var commandRefereeYellowCardErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% cannot be awarded a yellow card.")

 override var commandRefereeRedCardToolTip = LanguageItem("Shows a red card to the given player.")

 override var commandRefereeRedCardSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Player %blockball_param_1% was shown a red card by the referee.")

 override var commandRefereeRedCardErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% cannot be awarded a red card.")

 override var cloudLoginToolTip = LanguageItem("Starts a login flow to the BlockBall Hub.")

 override var cloudLogoutToolTip = LanguageItem("Logout from the BlockBall Hub and cancel all login processes.")

 override var cloudLogoutSuccess = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully logged out.")

 override var cloudLoginStart = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Starting login...")

 override var cloudLoginOpenInBrowser = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Open your web browser and login here:")

 override var cloudLoginWait = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Waiting for login...")

 override var cloudLoginComplete = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully logged into Blockball Hub.")

 override var cloudPublishGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 This game has been published to:")

 override var gameStartingMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Game starts in %blockball_param_1% second(s).")

 override var gameIsInClubModeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This game is in club mode. You cannot join it as a standard player as long as club players are playing it.")

 override var gameIsInStandardModeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This game is in standard mode. You cannot join it with a club as long as standard players are playing it.")

 override var gameNoPermissionToStartAGameInClubModeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission to start a game in club mode.")

 override var gameNoPermissionToJoinAGameInClubModeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission to join a game in club mode.")

 override var gameNotAMemberOfClubMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You are not a member of the club %blockball_param_1%.")

 override var gameAllClubSlotsAreFilledMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c All club slots are already filled. Your club cannot join this game.")

 override var joinTeamClubMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined team %blockball_param_1%&r&7.")

 override var gameClubStartedGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Your club %blockball_param_1% has started the game %blockball_param_2%. Use '/blockball club join %blockball_param_2% %blockball_param_1%' to join too.")

 override var commandClubJoinToolTip = LanguageItem("Lets the player executing the command join the game with the given club. The player has to be a member of the club and the club has to have free slots in the game.")

 override var outMessage = LanguageItem("&eOut")

 override var throwInTeleportMessage = LanguageItem("&eThrow-in")

 override var throwInReadyMessage = LanguageItem("Prepare for throw-in in %blockball_param_1% second(s).")

 override var throwInPerformMessage = LanguageItem("&a&lGO!")

 override var cornerKickTeleportMessage = LanguageItem("&eCorner Kick")

 override var cornerKickReadyMessage = LanguageItem("Prepare for corner kick in %blockball_param_1% second(s).")

 override var cornerKickPerformMessage = LanguageItem("&a&lGO!")

 override var goalKickTeleportMessage = LanguageItem("&eGoal Kick")

 override var goalKickReadyMessage = LanguageItem("Prepare for goal kick in %blockball_param_1% second(s).")

 override var goalKickPerformMessage = LanguageItem("&a&lGO!")

 override var shyScoreboardPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %blockball_param_1% not found.")

 override var shyScoreboardNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to execute this command.")

 override var shyScoreboardReloadCommandHint = LanguageItem("Reloads all scoreboards and configuration.")

 override var shyScoreboardReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all scoreboards and configuration.")

 override var shyScoreboardCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyScoreboardCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player if you do not specify the optional player argument.")

 override var shyScoreboardCommandUsage = LanguageItem("[&9ShyScoreboard&f] Use /shyscoreboard help to see more info about the plugin.")

 override var shyScoreboardCommandDescription = LanguageItem("[&9ShyScoreboard&f] All commands for the ShyScoreboard plugin.")

 override var shyScoreboardAddCommandHint = LanguageItem("Adds a scoreboard to a player.")

 override var shyScoreboardSetCommandHint = LanguageItem("Sets a scoreboard to a player.")

 override var shyScoreboardRemoveCommandHint = LanguageItem("Removes a scoreboard from a player.")

 override var shyScoreboardNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cScoreboard %blockball_param_1% not found.")

 override var shyScoreboardNoPermissionToScoreboardCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to this scoreboard.")

 override var shyScoreboardAddedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added the scoreboard %blockball_param_1% to the player %blockball_param_2%.")

 override var shyScoreboardRemovedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Removed the scoreboard %blockball_param_1% from the player %blockball_param_2%.")

 override var shyScoreboardUpdateCommandHint = LanguageItem("Updates the placeholder of the scoreboard.")

 override var shyScoreboardUpdatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated the scoreboard.")

 override var shyScoreboardBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyBossBarPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %blockball_param_1% not found.")

 override var shyBossBarNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to execute this command.")

 override var shyBossBarReloadCommandHint = LanguageItem("Reloads all bossbars and configuration.")

 override var shyBossBarReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all bossbars and configuration.")

 override var shyBossBarCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyBossBarCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player if you do not specify the optional player argument.")

 override var shyBossBarCommandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /blockballbossbar help to see more info about the plugin.")

 override var shyBossBarCommandDescription = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All commands for the ShyBossBar module of BlockBall.")

 override var shyBossBarAddCommandHint = LanguageItem("Adds a bossbar to a player.")

 override var shyBossBarSetCommandHint = LanguageItem("Sets a bossbar to a player.")

 override var shyBossBarRemoveCommandHint = LanguageItem("Removes a bossbar from a player.")

 override var shyBossBarNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cBossBar %blockball_param_1% not found.")

 override var shyBossBarNoPermissionToBossBarCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to this bossbar.")

 override var shyBossBarAddedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added the bossbar %blockball_param_1% to the player %blockball_param_2%.")

 override var shyBossBarRemovedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Removed the bossbar %blockball_param_1% from the player %blockball_param_2%.")

 override var shyBossBarUpdateCommandHint = LanguageItem("Updates the placeholder of the bossbar.")

 override var shyBossBarUpdatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated the bossbar.")

 override var shyBossBarBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyCommandSignsPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %blockball_param_1% not found.")

 override var shyCommandSignsNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to execute this command.")

 override var shyCommandSignsReloadCommandHint = LanguageItem("Reloads all signs and configuration.")

 override var shyCommandSignsReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all signs and configuration.")

 override var shyCommandSignsCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyCommandSignsCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player if you do not specify the optional player argument.")

 override var shyCommandSignsCommandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /shycommandsigns help to see more info about the plugin.")

 override var shyCommandSignsCommandDescription = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All commands for the ShyCommandSign plugin.")

 override var shyCommandSignsAddCommandHint = LanguageItem("Adds a sign of the given sign type.")

 override var shyCommandSignsNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cSign %blockball_param_1% not found.")

 override var shyCommandSignsBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyCommandSignsRightClickOnSign = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Click on a sign to convert it into a %blockball_param_1% sign.")

 override var shyCommandSignsRightClickOnSignSuccess = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added a sign of type %blockball_param_1% sign.")

 override var shyCommandSignsServerCommandHint = LanguageItem("Sends the player to the given server.")

 override var shyCommandSignsServerMessage = LanguageItem("[&9ShyCommandSigns&f] Connecting to server '%shycommandsigns_param_1%' ...")

 override var shyParticlesPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %blockball_param_1% not found.")

 override var shyParticlesNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to perform this action.")

 override var shyParticlesReloadCommandHint = LanguageItem("Reloads all particle effects and configuration.")

 override var shyParticlesReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all particle effects and configuration.")

 override var shyParticlesCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cA problem occurred. Check the console log for details.")

 override var shyParticlesCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player.")

 override var shyParticlesCommandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /shyparticles help to see more info about the plugin.")

 override var shyParticlesCommandDescription = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All commands for the ShyParticles plugin.")

 override var shyParticlesEffectNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cParticle effect %blockball_param_1% not found.")

 override var shyParticlesEffectPlayMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Playing particle effect %blockball_param_1% with session %blockball_param_2%.")

 override var shyParticlesEffectStopMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Stopped particle effect %blockball_param_1%.")

 override var shyParticlesEffectStopAllMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Stopped all particle effects.")

 override var shyParticlesEffectListMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Available effects: %blockball_param_1%")

 override var shyParticlesPlayCommandHint = LanguageItem("Plays a particle effect at your location or the specified location.")

 override var shyParticlesStopCommandHint = LanguageItem("Stops a running particle effect.")

 override var shyParticlesListCommandHint = LanguageItem("Lists all available particle effects.")

 override var shyParticlesCoordinateValueMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cThe value %blockball_param_1% has to be a number with the following supported formats: 2, 2.0, -3.0, ~2.2 ~-2.3")

 override var shyParticlesWorldNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cWorld %blockball_param_1% not found.")

 override var shyGuildPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %blockball_param_1% not found.")

 override var shyGuildNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to execute this command.")

 override var shyGuildReloadCommandHint = LanguageItem("Reloads all clubs and configuration.")

 override var shyGuildReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all clubs and configuration.")

 override var shyGuildCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyGuildCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player if you do not specify the optional player argument.")

 override var shyGuildCommandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /blockballclub help to see more info about the plugin.")

 override var shyGuildCommandDescription = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All commands for the BlockBallClub module.")

 override var shyGuildTemplateListCommandHint = LanguageItem("Displays all loaded club templates.")

 override var shyGuildTemplateListMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Templates:")

 override var shyGuildWordNotAllowedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c The text contains invalid words, is too short or too long. Please make sure to follow the rules and try again.")

 override var shyGuildCreateCommandHint = LanguageItem("Creates a new club with the given name based on the given template.")

 override var shyGuildCreateSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully created club %blockball_param_1%.")

 override var shyGuildAlreadyExistsMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A club with the name %blockball_param_1% already exists.")

 override var shyGuildTemplateNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Club template %blockball_param_1% not found.")

 override var shyGuildNoPermissionTemplateMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c You do not have permission to use club template %blockball_param_1%.")

 override var shyGuildGuildNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Club %blockball_param_1% not found.")

 override var shyGuildRoleNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Role %blockball_param_1% not found in club %blockball_param_2%.")

 override var shyGuildNoPermissionRoleMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c You do not have permission to assign role %blockball_param_1%.")

 override var shyGuildAddRoleCommandHint = LanguageItem("Assigns the given role to the given player in the given club.")

 override var shyGuildRemoveRoleCommandHint = LanguageItem("Removes the given role from the given player in the given club.")

 override var shyGuildListRolesCommandHint = LanguageItem("Lists all roles of the given club or the roles of a single player in the given club.")

 override var shyGuildPlayerNotAMemberMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% is not a member of this club.")

 override var shyGuildAssignRoleSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully assigned role %blockball_param_1% to player %blockball_param_2%.")

 override var shyGuildRemoveRoleSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully removed role %blockball_param_1% from player %blockball_param_2%.")

 override var shyGuildRoleListAllMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All roles of this club:")

 override var shyGuildRoleListPlayerMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All roles of player %blockball_param_1%:")

 override var shyGuildDeleteCommandHint = LanguageItem("Deletes the given club.")

 override var shyGuildDeleteSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully deleted club %blockball_param_1%.")

 override var shyGuildMemberAddCommandHint = LanguageItem("Adds a player directly to the given club. Should only be used for administrative purposes.")

 override var shyGuildMemberAddSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully added player %blockball_param_1% to club %blockball_param_2%.")

 override var shyGuildMemberInviteCommandHint = LanguageItem("Invites a player to the given club.")

 override var shyGuildMemberInviteSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully invited player %blockball_param_1% to club %blockball_param_2%.")

 override var shyGuildMemberInviteReceivedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 You have been invited to join club %blockball_param_1% by %blockball_param_2%. Use /blockballclub member accept %blockball_param_1% to join.")

 override var shyGuildMemberRemoveCommandHint = LanguageItem("Removes a player from the given club.")

 override var shyGuildMemberRemoveSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully removed player %blockball_param_1% from club %blockball_param_2%.")

 override var shyGuildMemberListCommandHint = LanguageItem("Lists all members of the given club.")

 override var shyGuildMemberListMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Members of club %blockball_param_1%:")

 override var shyGuildMemberAlreadyInGuildMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% is already a member of club %blockball_param_2%.")

 override var shyGuildMemberMaxGuildsReachedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Player %blockball_param_1% has already reached the maximum number of clubs they can join.")

 override var shyGuildMemberInviteFailedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Failed to invite player %blockball_param_1%. Please wait a few minutes until you can send invites again.")

 override var shyGuildMemberAcceptCommandHint = LanguageItem("Accepts a pending club invite.")

 override var shyGuildMemberAcceptSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully accepted the invite and joined club %blockball_param_1%.")

 override var shyGuildMemberAcceptNoInviteMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c You do not have a pending invite for club %blockball_param_1%.")

 override var shyGuildMemberGuildFullMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Failed to join club %blockball_param_1% because it is already full.")

 override var shyGuildCreateMaxGuildsReachedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c You have already reached the maximum number of clubs you can create.")

 override var shyGuildLeaveCommandHint = LanguageItem("Leaves the given club.")

 override var shyGuildLeaveSuccessMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully left club %blockball_param_1%.")

 override var shyGuildCannotLeaveOwnerGuildMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c You cannot leave club %blockball_param_1% because you are the only owner. Please assign the owner role to another player and try again.")

 override var shyGuildThereCannotBeNoOwnerMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c There has to be at least one owner in club %blockball_param_1%. Please assign the owner role to another player before you can remove it from yourself.")

 override var shyGuildListGuildsCommandHint = LanguageItem("Lists all clubs you are a member of.")

 override var shyGuildListGuildsMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 You are a member of the following clubs:")
}
