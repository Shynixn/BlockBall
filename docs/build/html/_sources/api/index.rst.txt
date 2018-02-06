Developer API
=============

.. toctree::


JavaDocs
~~~~~~~~

https://shynixn.github.io/PetBlocks/apidocs/

Including the PetBlocks Bukkit-Api
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

PetBlocks is using maven as build system but you can include the api via different ways:

**Maven**:
::
    <dependency>
        <groupId>com.github.shynixn.petblocks</groupId>
        <artifactId>petblocks-bukkit-api</artifactId>
        <version>6.4.3</version>
        <scope>provided</scope>
    </dependency>


**Gradle**:
::
    dependencies {
        compileOnly 'com.github.shynixn.petblocks:petblocks-bukkit-api:6.4.3'
    }

**Reference the jar file**:

If you are not capable of using one of these above you can also manually download the
api from the `repository <https://oss.sonatype.org/content/repositories/releases/com/github/shynixn/petblocks/petblocks-bukkit-api/>`__  and reference it in your project.

Updating your plugin.yml
~~~~~~~~~~~~~~~~~~~~~~~~

Your plugin optionally uses PetBlocks.
::
    softdepend: [PetBlocks]

Your plugin requires PetBlocks to work.
::
    depend: [PetBlocks]

Modifying PetMeta and PetBlock
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


**Creating a new PetMeta for a player:**
::
    Player player; //Any player instance
    Plugin plugin; //Any plugin instance

    PetMetaController metaController = PetBlocksApi.getDefaultPetMetaController();
    PetMeta petMeta = metaController.create(player);
    petMeta.setPetDisplayName(ChatColor.GREEN + "This is my new pet."); //Modify the petMeta

    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
        @Override
        public void run() {
            metaController.store(petMeta); //It is recommend to save the petMeta asynchronously into the database
        }
    });

**Obtaining an existing PetMeta for a player from the database:**

You can see that this gets easily very complicated if
you need to manage asynchronous and synchronous server tasks.
::
            final Player player; //Any player instance
            final Plugin plugin; //Any plugin instance
            PetMetaController metaController = PetBlocksApi.getDefaultPetMetaController();

            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    Optional<PetMeta> optPetMeta = metaController.getFromPlayer(player);   //Acquire the PetMeta async from the database.
                    if (optPetMeta.isPresent()) { //Check if the player has got a petMeta?
                        Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                PetMeta petMeta = optPetMeta.get();
                                petMeta.setSkin(5, 0, null, false); //Change skin to a wooden block

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        metaController.store(petMeta);
                                    }
                                });
                            }
                        });
                    }
                }
            });
::

Using lamda expressions can reduce the code above significantly.
::
            final Player player; //Any player instance
            final Plugin plugin; //Any plugin instance
            PetMetaController metaController = PetBlocksApi.getDefaultPetMetaController();

            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                Optional<PetMeta> optPetMeta = metaController.getFromPlayer(player);   //Acquire the PetMeta async from the database.
                if (optPetMeta.isPresent()) { //Check if the player has got a petMeta?
                    Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                        PetMeta petMeta = optPetMeta.get();
                        petMeta.setSkin(5, 0, null, false); //Change skin to a wooden block
                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> metaController.store(petMeta));
                    });
                }
            });

**Spawning a petblock for a player:**
::
    final Player player; //Any player instance
    final PetMeta petMeta; //Any PetMeta instance
    final Location location; //Any target location

    final PetBlockController petBlockController = PetBlocksApi.getDefaultPetBlockController();
    final PetBlock petBlock = petBlockController.create(player, petMeta); //Spawn PetBlock
    petBlockController.store(petBlock); //Set it managed by the PetBlocks plugin

    petBlock.teleport(location);    //Teleport the petblock to the target location

**Obtaining an existing petblock for a player:**
::
            final Player player; //Any player instance
            final Location location; //Any target location

            final PetBlockController petBlockController = PetBlocksApi.getDefaultPetBlockController();
            final Optional<PetBlock> optPetBlock = petBlockController.getFromPlayer(player); //PetBlock is already managed
            if (optPetBlock.isPresent()) {
                final PetBlock petBlock = optPetBlock.get();
                petBlock.teleport(location);    //Teleport the petblock to the target location
            }

**Applying changes to the PetBlock**

You can also directly change the meta data of the spawned PetBlock:
::
    final PetBlock petBlock; //Any PetBlock instance
    petBlock.getMeta().setPetDisplayName("New name");

However, for applying the changes you need to respawn the PetBlock:
::
    final PetBlock petBlock; //Any PetBlock instance
    petBlock.respawn();

Listen to Events
~~~~~~~~~~~~~~~~

There are many PetBlock events in order to listen to actions. Please take a look into the `JavaDocs <https://shynixn.github.io/PetBlocks/apidocs/>`__  for all events:
::
    @EventHandler
    public void onPetBlockSpawnEvent(PetBlockSpawnEvent event){
        Player owner = event.getPlayer();
        PetBlock petBlock = event.getPetBlock();

        //Do something
    }

::
