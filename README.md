# BlockBall


| branch        | status        |  download |
| ------------- | ------------- |   ---------| 
| master        | [![Build Status](https://github.com/Shynixn/BlockBall/workflows/CI/badge.svg?branch=master)](https://github.com/Shynixn/BlockBall/actions)   |[Download latest release](https://github.com/Shynixn/BlockBall/releases)|
| development        | [![Build Status](https://github.com/Shynixn/BlockBall/workflows/CI/badge.svg?branch=master)](https://github.com/Shynixn/BlockBall/actions)   ||

## Description

BlockBall is a spigot plugin to play soccer games in Minecraft.

## Features

* Uses blocks as balls in minecraft
* Games are completely customizable
* Version support 1.8.R3 - 1.20.R3
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
* BlockBall requires multiple spigot versions to be correctly installed in your local Maven cache.
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
