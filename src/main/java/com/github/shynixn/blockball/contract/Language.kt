package com.github.shynixn.blockball.contract

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.LanguageProvider

interface Language : LanguageProvider {
  var gameAlreadyExistsMessage: LanguageItem

  var commandUsage: LanguageItem

  var commandDescription: LanguageItem

  var maxLength20Characters: LanguageItem

  var gameDoesNotExistMessage: LanguageItem

  var teamDoesNotExistMessage: LanguageItem

  var gameTypeNotExistMessage: LanguageItem

  var selectionTypeDoesNotExistMessage: LanguageItem

  var signTypeDoesNotExistMessage: LanguageItem

  var noPermissionForGameMessage: LanguageItem

  var noPermissionMessage: LanguageItem

  var commandSenderHasToBePlayer: LanguageItem

  var gameCreatedMessage: LanguageItem

  var deletedGameMessage: LanguageItem

  var gameIsFullMessage: LanguageItem

  var joinTeamRedMessage: LanguageItem

  var joinTeamBlueMessage: LanguageItem

  var leftGameMessage: LanguageItem

  var selectionSetMessage: LanguageItem

  var enabledArenaMessage: LanguageItem

  var reloadedAllGamesMessage: LanguageItem

  var reloadedGameMessage: LanguageItem

  var updatedInventoryMessage: LanguageItem

  var updatedArmorMessage: LanguageItem

  var gameRuleChangedMessage: LanguageItem

  var rightClickOnSignMessage: LanguageItem

  var addedSignMessage: LanguageItem

  var noLeftClickSelectionMessage: LanguageItem

  var noRightClickSelectionMessage: LanguageItem

  var toggleHighlightMessage: LanguageItem

  var axeReceivedMessage: LanguageItem

  var bossBarMessage: LanguageItem

  var hologramMessage: LanguageItem

  var scoreRed: LanguageItem

  var scoreBlue: LanguageItem

  var winRed: LanguageItem

  var winBlue: LanguageItem

  var winDraw: LanguageItem

  var gameStatusJoinAble: LanguageItem

  var gameStatusDisabled: LanguageItem

  var gameStatusRunning: LanguageItem

  var hubGameJoinHeader: LanguageItem

  var hubGameJoinRed: LanguageItem

  var hubGameJoinBlue: LanguageItem

  var commandCreateToolTip: LanguageItem

  var commandDeleteToolTip: LanguageItem

  var commandListToolTip: LanguageItem

  var commandToggleToolTip: LanguageItem

  var commandJoinToolTip: LanguageItem

  var commandLeaveToolTip: LanguageItem

  var commandSelectionToolTip: LanguageItem

  var commandInventoryToolTip: LanguageItem

  var commandArmorToolTip: LanguageItem

  var commandSignToolTip: LanguageItem

  var commandReloadToolTip: LanguageItem

  var commandAxeToolTip: LanguageItem

  var commandHighlightToolTip: LanguageItem

  var commandSelectToolTip: LanguageItem

  var commandGameRuleToolTip: LanguageItem

  var joinSignLine1: LanguageItem

  var joinSignLine2: LanguageItem

  var joinSignLine3: LanguageItem

  var joinSignLine4: LanguageItem

  var leaveSignLine1: LanguageItem

  var leaveSignLine2: LanguageItem

  var leaveSignLine3: LanguageItem

  var leaveSignLine4: LanguageItem

  var failedToReloadMessage: LanguageItem

  var teamRedDisplayName: LanguageItem

  var teamBlueDisplayName: LanguageItem

  var gameIsNotARefereeGame: LanguageItem

  var gameTypeRefereeOnlyForPatreons: LanguageItem

  var joinTeamRefereeMessage: LanguageItem

  var waitingForRefereeToStart: LanguageItem

  var waitingForRefereeToStartHint: LanguageItem

  var nextPeriodReferee: LanguageItem

  var nextPeriodRefereeHint: LanguageItem

  var whistleTimeOutReferee: LanguageItem

  var whistleTimeOutRefereeHint: LanguageItem

  var refereeStartedGame: LanguageItem

  var refereeStoppedGame: LanguageItem

  var refereeBallEnabled: LanguageItem

  var refereeBallDisabled: LanguageItem

  var commandRefereeStartGameToolTip: LanguageItem

  var commandRefereeStopGameToolTip: LanguageItem

  var commandRefereeWhistleResumeToolTip: LanguageItem

  var commandRefereeWhistleStopToolTip: LanguageItem

  var commandRefereeFreezeTimeToolTip: LanguageItem

  var commandRefereeSetBallToolTip: LanguageItem

  var commandRefereeNextPeriodToolTip: LanguageItem

  var commandPlaceHolderToolTip: LanguageItem

  var commandPlaceHolderMessage: LanguageItem
}
