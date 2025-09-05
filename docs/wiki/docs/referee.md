# Referee

BlockBall offers a third game mode called ``refereegame``. This game mode is exclusive for **Patreon supporters**. (They are responsible for funding this feature :heart:).

This game mode is intended for server events where you have an admin or moderator control the flow of a BlockBall match by using the ``/blockball referee ....`` subcommands.
Examples:

* The referee can resume/pause matches
* The referee can blow the whistle to block players interacting with the ball
* The referee can place the ball anywhere
* ...


## Setup

### Create a new game with gamerule minigame first

Go to the page [https://shynixn.github.io/BlockBall/wiki/site/game/](https://shynixn.github.io/BlockBall/wiki/site/game/) and perform all steps until you have got a game with gamerule ``minigame.``

### Setting the referee spawnpoint

Move to the location where you want the referee to spawn in the field.

```
/blockball location game1 referee_spawnpoint
```

### Setting the referee lobby spawnpoint

Move to the location where you want the referee to spawn in the lobby.

```
/blockball location game1 referee_lobby
```

### Converting the MINIGAME to a REFEREEGAME

In order to give the referee full control over the game, you need to convert the game type.

```
/blockball gamerule gameType game1 refereegame
```

### Enabling the game

```
/blockball toggle game1
```

## Gameplay

Players can join the game in the same way as before. However, the lobby countdown will not start until a referee has joined the game.

A referee can join the game using the following command:

```
/blockball join game1 referee
```

### Starting the game

``/blockball referee startgame``

### Controlling the game

The referee can control the game using the ``/blockball referee...`` subcommands. You can take a look at the commands beforehand, but they
are also displayed hints for the referee during the game.

### Setting up whistle sounds and clickable items

Currently, there is no way to directly add a whistle sound or execute the referee commands via items using BlockBall.

However, you can still do that by following these steps:

* Install a third party plugin to bind commands to items (they are called ``Command Items plugins``, use google)
* Bind the referee commands ``/blockball referee whistleresume`` and others to items
* Bind the ``/playsound`` vanilla minecraft command to these items to play a whistle sound
* Put the item in your inventory and execute the ``/blockball inventory game1 referee`` command to put that item into the inventory, which is received by the referee on join.

### Showing yellow and red cards

The referee can assign yellow and red cards to players using the following commands:

```
/blockball referee yellowcard <player>
```

```
/blockball referee redcard <player>
```

This does not affect the player in any way. The referee is responsible to track the amount of assigned cards in a game and kick the player once he has received two yellow cards.

```
/blockball referee kickplayer <player>
```

You can customize the scoreboard to display the amount of cards of a player. 
Open the ``/plugins/BlockBall/scoreboard/blockball_scoreboard.yml`` file and add the following line to display  the amount of yellow and red cards of a player.

```
  - "Cards: %blockball_player_cardDisplay%"
```

You alternatively can also display the amount of yellow and red cards.

```
  - "%blockball_player_redCards% Red, %blockball_player_yellowCards% Yellow"
```






