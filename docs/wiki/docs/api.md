# Developer API

BlockBall provides a comprehensive API for developers to integrate with the plugin programmatically.

!!! warning "Installation Required"
    The API is not published to Maven Central. You must reference the BlockBall.jar file directly in your project.

---

## 🚀 Quick Setup

### 1. Plugin Dependency

Add BlockBall as a soft dependency in your `plugin.yml`:

```yaml
softdepend: [ BlockBall ]
```

### 2. Basic Usage

Access BlockBall services through Bukkit's ServiceManager:

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

---

## 🎯 Core Services

| Service | Purpose |
|---------|---------|
| **GameService** | Manage games, get game instances, player management |
| **SoccerBallFactory** | Create and manage soccer balls outside of games |

---

## 📡 Event System

BlockBall broadcasts various events that you can listen to:

- **Game Events**: Start, end, goal scored, player join/leave
- **Ball Events**: Kick, pass, interaction
- **Player Events**: Team switch, respawn

View all available events in the [GitHub repository](https://github.com/Shynixn/BlockBall/tree/master/src/main/java/com/github/shynixn/blockball/event).

---

## 📚 Documentation

For detailed API documentation and method signatures, refer to the BlockBall source code on GitHub or use your IDE's intellisense when the plugin is loaded.
