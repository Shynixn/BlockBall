Developer API
=============

JavaDocs
~~~~~~~~

https://shynixn.github.io/BlockBall/apidocs/

Including the BlockBall API
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. image:: https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api/badge.svg?style=flat-square
  :target: https://maven-badges.herokuapp.com/maven-central/com.github.shynixn.blockball/blockball-api

BlockBall is using gradle as build system and is available in the central repository.

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

.. note::  **Jar Files** - Bukkit


* `Download BlockBall-Api <http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.github.shynixn.blockball&a=blockball-api&v=LATEST>`__
* `Download BlockBall-Bukkit-Api <http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.github.shynixn.blockball&a=blockball-bukkit-api&v=LATEST>`__


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