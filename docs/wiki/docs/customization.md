# Advanced Customization

Take your BlockBall arenas to the next level with extensive customization options. This guide covers everything from basic tweaks to advanced configurations using AI assistance.

## 🎯 Overview

BlockBall offers hundreds of configuration options through arena YAML files. These files control everything from game mechanics to visual effects, allowing you to create unique gameplay experiences.

---

## 📁 Configuration Files

### Location
All arena customizations are stored in YAML files located at:
```
/plugins/BlockBall/arena/<arena_name>.yml
```

For example, if your arena is named `game1`, the file will be:
```
/plugins/BlockBall/arena/game1.yml
```

### Editing Workflow
1. **Stop the arena**: `/blockball toggle <arena_name>` (if enabled)
2. **Edit the YAML file** using your preferred text editor
3. **Apply changes**: `/blockball reload <arena_name>`
4. **Test your changes** in-game

!!! tip "Text Editor Recommendations"
    - **Notepad++** (Windows) - Free with YAML syntax highlighting
    - **Visual Studio Code** - Cross-platform with excellent YAML support
    - **Sublime Text** - Lightweight with good plugin support

---

## 🤖 AI-Powered Configuration (Recommended)

### Using ChatGPT for Easy Setup

BlockBall's configuration files contain hundreds of options with detailed comments. Instead of learning every setting manually, you can use AI to make precise changes quickly and accurately.

#### Step-by-Step Process

1. **Upload your arena file** to ChatGPT
2. **Describe your desired changes** in plain English  
3. **Review the AI suggestions** before applying
4. **Apply changes manually** to your file (recommended for safety)

#### Example Conversation Flow

![ChatGPT Arena Configuration](./assets/chatgpt1.png)

![Configuration Analysis](./assets/chatgpt2.png)

![Suggested Changes](./assets/chatgpt3.png)

![Implementation Guide](./assets/chatgpt4.png)

![Final Verification](./assets/chatgpt5.png)

#### Sample AI Prompts

**Basic Modifications:**
```
"Make the game last 10 minutes with 5-minute halftime"
"Increase ball speed by 50% and make it bounce higher"
"Add a 30-second lobby countdown before games start"
```

**Advanced Customizations:**
```
"Create a tournament mode with best-of-3 periods and sudden death overtime"
"Configure different weather effects for each game period"
"Set up automatic team balancing with skill-based matching"
```

**UI and Effects:**
```
"Change team colors to green and purple with matching particle effects"
"Add custom sounds for goals, saves, and game events"
"Configure dynamic scoreboards that update in real-time"
```

### AI Safety Tips

!!! warning "Backup First"
    Always backup your arena files before applying AI-generated changes.

!!! tip "Manual Application"
    While ChatGPT can generate download links, manually applying suggested changes ensures file integrity and gives you better understanding.

!!! info "Verification"
    After applying changes, use `/blockball reload` and test thoroughly before going live.

---

## 🔧 Common Customization Categories

### Game Mechanics
- **Scoring Rules**: Points per goal, win conditions, overtime rules
- **Physics**: Ball speed, gravity, bounce behavior, player knockback
- **Timing**: Game duration, period lengths, countdown timers

### Team Configuration  
- **Team Settings**: Names, colors, maximum players per team
- **Equipment**: Default armor, tools, inventory items
- **Spawn Behavior**: Respawn rules, spawn protection, team switching

### Visual & Audio
- **Particle Effects**: Goal celebrations, ball trails, area markers
- **Sound Effects**: Custom sounds for events, ambient audio
- **Scoreboard Display**: Layout, colors, update frequency

### Permissions & Restrictions
- **Player Abilities**: Flying, breaking blocks, using commands
- **Inventory Management**: Item restrictions, equipment changes
- **Command Access**: Available commands during gameplay

### Integration Features
- **PlaceholderAPI**: Custom placeholders for external plugins
- **Hook Systems**: Integration with economy, permission, and other plugins
- **Statistics**: Player tracking, leaderboards, achievement systems

---

## 📖 Configuration File Structure

### Basic Sections

```yaml
# Arena identification and display
arena:
  name: "MyArena"
  displayName: "&6Golden Stadium"
  
# Game mechanics and rules  
game:
  type: "MINIGAME"  # HUBGAME, MINIGAME, REFEREEGAME
  maxScore: 5
  duration: 600  # seconds
  
# Team configurations
teams:
  red:
    displayName: "&cRed Team"
    maxPlayers: 5
  blue:
    displayName: "&9Blue Team" 
    maxPlayers: 5
    
# Ball physics and behavior
ball:
  size: "NORMAL"
  speed: 1.0
  gravity: 0.7
```

### Advanced Sections

```yaml
# Custom commands executed on events
joinCommands:
  - type: 'SERVER_PER_PLAYER'
    command: '/gamemode adventure %blockball_player_name%'
    
# Particle and visual effects
effects:
  goalEffects: true
  ballTrail: true
  
# Integration with other plugins
hooks:
  placeholderapi: true
  vault: true
```

---

## 🎨 Popular Customization Examples

### Tournament Setup
```yaml
game:
  type: "REFEREEGAME"
  maxScore: 3
  periods:
    - duration: 900  # 15 minutes
    - duration: 900  # 15 minutes  
    - duration: 300  # 5 min overtime if tied
```

### Casual Lobby Game
```yaml
game:
  type: "HUBGAME"
  maxScore: 10
  duration: -1  # Infinite duration
  joinAnytime: true
  autoBalance: true
```

### Speed Soccer Mode
```yaml
ball:
  speed: 2.5
  gravity: 0.3
  size: "SMALL"
players:
  walkSpeed: 0.3  # Faster movement
  jumpBoost: 2
```

---

## 🔄 Testing and Iteration

### Testing Workflow
1. **Make small changes** - Test one feature at a time
2. **Use test server** - Avoid disrupting live gameplay  
3. **Get player feedback** - Real users provide valuable insights
4. **Document changes** - Keep notes on what works well

### Common Issues and Solutions

| Problem | Solution |
|---------|----------|
| Configuration not loading | Check YAML syntax, use online validator |
| Performance issues | Reduce particle effects, optimize timers |
| Player complaints | Gather specific feedback, adjust gradually |
| Conflicting features | Review related settings, check dependencies |

---

## 📚 Further Resources

### Learning More
- **In-game help**: `/blockball help` for command reference
- **Community forums**: Share configurations with other server owners
- **Plugin documentation**: Detailed technical references

### Advanced Topics
- **Custom scripting**: Integration with other plugins
- **Performance optimization**: Large server considerations  
- **Multi-arena management**: Coordinating multiple games
- **Event integration**: Tournament and competition setups

!!! success "Pro Tip"
    Start with small modifications and gradually build complexity. The most successful arena configurations evolve over time based on player feedback and server needs.
