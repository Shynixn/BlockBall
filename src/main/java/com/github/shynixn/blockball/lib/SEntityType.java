package com.github.shynixn.blockball.lib;


@Deprecated
public enum SEntityType {
    //Drops
    ITEM("ITEM", 1, "Item"),
    XP_ORB("XP_ORB", 2, "XPOrb", "ExperienceOrb"),
    //Immobile
    LEAD_KNOT("LEAD_KNOT", 8, "LeashKnot", "Leash"),
    PAINTING("PAINTING", 9, "Painting"),
    ITEM_FRAME("ITEM_FRAME", 18, "ItemFrame"),
    ARMOR_STAND("ARMOR_STAND", 30, "ArmorStand"),
    ENDER_CRYSTAL("ENDER_CRYSTAL", 200, "EnderCrystal"),
    //Projectiles
    ARROW("ARROW", 10, "Arrow"),
    SNOWBALL("SNOWBALL", 11, "Snowball"),
    GHAST_FIREBALL("GHAST_FIREBALL", 12, "Fireball"),
    BLAZE_FIREBALL("BLAZE_FIREBALL", 13, "SmallFireball"),
    THROWN_ENDER_PEARL("THROWN_ENDER_PEARL", 14, "ThrownEnderpearl", "EnderPearl"),
    THROWN_EYE_OF_ENDER("THROWN_EYE_OF_ENDER", 15, "EyeOfEnderSignal", "EnderSignal"),
    THROWN_SPLASH_POTION("THROWN_SPLASH_POTION", 16, "ThrownPotion", "Potion"),
    THROWN_EXP_BOTTLE("THROWN_EXP_BOTTLE", 17, "ThrownExpBottle"),
    WITHER_SKULL("WITHER_SKULL", 19, "WitherSkull"),
    FIREWORKS_ROCKET("FIREWORKS_ROCKET", 22, "FireworksRocketEntity", "Fireworks"),
    //Blocks
    PRIMED_TNT("PRIMED_TNT", 20, "PrimedTnt", "TNTPrimed"),
    FALLING_SAND("FALLING_SAND", 21, "FallingSand", "FallingBlock"),
    //Vehicles
    MINECART_COMMAND_BLOCK("MINECART_COMMAND_BLOCK", 40, "MinecartCommandBlock"),
    BOAT("BOAT", 41, "Boat"),
    MINECART("MINECART", 42, "MinecartRideable"),
    MINECART_CHEST("MINECART_CHEST", 43, "MinecartChest"),
    MINECART_FURNACE("MINECART_FURNACE", 44, "MinecartFurnace"),
    MINECART_TNT("MINECART_TNT", 45, "MinecartTNT"),
    MINECART_HOPPER("MINECART_HOPPER", 46, "MinecartHopper"),
    MINECART_SPAWNER("MINECART_SPAWNER", 47, "MinecartSpawner", "MinecartMobSpawner"),
    //Generic
    MOB("MOB", 48, "Mob", "Monster"),
    MONSTER("MONSTER", 49, "Monster"),
    //Hostile mobs
    CREEPER("CREEPER", 50, "Creeper"),
    SKELETON("SKELETON", 51, "Skeleton"),
    SPIDER("SPIDER", 52, "Spider"),
    GIANT("GIANT", 53, "Giant", "GiantZombie"),
    ZOMBIE("ZOMBIE", 54, "Zombie"),
    SLIME("SLIME", 55, "Slime"),
    GHAST("GHAST", 56, "Ghast"),
    ZOMBIE_PIGMAN("ZOMBIE_PIGMAN", 57, "PigZombie"),
    ENDERMAN("ENDERMAN", 58, "Enderman"),
    CAVESPIDER("CAVESPIDER", 59, "CaveSpider"),
    SILVERFISH("SILVERFISH", 60, "Silverfish"),
    BLAZE("BLAZE", 61, "Blaze"),
    MAGMA_CUBE("MAGMA_CUBE", 62, "LavaSlime", "MagmaCube"),
    ENDER_DRAGON("ENDER_DRAGON", 63, "EnderDragon"),
    WITHER("WITHER", 64, "WitherBoss", "Wither"),
    WITCH("WITCH", 66, "Witch"),
    ENDERMITE("ENDERMITE", 67, "Endermite"),
    GUARDIAN("GUARDIAN", 68, "Guardian"),
    //Passive mobs
    BAT("BAT", 65, "Bat"),
    PIG("PIG", 90, "Pig"),
    SHEEP("SHEEP", 91, "Sheep"),
    COW("COW", 92, "Cow"),
    CHICKEN("CHICKEN", 93, "Chicken"),
    SQUID("SQUID", 94, "Squid"),
    WOLF("WOLF", 95, "Wolf"),
    MOOSHROOM("MOOSHROOM", 96, "MushroomCow"),
    SNOW_GOLEM("SNOW_GOLEM", 97, "SnowMan", "Snowman"),
    OCELOT("OCELOT", 98, "Ozelot", "Ocelot"),
    IRON_GOLEM("IRON_GOLEM", 99, "VillagerGolem", "IronGolem"),
    HORSE("HORSE", 100, "EntityHorse", "Horse"),
    RABBIT("RABBIT", 101, "Rabbit"),
    //NPCs
    VILLAGER("VILLAGER", 120, "Villager"),
    //Player
    PLAYER("PLAYER", -7, "Player");

    private final String name;
    private final String savegameID;
    private final int id;
    private final String otherID;

    SEntityType(String name, int id, String savegameID, String otherId) {
        this.name = name;
        this.id = id;
        this.savegameID = savegameID;
        this.otherID = otherId;
    }

    SEntityType(String name, int id, String savegameID) {
        this(name, id, savegameID, savegameID);
    }

    public static SEntityType getSEntityTypeByOtherId(String otherId) {
        for (final SEntityType sEntityType : SEntityType.values()) {
            if (otherId.contains(sEntityType.otherID))
                return sEntityType;
        }
        return null;
    }

    public Class<?> getNMSEntityClass() {
        try {
            return ReflectionLib.getClassFromName("net.minecraft.server.VERSION.Entity" + this.otherID);
        } catch (final Exception e) {
        }
        throw new IllegalArgumentException("Class not found!");
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSavegameID() {
        return this.savegameID;
    }

    public boolean isCreature() {
        return this.id >= 48;
    }

    public boolean isItem() {
        return this.id == 1 || this.id == 2;
    }

    public boolean isVehicle() {
        return this.id >= 40 && this.id <= 47;
    }

    public boolean isProjectile() {
        return (this.id >= 10 && this.id <= 19) || this.id != 22;
    }

    public boolean isDynamic() {
        return this.id == 20 || this.id == 21;
    }
}