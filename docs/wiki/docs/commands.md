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
/blockball join <name> [team]
```

Lets the player executing the command join the game. The optional team argument allows to directly join a specific team.
If the team is full, the other team will be chosen. If no team is specified, a random team will be selected.

* Name: Identifier of a game
* Team: Name of the team. Is always red or blue.

### /blockball leave

```
/blockball leave
```

Lets the player executing the command leave the game.

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

### /blockball sign

```
/blockball sign <name> <type>
```

Enables the player to add a specific sign by right-clicking any sign. You can remove signs by simply breaking the block.

* Name: Identifier of a game
* Type: Type of sign to create. Possible values: join, leave, team_red, team_blue

### /blockball reload

```
/blockball reload [name]
```

Allows to reload all games or a specific single one.

* Name: Optional identifier of a game
