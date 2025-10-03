# Api

BlockBall offers a Developer Api, however it is not published to Maven Central or any other distribution system yet.
You need to directly reference the BlockBall.jar file.

## Usage

Add a dependency in your plugin.yml

```yaml
softdepend: [ BlockBall ]
```

Take a look at the following example:
```java
public class YourPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Player player = Bukkit.getPlayer("YourPlayerName");

        // Creating a soccer ball independent of a game.
        SoccerBallFactory soccerBallFactory = Bukkit.getServicesManager().load(SoccerBallFactory.class);
        SoccerBall soccerBall = soccerBallFactory.createSoccerBall(player.getLocation(), new SoccerBallSettings());
        // The is no way to directly set the velocity, but you can kick it relative to the position the player.
        soccerBall.kickByPlayer(player);

        // Letting a player join a game.
        GameService gameService = Bukkit.getServicesManager().load(GameService.class);
        SoccerGame game = gameService.getByName("myGameName");
        game.join(player, Team.BLUE);
    }
}
```

BlockBall broadcasts the following [events.](https://github.com/Shynixn/BlockBall/tree/master/src/main/java/com/github/shynixn/blockball/event)
