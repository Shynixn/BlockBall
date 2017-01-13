# BlockBall
[![GitHub license](http://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/Shynixn/BlockBall/master/LICENSE)

![alt tag](http://www.mediafire.com/convkey/8782/9kjiwk2sisw5hk6zg.jpg)

## Description

Spigot plugin to simulate a football/soccer game in minecraft.

## Features

* Simulates a working football/soccer game in minecraft with a real looking ball
* 4 different gamemodes
* Version support 1.8.R1 - 1.11.R1
* Check out the [BlockBall-Spigot-Page](https://www.spigotmc.org/resources/blockball-minigame-bungeecord-soccer-football-1-8-1-9-1-10-1-11.15320/) to get more information. 

## Installation

* Download the plugin BlockBall
* Put the plugin into your plugin folder
* Install the plugin Worldedit
* Start the server (1.8.0 - 1.11.2, Java 7/Java 8)
* Join and play :)

## API

* Reference the BlockBall.jar in your own projects.
* If you are using maven you can add it from the central maven repository

### Maven

```xml
<dependency>
     <groupId>com.github.shynixn</groupId>
     <artifactId></artifactId>
     <version></version>
</dependency>
```

## How to use the it

#### Spawn a custom ball

```java
   Location location = new Location(Bukkit.getWorld("world"), 100, 0, 100);
   Ball ball = BlockBallApi.createNewBall(location.getWorld());
   ball.spawn(location);
   ball.setVelocity(new Vector(0.2, 0.2,0.2));
```

* Check out the [BlockBall-Spigot-Page](https://www.spigotmc.org/resources/blockball-minigame-bungeecord-soccer-football-1-8-1-9-1-10-1-11.15320/) to get more information. 

## Licence

Copyright 2017 Shynixn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
