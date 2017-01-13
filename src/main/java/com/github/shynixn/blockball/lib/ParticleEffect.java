package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.lib.ReflectionUtils.PackageType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

//Darkblade12
public enum ParticleEffect {

    EXPLOSION_NORMAL("explode", 0, -1, ParticleProperty.DIRECTIONAL),

    EXPLOSION_LARGE("largeexplode", 1, -1),

    EXPLOSION_HUGE("hugeexplosion", 2, -1),

    FIREWORKS_SPARK("fireworksSpark", 3, -1, ParticleProperty.DIRECTIONAL),

    WATER_BUBBLE("bubble", 4, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER),

    WATER_SPLASH("splash", 5, -1, ParticleProperty.DIRECTIONAL),

    WATER_WAKE("wake", 6, 7, ParticleProperty.DIRECTIONAL),

    SUSPENDED("suspended", 7, -1, ParticleProperty.REQUIRES_WATER),

    SUSPENDED_DEPTH("depthSuspend", 8, -1, ParticleProperty.DIRECTIONAL),

    CRIT("crit", 9, -1, ParticleProperty.DIRECTIONAL),

    CRIT_MAGIC("magicCrit", 10, -1, ParticleProperty.DIRECTIONAL),

    SMOKE_NORMAL("smoke", 11, -1, ParticleProperty.DIRECTIONAL),

    SMOKE_LARGE("largesmoke", 12, -1, ParticleProperty.DIRECTIONAL),

    SPELL("spell", 13, -1),

    SPELL_INSTANT("instantSpell", 14, -1),

    SPELL_MOB("mobSpell", 15, -1, ParticleProperty.COLORABLE),

    SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1, ParticleProperty.COLORABLE),

    SPELL_WITCH("witchMagic", 17, -1),

    DRIP_WATER("dripWater", 18, -1),

    DRIP_LAVA("dripLava", 19, -1),

    VILLAGER_ANGRY("angryVillager", 20, -1),

    VILLAGER_HAPPY("happyVillager", 21, -1, ParticleProperty.DIRECTIONAL),

    TOWN_AURA("townaura", 22, -1, ParticleProperty.DIRECTIONAL),

    NOTE("note", 23, -1, ParticleProperty.COLORABLE),

    PORTAL("portal", 24, -1, ParticleProperty.DIRECTIONAL),

    ENCHANTMENT_TABLE("enchantmenttable", 25, -1, ParticleProperty.DIRECTIONAL),

    FLAME("flame", 26, -1, ParticleProperty.DIRECTIONAL),

    LAVA("lava", 27, -1),

    FOOTSTEP("footstep", 28, -1),

    CLOUD("cloud", 29, -1, ParticleProperty.DIRECTIONAL),

    REDSTONE("reddust", 30, -1, ParticleProperty.COLORABLE),

    SNOWBALL("snowballpoof", 31, -1),

    SNOW_SHOVEL("snowshovel", 32, -1, ParticleProperty.DIRECTIONAL),

    SLIME("slime", 33, -1),

    HEART("heart", 34, -1),

    BARRIER("barrier", 35, 8),

    ITEM_CRACK("iconcrack", 36, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),

    BLOCK_CRACK("blockcrack", 37, -1, ParticleProperty.REQUIRES_DATA),

    BLOCK_DUST("blockdust", 38, 7, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),

    WATER_DROP("droplet", 39, 8),

    ITEM_TAKE("take", 40, 8),

    MOB_APPEARANCE("mobappearance", 41, 8);

    private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<>();
    private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<>();
    private final String name;
    private final int id;
    private final int requiredVersion;
    private final List<ParticleProperty> properties;

    // Initialize map for quick name and id lookup
    static {
        for (ParticleEffect effect : values()) {
            NAME_MAP.put(effect.name, effect);
            ID_MAP.put(effect.id, effect);
        }
    }


    ParticleEffect(String name, int id, int requiredVersion, ParticleProperty... properties) {
        this.name = name;
        this.id = id;
        this.requiredVersion = requiredVersion;
        this.properties = Arrays.asList(properties);
    }


    public String getName() {
        return this.name;
    }


    public int getId() {
        return this.id;
    }


    public int getRequiredVersion() {
        return this.requiredVersion;
    }


    public boolean hasProperty(ParticleProperty property) {
        return this.properties.contains(property);
    }


    public boolean isSupported() {
        return this.requiredVersion == -1 || ParticlePacket.getVersion() >= this.requiredVersion;
    }


    public static ParticleEffect fromName(String name) {
        for (Entry<String, ParticleEffect> entry : NAME_MAP.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(name)) {
                continue;
            }
            return entry.getValue();
        }
        return null;
    }


    public static ParticleEffect fromId(int id) {
        for (Entry<Integer, ParticleEffect> entry : ID_MAP.entrySet()) {
            if (entry.getKey() != id) {
                continue;
            }
            return entry.getValue();
        }
        return null;
    }


    private static boolean isWater(Location location) {
        Material material = location.getBlock().getType();
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }


    private static boolean isLongDistance(Location location, List<Player> players) {
        String world = location.getWorld().getName();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            if (!world.equals(playerLocation.getWorld().getName()) || playerLocation.distanceSquared(location) < 65536) {
                continue;
            }
            return true;
        }
        return false;
    }


    private static boolean isDataCorrect(ParticleEffect effect, ParticleData data) {
        return ((effect == BLOCK_CRACK || effect == BLOCK_DUST) && data instanceof BlockData) || (effect == ITEM_CRACK && data instanceof ItemData);
    }


    private static boolean isColorCorrect(ParticleEffect effect, ParticleColor color) {
        return ((effect == SPELL_MOB || effect == SPELL_MOB_AMBIENT || effect == REDSTONE) && color instanceof OrdinaryColor) || (effect == NOTE && color instanceof NoteColor);
    }


    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, null).sendTo(center, range);
    }


    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), null).sendTo(center, players);
    }


    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        this.display(offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
    }


    public void display(Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (!this.hasProperty(ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, range > 256, null).sendTo(center, range);
    }


    public void display(Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (!this.hasProperty(ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        }
        if (this.hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, isLongDistance(center, players), null).sendTo(center, players);
    }


    public void display(Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        this.display(direction, speed, center, Arrays.asList(players));
    }


    public void display(ParticleColor color, Location center, double range) throws ParticleVersionException, ParticleColorException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.COLORABLE)) {
            throw new ParticleColorException("This particle effect is not colorable");
        }
        if (!isColorCorrect(this, color)) {
            throw new ParticleColorException("The particle color type is incorrect");
        }
        new ParticlePacket(this, color, range > 256).sendTo(center, range);
    }


    public void display(ParticleColor color, Location center, List<Player> players) throws ParticleVersionException, ParticleColorException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.COLORABLE)) {
            throw new ParticleColorException("This particle effect is not colorable");
        }
        if (!isColorCorrect(this, color)) {
            throw new ParticleColorException("The particle color type is incorrect");
        }
        new ParticlePacket(this, color, isLongDistance(center, players)).sendTo(center, players);
    }


    public void display(ParticleColor color, Location center, Player... players) throws ParticleVersionException, ParticleColorException {
        this.display(color, center, Arrays.asList(players));
    }


    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, data).sendTo(center, range);
    }


    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), data).sendTo(center, players);
    }


    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
        this.display(data, offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
    }


    public void display(ParticleData data, Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, direction, speed, range > 256, data).sendTo(center, range);
    }


    public void display(ParticleData data, Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, direction, speed, isLongDistance(center, players), data).sendTo(center, players);
    }


    public void display(ParticleData data, Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
        this.display(data, direction, speed, center, Arrays.asList(players));
    }


    public enum ParticleProperty {

        REQUIRES_WATER,

        REQUIRES_DATA,

        DIRECTIONAL,

        COLORABLE
    }


    public static abstract class ParticleData {
        private final Material material;
        private final byte data;
        private final int[] packetData;


        @SuppressWarnings("deprecation")
        public ParticleData(Material material, byte data) {
            this.material = material;
            this.data = data;
            this.packetData = new int[]{material.getId(), data};
        }


        public Material getMaterial() {
            return this.material;
        }


        public byte getData() {
            return this.data;
        }


        public int[] getPacketData() {
            return this.packetData;
        }


        public String getPacketDataString() {
            return "_" + this.packetData[0] + "_" + this.packetData[1];
        }
    }


    public static final class ItemData extends ParticleData {

        public ItemData(Material material, byte data) {
            super(material, data);
        }
    }


    public static final class BlockData extends ParticleData {

        public BlockData(Material material, byte data) throws IllegalArgumentException {
            super(material, data);
            if (!material.isBlock()) {
                throw new IllegalArgumentException("The material is not a block");
            }
        }
    }


    public static abstract class ParticleColor {

        public abstract float getValueX();


        public abstract float getValueY();


        public abstract float getValueZ();
    }


    public static final class OrdinaryColor extends ParticleColor {
        private final int red;
        private final int green;
        private final int blue;


        public OrdinaryColor(int red, int green, int blue) throws IllegalArgumentException {
            if (red < 0) {
                throw new IllegalArgumentException("The red value is lower than 0");
            }
            if (red > 255) {
                throw new IllegalArgumentException("The red value is higher than 255");
            }
            this.red = red;
            if (green < 0) {
                throw new IllegalArgumentException("The green value is lower than 0");
            }
            if (green > 255) {
                throw new IllegalArgumentException("The green value is higher than 255");
            }
            this.green = green;
            if (blue < 0) {
                throw new IllegalArgumentException("The blue value is lower than 0");
            }
            if (blue > 255) {
                throw new IllegalArgumentException("The blue value is higher than 255");
            }
            this.blue = blue;
        }


        public OrdinaryColor(Color color) {
            this(color.getRed(), color.getGreen(), color.getBlue());
        }


        public int getRed() {
            return this.red;
        }


        public int getGreen() {
            return this.green;
        }


        public int getBlue() {
            return this.blue;
        }


        @Override
        public float getValueX() {
            return (float) this.red / 255F;
        }


        @Override
        public float getValueY() {
            return (float) this.green / 255F;
        }


        @Override
        public float getValueZ() {
            return (float) this.blue / 255F;
        }
    }


    public static final class NoteColor extends ParticleColor {
        private final int note;


        public NoteColor(int note) throws IllegalArgumentException {
            if (note < 0) {
                throw new IllegalArgumentException("The note value is lower than 0");
            }
            if (note > 24) {
                throw new IllegalArgumentException("The note value is higher than 24");
            }
            this.note = note;
        }


        @Override
        public float getValueX() {
            return (float) this.note / 24F;
        }


        @Override
        public float getValueY() {
            return 0;
        }


        @Override
        public float getValueZ() {
            return 0;
        }

    }


    private static final class ParticleDataException extends RuntimeException {
        private static final long serialVersionUID = 3203085387160737484L;


        public ParticleDataException(String message) {
            super(message);
        }
    }


    private static final class ParticleColorException extends RuntimeException {
        private static final long serialVersionUID = 3203085387160737484L;


        public ParticleColorException(String message) {
            super(message);
        }
    }


    private static final class ParticleVersionException extends RuntimeException {
        private static final long serialVersionUID = 3203085387160737484L;


        public ParticleVersionException(String message) {
            super(message);
        }
    }


    public static final class ParticlePacket {
        private static int version;
        private static Class<?> enumParticle;
        private static Constructor<?> packetConstructor;
        private static Method getHandle;
        private static Field playerConnection;
        private static Method sendPacket;
        private static boolean initialized;
        private final ParticleEffect effect;
        private float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final float speed;
        private final int amount;
        private final boolean longDistance;
        private final ParticleData data;
        private Object packet;


        public ParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleData data) throws IllegalArgumentException {
            initialize();
            if (speed < 0) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            if (amount < 0) {
                throw new IllegalArgumentException("The amount is lower than 0");
            }
            this.effect = effect;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.amount = amount;
            this.longDistance = longDistance;
            this.data = data;
        }


        public ParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleData data) throws IllegalArgumentException {
            this(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
        }


        public ParticlePacket(ParticleEffect effect, ParticleColor color, boolean longDistance) {
            this(effect, color.getValueX(), color.getValueY(), color.getValueZ(), 1, 0, longDistance, null);
            if (effect == ParticleEffect.REDSTONE && color instanceof OrdinaryColor && ((OrdinaryColor) color).getRed() == 0) {
                this.offsetX = Float.MIN_NORMAL;
            }
        }

        public static void initialize() throws VersionIncompatibleException {
            if (initialized) {
                return;
            }
            try {
                version = Integer.parseInt(PackageType.getServerVersion().split(Pattern.quote("_"))[1]);
                if (version > 7) {
                    enumParticle = PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
                }
                Class<?> packetClass = PackageType.MINECRAFT_SERVER.getClass(version < 7 ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
                packetConstructor = ReflectionUtils.getConstructor(packetClass);
                getHandle = ReflectionUtils.getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
                playerConnection = ReflectionUtils.getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection");
                sendPacket = ReflectionUtils.getMethod(playerConnection.getType(), "sendPacket", PackageType.MINECRAFT_SERVER.getClass("Packet"));
            } catch (Exception exception) {
                throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
            }
            initialized = true;
        }


        public static int getVersion() {
            if (!initialized) {
                initialize();
            }
            return version;
        }


        public static boolean isInitialized() {
            return initialized;
        }


        private void initializePacket(Location center) throws PacketInstantiationException {
            if (this.packet != null) {
                return;
            }
            try {
                this.packet = packetConstructor.newInstance();
                if (version < 8) {
                    String name = this.effect.getName();
                    if (this.data != null) {
                        name += this.data.getPacketDataString();
                    }
                    ReflectionUtils.setValue(this.packet, true, "a", name);
                } else {
                    ReflectionUtils.setValue(this.packet, true, "a", enumParticle.getEnumConstants()[this.effect.getId()]);
                    ReflectionUtils.setValue(this.packet, true, "j", this.longDistance);
                    if (this.data != null) {
                        int[] packetData = this.data.getPacketData();
                        ReflectionUtils.setValue(this.packet, true, "k", this.effect == ParticleEffect.ITEM_CRACK ? packetData : new int[]{packetData[0] | (packetData[1] << 12)});
                    }
                }
                ReflectionUtils.setValue(this.packet, true, "b", (float) center.getX());
                ReflectionUtils.setValue(this.packet, true, "c", (float) center.getY());
                ReflectionUtils.setValue(this.packet, true, "d", (float) center.getZ());
                ReflectionUtils.setValue(this.packet, true, "e", this.offsetX);
                ReflectionUtils.setValue(this.packet, true, "f", this.offsetY);
                ReflectionUtils.setValue(this.packet, true, "g", this.offsetZ);
                ReflectionUtils.setValue(this.packet, true, "h", this.speed);
                ReflectionUtils.setValue(this.packet, true, "i", this.amount);
            } catch (Exception exception) {
                throw new PacketInstantiationException("Packet instantiation failed", exception);
            }
        }


        public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException {
            this.initializePacket(center);
            try {
                sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), this.packet);
            } catch (Exception exception) {
                throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
            }
        }


        public void sendTo(Location center, List<Player> players) throws IllegalArgumentException {
            if (players.isEmpty()) {
                return;
            }
            for (Player player : players) {
                this.sendTo(center, player);
            }
        }


        public void sendTo(Location center, double range) throws IllegalArgumentException {
            if (range < 1) {
                throw new IllegalArgumentException("The range is lower than 1");
            }
            String worldName = center.getWorld().getName();
            double squared = range * range;
            for (Player player : SFileUtils.getOnlinePlayers()) {
                if (!player.getWorld().getName().equals(worldName) || player.getLocation().distanceSquared(center) > squared) {
                    continue;
                }
                this.sendTo(center, player);
            }
        }


        private static final class VersionIncompatibleException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;


            public VersionIncompatibleException(String message, Throwable cause) {
                super(message, cause);
            }
        }


        private static final class PacketInstantiationException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;


            public PacketInstantiationException(String message, Throwable cause) {
                super(message, cause);
            }
        }


        private static final class PacketSendingException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;


            public PacketSendingException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }
}
