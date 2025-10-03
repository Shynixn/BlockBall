# Developer API

BlockBall provides a comprehensive Developer API that allows other plugins to integrate with and extend BlockBall's functionality. Whether you want to create custom game modes, track statistics, or build tournament systems, the API gives you the tools you need.

!!! warning "Installation Notice"
    The BlockBall API is not currently published to Maven Central or other distribution systems. You must directly reference the `BlockBall.jar` file as a dependency in your development environment.

---

## 🚀 Getting Started

### Plugin Dependencies

Add BlockBall as a soft dependency in your `plugin.yml`:

```yaml
softdepend: [ BlockBall ]
```

!!! tip "Why Soft Dependency?"
    Using `softdepend` ensures your plugin loads properly even if BlockBall isn't installed, allowing you to handle the integration gracefully.

### API Access

The BlockBall API is accessed through Bukkit's Service Manager. All core services are registered automatically when BlockBall loads.

---

## 🎯 Core Services

### SoccerBallFactory

Create and manage soccer balls independent of games.

**Key Features:**
- Create soccer balls at any location
- Configure ball physics and behavior  
- Control ball movement and interactions

### GameService

Manage BlockBall games and player participation.

**Key Features:**
- Access existing games by name
- Add/remove players from teams
- Monitor game states and events
- Control game flow programmatically

---

## 📋 Common Use Cases

### Creating Independent Soccer Balls

Use the `SoccerBallFactory` to spawn soccer balls outside of game contexts:

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

### Managing Player Participation

Programmatically add players to games and teams:
- Access games by name using `GameService`
- Join players to specific teams (`Team.BLUE`, `Team.RED`)
- Handle team balancing and player limits

### Event Handling

BlockBall broadcasts comprehensive events for all game activities. Monitor these events to:
- Track player performance and statistics
- Implement custom game mechanics
- Create tournament systems
- Build leaderboards and achievements

---

## 🔥 Events System

BlockBall provides a rich event system that broadcasts all significant game activities. These events allow you to:

- **Monitor Game Flow**: Track game starts, goals, and completions
- **Player Actions**: Detect ball kicks, goals, and team changes  
- **Custom Logic**: Implement your own game mechanics and rules
- **Statistics**: Build comprehensive player and team statistics

**Available Events:** [View all BlockBall events on GitHub](https://github.com/Shynixn/BlockBall/tree/master/src/main/java/com/github/shynixn/blockball/event)

---

## 💡 Best Practices

### Service Availability

Always check if BlockBall services are available before using them:

```java
SoccerBallFactory factory = Bukkit.getServicesManager().load(SoccerBallFactory.class);
if (factory != null) {
    // BlockBall is available, proceed with API calls
} else {
    // BlockBall not installed or not loaded yet
}
```

### Event Listeners

Register event listeners to respond to BlockBall activities:
- Use appropriate event priorities
- Handle events gracefully if BlockBall becomes unavailable
- Consider performance impact of event handling

### Error Handling

Implement robust error handling when using the API:
- Check for null returns from service methods
- Handle cases where games don't exist
- Validate player states before operations

---

## 🔗 Related Documentation

- **[Game Configuration](game.md)** - Learn how to set up BlockBall arenas
- **[Commands Reference](commands.md)** - Administrative commands for game management
- **[Events and Placeholders](placeholders.md)** - Integration with other plugins
