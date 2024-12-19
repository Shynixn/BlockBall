# Commands

The following commands are available in BlockBall. You can access them by typing:

```
/blockball help 1
```

### /blockball create

```
/blockball create <name> <displayName>
```

Creates a new arena for a BlockBall game.

* Name: Identifier of a game
* DisplayName: Arbitrary Display for your game. Can contain ChatColors.

### /blockball delete

```
/blockball delete <name>
```

Deletes a BlockBall game.

* Name: Identifier of a game

### /blockball list

```
/blockball list [player]
```

Lists all games you have created.

### /blockball toggle

```
/blockball toggle <name>
```

Enables or disables your game. If a game is disabled, nobody can join.

* Name: Identifier of a game

### /blockball join

```
/blockball join <name> [team] [player]
```

Lets the player executing the command join the game. If no team is specified, a random team will be selected.
If the player has already joined a game, this command can also be used to switch teams. 

* Name: Identifier of a game
* Team: Optional name of the team. Is ``red``, ``blue`` or ``referee``.
* Player: Optional name of the player to join. Requires the ``blockball.edit`` permission to use.

### /blockball leave

```
/blockball leave [player]
```

Lets the player executing the command leave the game.

* Player: Optional name of the player to leave. Requires the ``blockball.edit`` permission to use.

### /blockball axe

```
/blockball axe
```

Adds the BlockBall axe for selection to your inventory.

### /blockball select

```
/blockball select <name>
```

Sets a area for your arena.

* Name: Identifier of a game
* Type: Type of selection to set: e.g. field, blue_goal, red_goal. See tab completion for all values.

### /blockball location

```
/blockball location <name>
```

Sets a location for your arena.

* Name: Identifier of a game
* Type: Type of location to set: ball, red_spawnpoint, blue_spawnpoint, leave_spawnpoint


### /blockball highlight

```
/blockball highlight <name>
```

Starts highlighting your arena. When executing this command a second time, you stop highlighting an arena.

* Name: Identifier of a game

### /blockball inventory

```
/blockball inventory <name> <team>
```

Copies the inventory of the player executing the command. This copy will be applied to players when they join a game.

* Name: Identifier of a game
* Team: Name of the team. Is always red or blue.

### /blockball armor

```
/blockball armor <name> <team>
```

Copies the armor inventory of the player executing the command. This copy will be applied to players when they join a game.

* Name: Identifier of a game
* Team: Name of the team. Is always red or blue.

### /blockball gamerule

```
/blockball gamerule <key> <name> <value>
```

Sets a gamerule of BlockBall.

* Key: The name of the gamerule.
* Name: Identifier of a game
* Value: Value of a gamerule. See tab completions for all values.

### /blockball sign

```
/blockball sign <name> <type>
```

Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block.

* Name: Identifier of a game
* Type: Type of sign to create. Possible values: join, leave, team_red, team_blue

### /blockball referee startgame

```
/blockball referee startgame
```

Starts the lobby countdown of a game.

### /blockball referee stop

```
/blockball referee stop
```

Transitions the game to the final period. Executing this command again stops it.

### /blockball referee setball

```
/blockball referee setball
```

Teleports the ball to the position of the referee.

### /blockball referee whistleresume

```
/blockball referee whistleresume
```

Resumes the game and sets the ball interactable.

### /blockball referee whistlestop

```
/blockball referee whistlestop
```

Stops the game and sets the ball inactive.

### /blockball referee freezetime

```
/blockball referee freezetime
```

Freezes the countdown and sets the ball inactive.

### /blockball referee nextperiod

```
/blockball referee nextperiod
```

Transitions to the next configured period.

### /blockball placeholder

```
/blockball placeholder <placeholder>
```

Tries to resolve the value of a given placeholder.

* PlaceHolder: PlaceHolder


### /blockball reload

```
/blockball reload [name]
```

Allows to reload all games or a specific single one.

* Name: Optional identifier of a game
