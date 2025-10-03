# Bedrock Edition Support

BlockBall fully supports **cross-platform gameplay** between Java Edition and Bedrock Edition clients (mobile, console, Windows 10/11) through [GeyserMC](https://geysermc.org/). This allows players on any device to enjoy BlockBall together!

!!! info "Cross-Platform Compatibility"
    Bedrock players can join Java servers running BlockBall using GeyserMC, enabling true cross-platform soccer matches between PC, mobile, and console players.

---

## 🎯 What You'll Need

### Requirements

- **Java Server**: Spigot, Paper, or compatible server
- **BlockBall Plugin**: Installed on your Java server
- **GeyserMC**: Translation layer for Bedrock clients
- **Optional**: Patreon resources for enhanced visual experience

### Supported Platforms

✅ **Java Edition** (PC, Mac, Linux)  
✅ **Bedrock Edition** (Mobile, Xbox, PlayStation, Switch, Windows 10/11)  
✅ **All GeyserMC Configurations** (Standalone, Plugin mode, Proxy mode)

---

## ⚡ Quick Setup Guide

### Step 1: Install GeyserMC

Choose your installation method:

=== "Spigot/Paper Server"
    1. Download [GeyserMC](https://geysermc.org/) for Spigot/Paper
    2. Place `Geyser-Spigot.jar` in your `plugins/` folder
    3. Restart your server
    4. Configure port forwarding for UDP port `19132`

=== "Proxy Server (BungeeCord/Velocity)"
    1. Download GeyserMC for your proxy platform
    2. Install on your proxy server
    3. Configure backend server connections
    4. Set up UDP port forwarding

=== "Standalone GeyserMC"
    1. Download standalone GeyserMC
    2. Configure server connections
    3. Run alongside your Java server
    4. Manage ports independently

### Step 2: Install BlockBall

1. Install BlockBall on your **Java server** (not the proxy)
2. Configure your games as normal
3. Ensure permissions are set for cross-platform players

### Step 3: Test Basic Functionality

1. Join with a Bedrock client: `/blockball join game1`
2. Verify game participation works
3. Test basic ball interactions

!!! warning "Initial Experience"
    Without additional configuration, Bedrock players will see:
    
    - ⚠️ Ball appears as a basic player head (no custom texture)
    - ⚠️ Ball cannot rotate properly
    - ✅ All gameplay mechanics work normally

---

## 🎨 Enhanced Visual Experience (Patreon)

### Premium Features

Thanks to **Patreon supporters** who funded this feature! ❤️ 

The enhanced Bedrock experience includes:

* Custom Ball Texture: Proper soccer ball appearance
* Smooth Rotation: Realistic ball movement animations  
* Optimized Performance: Better frame rates on mobile devices
* Easy Installation: Pre-configured resource packs

### Installation (Patreon Members)

!!! tip "Time Saver"
    Patreon members get pre-configured files that make setup effortless. [Support development](https://www.patreon.com/Shynixn) to save hours of manual configuration!

=== "Spigot/Paper Installation"

    1. **Download Resources**
       - Get `BlockBall-GeyserMC.zip` from [Patreon](https://www.patreon.com/Shynixn)
    
    2. **Extract Files**
       ```
       plugins/Geyser-Spigot/
       ├── custom-skulls_blockball.yml
       └── packs/
           └── BlockBallPack.mcpack
       ```
    
    3. **Configure Custom Skulls**
       - Copy `player-profiles` values from `custom-skulls_blockball.yml`
       - Paste into `plugins/Geyser-Spigot/custom-skulls.yml`
    
    4. **Restart Server**
       - Restart to apply resource pack changes

=== "Proxy Installation (BungeeCord/Velocity)"

    1. **Download Resources**  
       - Get `BlockBall-GeyserMC.zip` from [Patreon](https://www.patreon.com/Shynixn)
    
    2. **Extract to Proxy**
       ```
       plugins/Geyser-<Proxy>/
       ├── custom-skulls_blockball.yml  
       └── packs/
           └── BlockBallPack.mcpack
       ```
    
    3. **Update Configuration**
       - Merge `player-profiles` from `custom-skulls_blockball.yml` 
       - Into `plugins/Geyser-<Proxy>/custom-skulls.yml`
    
    4. **Apply Changes**
       - Restart proxy server


