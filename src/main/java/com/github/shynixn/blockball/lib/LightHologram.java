package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.api.entities.IPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Shynixn
 */
public interface LightHologram {
    void show(Player... players);

    void remove(Player... players);

    void teleport(Location location);

    void setText(String text);

    String getText();

    Location getLocation();

    class Builder implements LightHologram {
        private final LightHologram holder;

        public Builder(Location location) {
            this.holder = invokeCustomNMS(location);
        }

        @Override
        public void show(Player... players) {
            this.holder.show(players);
        }

        @Override
        public void remove(Player... players) {
            this.holder.remove(players);
        }

        @Override
        public void teleport(Location location) {
            this.holder.teleport(location);
        }

        @Override
        public void setText(String text) {
            this.holder.setText(text);
        }

        @Override
        public String getText() {
            return this.holder.getText();
        }

        @Override
        public Location getLocation() {
            return this.holder.getLocation();
        }

        private static LightHologram invokeCustomNMS(Location location) {
            try {
                Class<?> clazz = Class.forName(LightHologram.class.getName() + "$Container_VERSION".replace("VERSION", getServerVersion()));
                Constructor constructor = clazz.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                return (LightHologram) constructor.newInstance(location);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot start hologram.");
            }
        }

        private static String getServerVersion() {
            try {
                return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            } catch (Exception ex) {
                throw new RuntimeException("Version not found!");
            }
        }
    }

    class Container_v1_8_R1 implements LightHologram {
        private final net.minecraft.server.v1_8_R1.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_8_R1(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_8_R1.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_8_R1.NBTTagCompound compound = new net.minecraft.server.v1_8_R1.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setGravity(false);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_8_R1.Packet packet = new net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_8_R1.Packet packet = new net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_8_R1.Packet packet = new net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_8_R1.Packet packet) {
            ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_8_R1.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_8_R1.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_8_R2 implements LightHologram {
        private final net.minecraft.server.v1_8_R2.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_8_R2(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_8_R2.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_8_R2.NBTTagCompound compound = new net.minecraft.server.v1_8_R2.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setGravity(false);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_8_R2.Packet packet = new net.minecraft.server.v1_8_R2.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_8_R2.Packet packet = new net.minecraft.server.v1_8_R2.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_8_R2.Packet packet = new net.minecraft.server.v1_8_R2.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_8_R2.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_8_R2.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_8_R2.Packet packet) {
            ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_8_R2.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_8_R2.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_8_R3 implements LightHologram {
        private final net.minecraft.server.v1_8_R3.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_8_R3(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_8_R3.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_8_R3.NBTTagCompound compound = new net.minecraft.server.v1_8_R3.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setGravity(false);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_8_R3.Packet packet = new net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_8_R3.Packet packet = new net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_8_R3.Packet packet = new net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_8_R3.Packet packet) {
            ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_8_R3.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_9_R1 implements LightHologram {
        private final net.minecraft.server.v1_9_R1.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_9_R1(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_9_R1.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_9_R1.NBTTagCompound compound = new net.minecraft.server.v1_9_R1.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setGravity(false);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_9_R1.Packet packet = new net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_9_R1.Packet packet = new net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_9_R1.Packet packet = new net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_9_R1.Packet packet) {
            ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_9_R1.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_9_R2 implements LightHologram {
        private final net.minecraft.server.v1_9_R2.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_9_R2(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_9_R2.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_9_R2.NBTTagCompound compound = new net.minecraft.server.v1_9_R2.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setGravity(false);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_9_R2.Packet packet = new net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_9_R2.Packet packet = new net.minecraft.server.v1_9_R2.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_9_R2.Packet packet = new net.minecraft.server.v1_9_R2.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_9_R2.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_9_R2.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_9_R2.Packet packet) {
            ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_9_R2.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_9_R2.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_10_R1 implements LightHologram {
        private final net.minecraft.server.v1_10_R1.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_10_R1(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_10_R1.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_10_R1.NBTTagCompound compound = new net.minecraft.server.v1_10_R1.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setNoGravity(true);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_10_R1.Packet packet = new net.minecraft.server.v1_10_R1.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_10_R1.Packet packet = new net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_10_R1.Packet packet = new net.minecraft.server.v1_10_R1.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_10_R1.Packet packet) {
            ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_10_R1.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_10_R1.CraftWorld) (world)).getHandle();
        }
    }

    class Container_v1_11_R1 implements LightHologram {
        private final net.minecraft.server.v1_11_R1.EntityArmorStand entityArmorStand;
        private IPosition position;

        private Container_v1_11_R1(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand = new net.minecraft.server.v1_11_R1.EntityArmorStand(this.toWorld(location.getWorld()));
            this.entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            net.minecraft.server.v1_11_R1.NBTTagCompound compound = new net.minecraft.server.v1_11_R1.NBTTagCompound();
            compound.setBoolean("Marker", true);
            compound.setBoolean("NoBasePlate", true);

            this.entityArmorStand.a(compound);
            this.entityArmorStand.setNoGravity(true);
            this.entityArmorStand.setCustomNameVisible(true);
            this.entityArmorStand.setInvisible(true);
        }

        @Override
        public void show(Player... players) {
            net.minecraft.server.v1_11_R1.Packet packet = new net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void remove(Player... players) {
            net.minecraft.server.v1_11_R1.Packet packet = new net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy(this.entityArmorStand.getId());
            for (Player player : players) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void teleport(Location location) {
            this.position = new SLocation(location);
            this.entityArmorStand.locX = this.position.getX();
            this.entityArmorStand.locY = this.position.getY();
            this.entityArmorStand.locZ = this.position.getZ();
            this.entityArmorStand.yaw = (float) this.position.getYaw();
            this.entityArmorStand.pitch = (float) this.position.getPitch();
            net.minecraft.server.v1_11_R1.Packet packet = new net.minecraft.server.v1_11_R1.PacketPlayOutEntityTeleport(this.entityArmorStand);
            for (Player player : location.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public void setText(String text) {
            this.entityArmorStand.setCustomName(text);
            net.minecraft.server.v1_11_R1.PacketPlayOutEntityMetadata packet = new net.minecraft.server.v1_11_R1.PacketPlayOutEntityMetadata(this.entityArmorStand.getId(), this.entityArmorStand.getDataWatcher(), true);
            for (Player player : this.position.getWorld().getPlayers()) {
                this.sendPacket(player, packet);
            }
        }

        @Override
        public String getText() {
            return this.entityArmorStand.getCustomName();
        }

        @Override
        public Location getLocation() {
            return this.position.toLocation();
        }

        private void sendPacket(Player player, net.minecraft.server.v1_11_R1.Packet packet) {
            ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        private net.minecraft.server.v1_11_R1.World toWorld(World world) {
            return ((org.bukkit.craftbukkit.v1_11_R1.CraftWorld) (world)).getHandle();
        }
    }
}
