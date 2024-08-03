package com.github.shynixn.blockball.contract

interface BlockBallLanguage {
  /** %2$1s scored for &cTeam Red. **/
  var scoreRedSubTitle : String

  /** &9&l%1$1s **/
  var scoreBlueTitle : String

  /** &cTeam Red **/
  var winRedTitle : String

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s already exists. **/
  var gameAlreadyExistsMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team blue. **/
  var joinTeamBlueMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c Team %1$1s does not exist. **/
  var teamDoesNotExistMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded game %1$1s. **/
  var reloadedGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 A sign was added to the game. **/
  var addedSignMessage : String

  /** &9Team Blue &ahas won the match **/
  var winBlueSubTitle : String

  /** &0&l[&f&lBlockBall&0&l]&7 Left the game. **/
  var leftGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Reloaded all games. **/
  var reloadedAllGamesMessage : String

  /** 20 **/
  var scoreRedFadeIn : String

  /** &1Running **/
  var gameStatusRunning : String

  /** Creates a new arena for a BlockBall game. **/
  var commandCreateToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission to join game %1$1s. **/
  var noPermissionForGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Updated armor of game. **/
  var updatedArmorMessage : String

  /** 20 **/
  var winBlueFadeOut : String

  /** &0&l[&f&lBlockBall&0&l]&7 Use /blockball help to see more info about the plugin. **/
  var commandUsage : String

  /** &0&l[&f&lBlockBall&0&l]&7 RightClick on a sign to convert it into a game sign. **/
  var rightClickOnSignMessage : String

  /** &c&l%1$1s **/
  var scoreRedTitle : String

  /** Deletes a BlockBall game. **/
  var commandDeleteToolTip : String

  /** &c[Team Red] **/
  var hubGameJoinRed : String

  /** 20 **/
  var winRedFadeIn : String

  /** 60 **/
  var winBlueStay : String

  /** &0&l[&f&lBlockBall&0&l]&7 Created game %1$1s. **/
  var gameCreatedMessage : String

  /** Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  var commandArmorToolTip : String

  /** 20 **/
  var winDrawFadeOut : String

  /** 20 **/
  var scoreBlueFadeOut : String

  /** 20 **/
  var scoreRedFadeOut : String

  /** Lets the player executing the command leave the game. **/
  var commandLeaveToolTip : String

  /** 20 **/
  var winBlueFadeIn : String

  /** 60 **/
  var winRedStay : String

  /** Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block. **/
  var commandSignToolTip : String

  /** 60 **/
  var scoreRedStay : String

  /** Copies the inventory of the player executing the command. This copy will be applied to players when they join a game. **/
  var commandInventoryToolTip : String

  /** Enables or disables your game. If a game is disabled, nobody can join. **/
  var commandToggleToolTip : String

  /** All commands for the BlockBall plugin. **/
  var commandDescription : String

  /** &0&l[&f&lBlockBall&0&l]&c This location type is not known. For more locations, open the arena.yml. **/
  var locationTypeDoesNotExistMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c You do not have permission. **/
  var noPermissionMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Game enable state was set to %1$1s. **/
  var enabledArenaMessage : String

  /** &cTeam Red &ahas won the match **/
  var winRedSubTitle : String

  /** Click on the team to join the match. **/
  var hubGameJoinHeader : String

  /** &0&l[&f&lBlockBall&0&l]&c The text length has to be less than 20 characters. **/
  var maxLength20Characters : String

  /** &0&l[&f&lBlockBall&0&l]&c This sign type is not known. **/
  var signTypeDoesNotExistMessage : String

  /** %2$1s scored for &9Team Blue. **/
  var scoreBlueSubTitle : String

  /** &0&l[&f&lBlockBall&0&l]&7 Successfully joined team red. **/
  var joinTeamRedMessage : String

  /** &aJoin **/
  var gameStatusJoinAble : String

  /** Lets the player executing the command join the game. The optional team argument allows to directly join a specific team. If the team is full, the other team will be chosen. If no team is specified, a random team will be selected. **/
  var commandJoinToolTip : String

  /** &0&l[&f&lBlockBall&0&l]&c Game %1$1s does not exist. **/
  var gameDoesNotExistMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Deleted game %1$1s. **/
  var deletedGameMessage : String

  /** &0&l[&f&lBlockBall&0&l]&c Game is already full. **/
  var gameIsFullMessage : String

  /** &9[Team Blue] **/
  var hubGameJoinBlue : String

  /** Updates the location of a part of the arena. **/
  var commandLocationToolTip : String

  /** &9Team Blue **/
  var winBlueTitle : String

  /** 60 **/
  var winDrawStay : String

  /** Allows to reload all games or a specific single one. **/
  var commandReloadToolTip : String

  /** &4Disabled **/
  var gameStatusDisabled : String

  /** &0&l[&f&lBlockBall&0&l]&7 Updated inventory of game. **/
  var updatedInventoryMessage : String

  /** &0&l[&f&lBlockBall&0&l]&7 Location %1$1s was set on %2$1s. **/
  var spawnPointSetMessage : String

  /** 20 **/
  var scoreBlueFadeIn : String

  /** &aThe game has ended in a draw **/
  var winDrawSubTitle : String

  /** &0&l[&f&lBlockBall&0&l]&c The command sender has to be a player! **/
  var commandSenderHasToBePlayer : String

  /** 20 **/
  var winRedFadeOut : String

  /** 20 **/
  var winDrawFadeIn : String

  /** Lists all games you have created. **/
  var commandListToolTip : String

  /** &fDraw **/
  var winDrawTitle : String

  /** 60 **/
  var scoreBlueStay : String
}
