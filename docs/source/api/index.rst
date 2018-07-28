Developer API
=============

JavaDocs
~~~~~~~~

https://shynixn.github.io/BlockBall/apidocs/

Including the BlockBall Bukkit-Api
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BlockBall is using maven as build system but you can include the api via different ways:

**Maven**:
::
   <dependency>
        <groupId>com.github.shynixn.ball</groupId>
        <artifactId>ball-api</artifactId>
        <version>1.4</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.github.shynixn.blockball</groupId>
        <artifactId>blockball-api</artifactId>
        <version>5.4.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.github.shynixn.blockball</groupId>
        <artifactId>blockball-bukkit-api</artifactId>
        <version>5.4.0</version>
        <scope>provided</scope>
    </dependency>

**Gradle**:
::
    dependencies {
        compileOnly 'com.github.shynixn.ball:ball-api:1.4'
        compileOnly 'com.github.shynixn.blockball:blockball-api:5.4.0'
        compileOnly 'com.github.shynixn.blockball:blockball-bukkit-api:5.4.0'
    }

**Reference the jar file**:

If you are not capable of using one of these above you can also manually download the
api from the `repository <https://oss.sonatype.org/content/repositories/releases/com/github/shynixn/blockball/blockball-bukkit-api/>`__  and reference it in your project.

Updating your plugin.yml
~~~~~~~~~~~~~~~~~~~~~~~~

Your plugin optionally uses BlockBall.
::
    softdepend: [BlockBall]

Your plugin requires BlockBall to work.
::
    depend: [BlockBall]

Modifying BlockBall games
~~~~~~~~~~~~~~~~~~~~~~~~~


**Teleport the ball of a game to a target location**
::
        String arenaId = "1";
        Location location; // Target location

        BukkitGameController gameController = BlockBallApi.INSTANCE.getDefaultGameController();
        BukkitGame game = gameController.getGameFromArenaName(arenaId);

        if (game == null) { // Check if the gme is not null!
            return;
        }

        BukkitBall ball = game.getBall();

        if (ball == null) { // Check if the ball is not null!
            return;
        }

        ball.teleport(location);

Listen to Events
~~~~~~~~~~~~~~~~

There are many BlockBall events in order to listen to actions. Please take a look into the `JavaDocs <https://shynixn.github.io/BlockBall/apidocs/>`__  for all events:
::
    @EventHandler
    public void onGameJoinEvent(GameJoinEvent event){
        Player player = event.getPlayer();
        Game game = event.getGame();

        //Do something
    }

::


Setup your personal BlockBall Workspace
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Important!** BlockBall is written in `Kotlin <https://kotlinlang.org/>`__ instead of pure Java. If you are not familiar
with Kotlin, BlockBall might be a difficult task.

It is sometimes necessary to customize BlockBall itself instead of using the Developer API. The following steps
help you to get started with developing for BlockBall.

Before you continue you should be familiar with **git**, **github**, **maven** and any preferred **Java IDE**.

1. Open `BlockBall on github <https://github.com/Shynixn/BlockBall>`__
2. Log in or create a github account and press the **Fork** button in the top right corner.
3. Github will create a new repository with BlockBall on your account
4. Click on the green **Clone or download** button and copy the text inside of the textbox
5. Open a terminal on your pc, go into a target folder and enter the command

**CLI**
::
   git clone <your copied text>
::

6. After BlockBall folder is created you can open the Project with any Java IDE supporting **Maven**
7. Add the required spigot libraries from 1.8.0 until the latest version via BuildTools and the the following commands.

**CLI**
::
    - java -jar BuildTools.jar --rev 1.8
    - java -jar BuildTools.jar --rev 1.8.3
    - java -jar BuildTools.jar --rev 1.8.8
    - java -jar BuildTools.jar --rev 1.9
    - java -jar BuildTools.jar --rev 1.9.4
    - java -jar BuildTools.jar --rev 1.10
    - java -jar BuildTools.jar --rev 1.11
    - java -jar BuildTools.jar --rev 1.12
    - java -jar BuildTools.jar --rev 1.13
    - mvn install:install-file -Dfile=spigot-1.8.jar -DgroupId=org.spigotmc -DartifactId=spigot18R1 -Dversion=1.8.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.8.3.jar -DgroupId=org.spigotmc -DartifactId=spigot18R2 -Dversion=1.8.3-R2.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.8.8.jar -DgroupId=org.spigotmc -DartifactId=spigot18R3 -Dversion=1.8.8-R3.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.9.jar -DgroupId=org.spigotmc -DartifactId=spigot19R1 -Dversion=1.9.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.9.4.jar -DgroupId=org.spigotmc -DartifactId=spigot19R2 -Dversion=1.9.4-R2.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.10.2.jar -DgroupId=org.spigotmc -DartifactId=spigot110R1 -Dversion=1.10.2-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.11.jar -DgroupId=org.spigotmc -DartifactId=spigot111R1 -Dversion=1.11.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.12.jar -DgroupId=org.spigotmc -DartifactId=spigot112R1 -Dversion=1.12.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.13.jar -DgroupId=org.spigotmc -DartifactId=spigot113R1 -Dversion=1.13.0-R1.0 -Dpackaging=jar


8. Refresh the maven dependencies.
9. Try to compile the root project with **mvn compile**
10. If successful you can start editing the source code and create jar files via **mvn package**

**Optional**

11. To share your changes with the world push your committed changes into your github repository.
12. Click on the **New pull request** button and start a pull request against BlockBall

(base:fork Shynixn/BlockBall, base: development <- head fork: <your repository> ...)