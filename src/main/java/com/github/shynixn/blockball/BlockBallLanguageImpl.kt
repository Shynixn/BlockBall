package com.github.shynixn.blockball

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.LanguageProviderImpl
import com.github.shynixn.blockball.contract.Language

class BlockBallLanguageImpl : Language, LanguageProviderImpl() {
 override val names: List<String>
  get() = listOf("en_us", "es_es")
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

 override var joinTeamRedMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined team red.")

 override var joinTeamBlueMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined team blue.")

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

 override var bossBarMessage = LanguageItem("&cTeam Red %blockball_game_redScore% : &9%blockball_game_blueScore% Team Blue")

 override var hologramMessage = LanguageItem("&cTeam Red %blockball_game_redScore% : &9Team Blue %blockball_game_blueScore%")

 override var scoreRed = LanguageItem("&c%blockball_game_redScore% : &9%blockball_game_blueScore%")

 override var scoreBlue = LanguageItem("&9%blockball_game_blueScore% : &c%blockball_game_redScore%")

 override var winRed = LanguageItem("&cTeam Red")

 override var winBlue = LanguageItem("&9Team Blue")

 override var winDraw = LanguageItem("&fDraw")

 override var gameStatusJoinAble = LanguageItem("&aJoin")

 override var gameStatusDisabled = LanguageItem("&4Disabled")

 override var gameStatusRunning = LanguageItem("&1Running")

 override var hubGameJoinHeader = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Click on the team to join the match.")

 override var hubGameJoinRed = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &c[Team Red]")

 override var hubGameJoinBlue = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 &9[Team Blue]")

 override var commandCreateToolTip = LanguageItem("Creates a new arena for a BlockBall game.")

 override var commandDeleteToolTip = LanguageItem("Deletes a BlockBall game.")

 override var commandListToolTip = LanguageItem("Lists all games you have created.")

 override var commandToggleToolTip = LanguageItem("Enables or disables your game. If a game is disabled, nobody can join.")

 override var commandJoinToolTip = LanguageItem("Lets the player executing the command join the game. The optional team argument allows to directly join a specific team. If the team is full, the other team will be chosen. If no team is specified, a random team will be selected.")

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

 override var teamRedDisplayName = LanguageItem("Team Red")

 override var teamBlueDisplayName = LanguageItem("Team Blue")

 override var gameIsNotARefereeGame = LanguageItem("&0&l[&f&lBlockBall&0&l]&c This game is not a game where you can use a referee. Convert the game to a referee game first.")

 override var gameTypeRefereeOnlyForPatreons = LanguageItem("&0&l[&f&lBlockBall&0&l]&c The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn.")

 override var joinTeamRefereeMessage = LanguageItem("&0&l[&f&lBlockBall&0&l]&7 Successfully joined team referee.")

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
}
