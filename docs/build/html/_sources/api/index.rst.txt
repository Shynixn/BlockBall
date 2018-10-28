Developer API
=============

JavaDocs
~~~~~~~~

https://shynixn.github.io/BlockBall/apidocs/

Including the BlockBall API
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. image:: https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api/badge.svg?style=flat-square
  :target: https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api

BlockBall is using maven as build system and is available in the central repository.

.. note::  **Maven** - Bukkit

.. parsed-literal::

    <dependency>
        <groupId>com.github.shynixn.blockball</groupId>
        <artifactId>blockball-api</artifactId>
        <version>\ |release|\ </version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.github.shynixn.blockball</groupId>
        <artifactId>blockball-bukkit-api</artifactId>
        <version>\ |release|\ </version>
        <scope>provided</scope>
    </dependency>

.. note::  **Gradle** - Bukkit

.. parsed-literal::

    dependencies {
        compileOnly 'com.github.shynixn.blockball:blockball-api:\ |release|\ '
        compileOnly 'com.github.shynixn.blockball:blockball-bukkit-api:\ |release|\ '
    }

Registering the dependency
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::  **Bukkit** - Add the following tag to your plugin.yml if you **optionally** want to use BlockBall.

.. code-block:: yaml

    softdepend: [BlockBall]

.. note::  **Bukkit** - Add the following tag to your plugin.yml if your plugin  **requires** BlockBall to work.

.. code-block:: yaml

    depend: [BlockBall]

Working with the BlockBall API
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::  There are 4 simple steps to access the **whole** business logic of BlockBall.

* Check out the package **com.github.shynixn.blockball.api.business.service** in the JavaDocs to find the part of the business logic you want to access.
* Get the instance by using the following line.

.. code-block:: java

    YourBusinessService service = BlockBallApi.INSTANCE.resolve(YourBusinessService.class);

* If the service methods require additional data entities, check out the JavaDocs to find other services which provide these data entities
  or create new entities by checking out the package **com.github.shynixn.blockball.api.persistence.entity**.

.. code-block:: java

    YourPersistenceEntity entity = BlockBallApi.INSTANCE.create(YourPersistenceEntity.class);


* There are some samples below to get your started.

.. note::  **Ball** - Bukkit - Spawning a ball.

.. code-block:: java

    Location location; // Any Location instance

    BallMeta ballMeta = BlockBallApi.INSTANCE.create(BallMeta.class);
    BallEntityService ballEntityService = BlockBallApi.INSTANCE.resolve(BallEntityService.class);

    BallProxy ballProxy = ballEntityService.spawnTemporaryBall(location, ballMeta);

.. note::  **Game** - Bukkit - Accessing games.

.. code-block:: java

    GameService gameService = BlockBallApi.INSTANCE.resolve(GameService.class);
    GameActionService<Game> gameActionService = BlockBallApi.INSTANCE.resolve(GameActionService.class);

    Player player; // Any player instance
    List<Game> games = gameService.getAllGames();
    Optional<Game> targetGame = gameService.getGameFromName("1");

    if (targetGame.isPresent()) {
        Game game = targetGame.get();
        gameActionService.joinGame(game, player)
    }

.. note::  **Arena** - Bukkit - Accessing arenas.

.. code-block:: java

    final PersistenceArenaService persistenceArenaService = BlockBallApi.INSTANCE.resolve(PersistenceArenaService.class);
    final CompletableFuture<Void> completableFuture = persistenceArenaService.refresh(); // Do you want to refresh the arenas from the files?

    completableFuture.thenAccept(aVoid -> {
        // Once the arenas are refreshed you can always access them directly.
        List<Arena> arenas = persistenceArenaService.getArenas();
    });

Listen to Events
~~~~~~~~~~~~~~~~

There are many BlockBall events in order to listen to actions. Please take a look into the `JavaDocs <https://shynixn.github.io/BlockBall/apidocs/>`__  for all events.

.. note::  **SpawnEvent** - Bukkit - Listening to the spawn event.

.. code-block:: java

    @EventHandler
    public void onBallSpawnEvent(BallSpawnEvent event) {
        BallProxy ball = event.getBall();

        // Do Something
    }


Contributing and setting up your workspace
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. warning:: BlockBall is written in `Kotlin <https://kotlinlang.org/>`__ instead of pure Java. If you are not familiar with Kotlin, modifying BlockBall might be a difficult task.

* Fork the BlockBall project on github and clone it to your local environment.

* Use BuildTools.jar from spigotmc.org to build the following dependencies.

.. code-block:: java

    - java -jar BuildTools.jar --rev 1.8
    - java -jar BuildTools.jar --rev 1.8.3
    - java -jar BuildTools.jar --rev 1.8.8
    - java -jar BuildTools.jar --rev 1.9
    - java -jar BuildTools.jar --rev 1.9.4
    - java -jar BuildTools.jar --rev 1.10
    - java -jar BuildTools.jar --rev 1.11
    - java -jar BuildTools.jar --rev 1.12
    - java -jar BuildTools.jar --rev 1.13
    - java -jar BuildTools.jar --rev 1.13.2

* Install the created libraries to your local maven repository.

.. code-block:: java

    - mvn install:install-file -Dfile=spigot-1.8.jar -DgroupId=org.spigotmc -DartifactId=spigot18R1 -Dversion=1.8.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.8.3.jar -DgroupId=org.spigotmc -DartifactId=spigot18R2 -Dversion=1.8.3-R2.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.8.8.jar -DgroupId=org.spigotmc -DartifactId=spigot18R3 -Dversion=1.8.8-R3.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.9.jar -DgroupId=org.spigotmc -DartifactId=spigot19R1 -Dversion=1.9.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.9.4.jar -DgroupId=org.spigotmc -DartifactId=spigot19R2 -Dversion=1.9.4-R2.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.10.2.jar -DgroupId=org.spigotmc -DartifactId=spigot110R1 -Dversion=1.10.2-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.11.jar -DgroupId=org.spigotmc -DartifactId=spigot111R1 -Dversion=1.11.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.12.jar -DgroupId=org.spigotmc -DartifactId=spigot112R1 -Dversion=1.12.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.13.jar -DgroupId=org.spigotmc -DartifactId=spigot113R1 -Dversion=1.13.0-R1.0 -Dpackaging=jar
    - mvn install:install-file -Dfile=spigot-1.13.2.jar -DgroupId=org.spigotmc -DartifactId=spigot113R2 -Dversion=1.13.2-R2.0 -Dpackaging=jar

* Reimport the BlockBall maven project and execute 'mvn package' afterwards.

* The generated blockball-bukkit-plugin/target/blockball-bukkit-plugin-###.jar can be used for testing on a server.