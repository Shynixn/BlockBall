# Interactions

BlockBall offers settings regarding prohibiting commands, destroying blocks, setting certain gameMode, etc. to make it easier for you to customize the experience of your player.

However, you can further customize it by executing custom commands when a player joins or leaves the games.

**arena.yml**

```yaml
joinCommands:
- type: 'SERVER_PER_PLAYER'
  command: '/say Hello %blockball_player_name% '
- type: 'SERVER_PER_PLAYER'
  command: '/experience add %blockball_player_name% 1'
leaveCommands:
- type: 'SERVER_PER_PLAYER'
  command: '/say Bye %blockball_player_name% '
- type: 'SERVER_PER_PLAYER'
  command: '/experience add %blockball_player_name% 1'
```

The type SERVER_PER_PLAYER executes commands using the SERVER level permission, which means players do not have to have the permission to the command. If you want to execute commands using the PLAYER level permission, use PER_PLAYER.

You need to configure this for each team. Each team can have its own join and leave commands.

## Permission Based Interactions

The right way to prohibit certain commands and actions during games is to use a permission plugin to configure it. The most popular plugin [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/) can be used for that.

#### Create a new group called blockball

```
/luckperms creategroup blockball
```

#### Add all your permissions you want to allow or not allow during games to this group

!!! note "Important"
    Examples are permissions to certain commands, scoreboards or bossbar plugins. This allows displaying scores only during BlockBall games.

You can use the web editor or the following command. As long as the players are inside that group, they can only do (e.g. not break blocks) and see (e.g. scoreboards) what you have configured.

#### Add a new join command which adds the player to the group while he is in the match

**arena.yml**

```yaml
joinCommands:
- type: 'SERVER_PER_PLAYER'
  command: '/lp user %blockball_player_name% parent add blockball'
```

#### Add a new leave command which removes the player from the group when he quits the match

**arena.yml**

```yaml
leaveCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/lp user %blockball_player_name% parent remove blockball'
```

#### Set the player to certain states

For example, if you want to set the player to gamemode adventure during games, add another command called:

```yaml
joinCommands:
- type: 'SERVER_PER_PLAYER'
  command: '/lp user %blockball_player_name% parent add blockball'
- type: 'SERVER_PER_PLAYER'
  command: '/gamemode adventure %blockball_player_name%
```

## Region Based Interactions

Another way to prohibit certain commands and actions during games is to use a region plugin to configure it.

Put a region around the arena and the lobby with certain flags to disable destroying the arena.
