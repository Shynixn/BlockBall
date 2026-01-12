# BlockBall Hub Cloud Integration

Share your BlockBall games with the world! The BlockBall Hub is a cloud service that automatically publishes game results and player statistics to a dedicated website.

Check out the demo on [https://blockball.shynixn.com](https://blockball.shynixn.com)

!!! warning "Patreon Exclusive"
    This feature is available exclusively to active Patreon supporters. [Become a patron](https://www.patreon.com/shynixn) to unlock cloud features. This feature is only available as long as you are subscribed.

## 🎯 Overview

BlockBall Hub allows you to:

- **Automatically publish game results** from your Minecraft server to the web
- **Track player statistics** with detailed breakdowns and leaderboards
- **Share games publicly** with a personal server page
- **Custom domain integration** (Elite & Legendary tiers only)

Your BlockBall Hub server is accessible at:
```
https://blockball.shynixn.com/games?s=<yourserverid>
```

---

## 🚀 Setup Guide

### Step 1: Register Your Server

1. Visit the [BlockBall Hub](https://blockball.shynixn.com)
2. Navigate to [Registration](https://blockball.shynixn.com/info)
3. Login with your **Patreon account**
4. Complete registration to create your personal server page

✅ **Success!** Your unique server URL will be generated automatically.

### Step 2: Connect Your Minecraft Server

Link your Minecraft server to your BlockBall Hub account:

```bash
/blockball cloud login
```

**What happens next:**
- You'll be prompted to authenticate with the **same Patreon account** used during registration
- Follow the authentication instructions in chat
- Your server will be securely connected to BlockBall Hub

!!! tip "Authentication"
    Make sure you're using the same Patreon account on both the website and in-game to ensure proper connection.

### Step 3: Enable Cloud Publishing for Arenas

Enable cloud publishing for individual arenas by editing their configuration files.

**File Location:**
```
/plugins/BlockBall/arena/<arena_name>.yml
```

**Configuration:**

1. Open your arena YAML file in a text editor
2. Find the `cloud:` section
3. Set `enabled: true`
4. Save the file
5. Reload the arena: `/blockball reload <arena_name>`

**Example Configuration:**
```yaml
cloud:
  enabled: true
  # Optional: additional cloud properties
```

!!! tip "Optional Properties"
    The cloud section supports additional configuration options. Check the YAML file for available settings.

### Step 4: Play and Publish

That's it! Now just play normally:

1. **Start a game** on your arena
2. **Play through to completion**
3. **Results are automatically published** to your BlockBall Hub server
4. **A publish link** is sent to you in ingame chat

---

## 📊 Features

### Game Publishing

Every completed game is automatically uploaded with:
- Final scores and match results
- Team compositions
- Game duration and timing
- Arena information

### Player Statistics

Player performance data is tracked and published:
- Individual player stats per game
- Cumulative statistics across all games
- Detailed performance breakdowns
- Leaderboards and rankings

### Public Access

Share your BlockBall Hub server URL with:
- Players on your server
- Your community website
- Social media followers
- Anyone interested in your server's games

---

## 👑 Premium Features

### Custom Domain Integration

**Available for:** Elite & Legendary Patreon Tiers

Instead of using the default BlockBall Hub URL, integrate the service into your own domain:

**Default URL:**
```
https://blockball.shynixn.com/games?s=<yourserverid>
```

**Custom Domain:**
```
http://soccer.yourcustomdomain.com/games
```

**Benefits:**
- Private server configuration
- Branded experience matching your server
- Professional appearance for your community
- Seamless integration with existing website

!!! info "Custom Domain Setup"
    Contact support through Patreon for assistance with custom domain configuration.

---

## 🔧 Troubleshooting

### Game Not Publishing

**Check these common issues:**

1. **Cloud login status:**  
   Run `/blockball cloud login` and verify authentication

2. **Arena cloud setting:**  
   Ensure `enabled: true` in arena YAML file

3. **Game completion:**  
   Only completed games are published (not manually stopped)

4. **Server connectivity:**  
   Check internet connection and firewall settings

### Authentication Issues

- Verify you're using the **same Patreon account** on both website and server
- Check that your Patreon subscription is active. The Basic tier is required
- Try logging out and logging in again: `/blockball cloud logout` then `/blockball cloud login`

### Statistics Not Updating

- Allow a few minutes for statistics to process
- Refresh your browser page
- Verify the game published successfully on your server page

---

## 📚 Additional Resources

- **Patreon:** [Support BlockBall development](https://www.patreon.com/shynixn)
- **BlockBall Hub:** [Visit your server page](https://blockball.shynixn.com)
- **Terms of Service:** [Terms of Service](https://blockball.shynixn.com/info)
