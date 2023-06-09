# BlockBall  [![Build Status](https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat-square)](https://raw.githubusercontent.com/Shynixn/BlockBall/master/LICENSE)


| branch        | status        |  version | download |
| ------------- | ------------- |  --------| ---------| 
| master        | [![Build Status](https://github.com/Shynixn/BlockBall/workflows/CI/badge.svg?branch=master)](https://github.com/Shynixn/BlockBall/actions) | ![GitHub license](https://img.shields.io/nexus/r/https/oss.sonatype.org/com.github.shynixn.blockball/blockball-bukkit-plugin.svg?style=flat-square)  |[Download latest release](https://github.com/Shynixn/BlockBall/releases)|
| development   | [![Build Status](https://github.com/Shynixn/BlockBall/workflows/CI/badge.svg?branch=development)](https://github.com/Shynixn/BlockBall/actions)|![GitHub license](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.github.shynixn.blockball/blockball-bukkit-plugin.svg?style=flat-square) |  [Download snapshots](https://oss.sonatype.org/content/repositories/snapshots/com/github/shynixn/blockball/blockball-bukkit-plugin/) |
## Description

BlockBall is a spigot plugin to play soccer games in Minecraft.

## Features

* Uses blocks as balls in minecraft
* Games are completely customizable
* Version support 1.8.R3 - 1.20.R1
* Check out the [BlockBall-Spigot-Page](https://www.spigotmc.org/resources/15320/) to get more information.

## Installation

* Please check out the [BlockBall Documentation](https://shynixn.github.io/BlockBall/) for further information.

## Screenshots

![alt tag](http://www.mediafire.com/convkey/3383/6zhpiiijhk022s5zg.jpg)
![alt tag](http://www.mediafire.com/convkey/a253/ur76bhb6doccomvzg.jpg)

## Contributing

### Setting up development environment

* Install Java 17 or higher
* Fork the BlockBall project on github and clone it to your local environment.
* BlockBall requires spigot server implementations from 1.16 to 1.20 to be correctly installed in your local Maven cache.
  As this requires multiple java version to build different versions, a Dockerfile is provided to build these dependencies in a docker container
  and then copy it to your local Maven cache.

Note: If using Windows, execute the commands using Git Bash.
````sh
mkdir -p ~/.m2/repository/org/spigotmc/
docker build --target dependencies-jdk8 -t blockball-dependencies-jdk8 .
docker create --name blockball-dependencies-jdk8 blockball-dependencies-jdk8 bash
docker cp blockball-dependencies-jdk8:/root/.m2/repository/org/spigotmc ~/.m2/repository/org/
docker rm -f blockball-dependencies-jdk8
docker build --target dependencies-jdk17 -t blockball-dependencies-jdk17 .
docker create --name blockball-dependencies-jdk17 blockball-dependencies-jdk17 bash
docker cp blockball-dependencies-jdk17:/root/.m2/repository/org/spigotmc ~/.m2/repository/org/
docker rm -f blockball-dependencies-jdk17
````

* Open the project with an IDE, gradle sync for dependencies.

### Testing

#### Option 1

* Setup your own minecraft server
* Change ``// val destinationDir = File("C:/temp/plugins")`` to your plugins folder in the ``structureblocklib-bukkit-sample/build.gradle.kts`` file.
* Run the ``pluginJar`` task to generate a plugin.jar file.
* Run your minecraft server

#### Option 2 :whale:

* Run the provided docker file.
* The source code is copied to a new docker container and built to a plugin.
* This plugin is installed on a new minecraft server which is accessible on the host machine on the default port on ``localhost``.

````sh
docker build -t blockball .
docker run --name=blockball -p 25565:25565 -p 5005:5005 blockball
````

## Licence

The source code is licensed under the Apache 2.0 license.
