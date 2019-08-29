# BlockBall  [![Build Status](https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat-square)](https://raw.githubusercontent.com/Shynixn/BlockBall/master/LICENSE)


| branch        | status        | coverage | version | download |
| ------------- | ------------- | -------- | --------| ---------| 
| master        | [![Build Status](https://img.shields.io/travis/Shynixn/BlockBall/master.svg?style=flat-square)](https://travis-ci.org/Shynixn/BlockBall) | [![Coverage](https://img.shields.io/codecov/c/github/shynixn/blockball/master.svg?style=flat-square)](https://codecov.io/gh/Shynixn/BlockBall/branch/master)|![GitHub license](https://img.shields.io/nexus/r/https/oss.sonatype.org/com.github.shynixn.blockball/blockball-bukkit-plugin.svg?style=flat-square)  |[Download latest release](https://github.com/Shynixn/BlockBall/releases)|
| development   | [![Build Status](https://img.shields.io/travis/Shynixn/BlockBall/development.svg?style=flat-square)](https://travis-ci.org/Shynixn/BlockBall)|[![Coverage](https://img.shields.io/codecov/c/github/shynixn/blockball/development.svg?style=flat-square)](https://codecov.io/gh/Shynixn/BlockBall/branch/development) |![GitHub license](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.github.shynixn.blockball/blockball-bukkit-plugin.svg?style=flat-square) |  [Download snapshots](https://oss.sonatype.org/content/repositories/snapshots/com/github/shynixn/blockball/blockball-bukkit-plugin/) |
## Description

BlockBall is a spigot and also a sponge plugin to play soccer games in Minecraft.

## Features

* Uses blocks as balls in minecraft
* Games are completely customizable
* Version support 1.8.R1 - 1.14.R1
* Check out the [BlockBall-Spigot-Page](https://www.spigotmc.org/resources/15320/) to get more information. 

## Installation

* Please check out the [BlockBall Documentation](https://shynixn.github.io/BlockBall/) for further information.

## Screenshots

![alt tag](http://www.mediafire.com/convkey/3383/6zhpiiijhk022s5zg.jpg)
![alt tag](http://www.mediafire.com/convkey/a253/ur76bhb6doccomvzg.jpg)

## Contributing

* Clone the repository to your local environment
* Execute gradle sync for dependencies
* Install the additional spigot dependencies by executing the following gradle task

```xml
[./gradlew|gradlew.bat] downloadDependencies
```

* Install the ForgeGradle development workspace for sponge

```xml
[./gradlew|gradlew.bat] setupDecompWorkspace
```

* Build the plugin by executing

```xml
[./gradlew|gradlew.bat] shadowJar
```

* The BlockBall-Bukkit.jar file gets generated at ``blockball-bukkit-plugin/build/libs/blockball-bukkit-plugin.jar``
* The BlockBall-Sponge-1.12.2.jar file gets generated at ``blockball-sponge-plugin/blockball-sponge-nms-112R1/build/libs/blockball-sponge-nms.jar``

## Licence

Copyright 2015-2019 Shynixn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
