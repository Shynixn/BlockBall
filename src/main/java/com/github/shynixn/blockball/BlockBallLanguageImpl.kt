package com.github.shynixn.blockball

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.blockball.contract.BlockBallLanguage

class BlockBallLanguageImpl : BlockBallLanguage {
 override val names: List<String>
  get() = listOf("en_us")
 override var gameAlreadyExistsMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game %1$1s already exists.")

 override var commandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin.")

 override var commandDescription = LanguageItem("All commands for the BlockBall plugin.")

 override var maxLength20Characters = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters.")

 override var gameDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game %1$1s does not exist.")

 override var teamDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Team %1$1s does not exist.")

 override var gameTypeNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c GameType %1$1s does not exist.")

 override var selectionTypeDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This selection type is not known.")

 override var signTypeDoesNotExistMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This sign type is not known.")

 override var noPermissionForGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %1$1s.")

 override var noPermissionMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c You do not have permission.")

 override var commandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The command sender has to be a player!")

 override var gameCreatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Created game %1$1s.")

 override var deletedGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Deleted game %1$1s.")

 override var gameIsFullMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Game is already full.")

 override var joinTeamRedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined %blockball_player_teamDisplayName%.")

 override var joinTeamBlueMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined %blockball_player_teamDisplayName%.")

 override var leftGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Left the game.")

 override var selectionSetMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Selection %1$1s was set.")

 override var enabledArenaMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %1$1s.")

 override var reloadedAllGamesMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all games.")

 override var reloadedGameMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded game %1$1s.")

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

 override var joinSignLine1 = LanguageItem("&0&l[&f&lBlockBall&0&l]&7")

 override var joinSignLine2 = LanguageItem("%blockball_game_stateDisplayName%")

 override var joinSignLine3 = LanguageItem("%blockball_game_players%/%blockball_game_maxPlayers%")

 override var joinSignLine4 = LanguageItem("")

 override var leaveSignLine1 = LanguageItem("&0&l[&f&lBlockBall&0&l]&7")

 override var leaveSignLine2 = LanguageItem("&fLeave")

 override var leaveSignLine3 = LanguageItem("%blockball_game_players%/%blockball_game_maxPlayers%")

 override var leaveSignLine4 = LanguageItem("")

 override var failedToReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cFailed to reload arena %1$1s. Recommended action: &e%2$1s")

 override var gameIsNotARefereeGame = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first.")

 override var gameTypeRefereeOnlyForPatreons = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn.")

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

 override var commandPlaceHolderMessage = LanguageItem("Evaluated placeholder: %1$1s")

 override var playerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Player %1$1s not found.")

 override var queueTimeOutMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Not enough players joined in time to start the game.")

 override var cannotParseNumberMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Cannot parse number.")

 override var cannotParseWorldMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&c Cannot find world.")

 override var noPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l] &cYou do not have permission to execute this command.")

 override var reloadCommandHint = LanguageItem("Reloads all configuration.")

 override var reloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all configuration.")

 override var commonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyScoreboardPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %1$1s not found.")

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

 override var shyScoreboardNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cScoreboard %1$1s not found.")

 override var shyScoreboardNoPermissionToScoreboardCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to this scoreboard.")

 override var shyScoreboardAddedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added the scoreboard %1$1s to the player %2$1s.")

 override var shyScoreboardRemovedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Removed the scoreboard %1$1s from the player %2$1s.")

 override var shyScoreboardUpdateCommandHint = LanguageItem("Updates the placeholder of the scoreboard.")

 override var shyScoreboardUpdatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated the scoreboard.")

 override var shyScoreboardBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyBossBarPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %1$1s not found.")

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

 override var shyBossBarNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cBossBar %1$1s not found.")

 override var shyBossBarNoPermissionToBossBarCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to this bossbar.")

 override var shyBossBarAddedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added the bossbar %1$1s to the player %2$1s.")

 override var shyBossBarRemovedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Removed the bossbar %1$1s from the player %2$1s.")

 override var shyBossBarUpdateCommandHint = LanguageItem("Updates the placeholder of the bossbar.")

 override var shyBossBarUpdatedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Updated the bossbar.")

 override var shyBossBarBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyCommandSignsPlayerNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cPlayer %1$1s not found.")

 override var shyCommandSignsNoPermissionCommand = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cYou do not have permission to execute this command.")

 override var shyCommandSignsReloadCommandHint = LanguageItem("Reloads all signs and configuration.")

 override var shyCommandSignsReloadMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Reloaded all signs and configuration.")

 override var shyCommandSignsCommonErrorMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c A problem occurred. Check the console log for details.")

 override var shyCommandSignsCommandSenderHasToBePlayer = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 The command sender has to be a player if you do not specify the optional player argument.")

 override var shyCommandSignsCommandUsage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Use /shycommandsigns help to see more info about the plugin.")

 override var shyCommandSignsCommandDescription = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 All commands for the ShyCommandSign plugin.")

 override var shyCommandSignsAddCommandHint = LanguageItem("Adds a sign of the given sign type.")

 override var shyCommandSignsNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &cSign %1$1s not found.")

 override var shyCommandSignsBooleanNotFoundMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7&c Only true and false are allowed as values.")

 override var shyCommandSignsRightClickOnSign = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Click on a sign to convert it into a %1$1s sign.")

 override var shyCommandSignsRightClickOnSignSuccess = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Added a sign of type %1$1s sign.")
}
