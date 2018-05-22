package com.github.shynixn.blockball.bukkit.nms.v1_8_R3;

import com.github.shynixn.blockball.api.business.entity.VirtualArmorstand;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

/**
 * Virtual lightning armorstand.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class DisplayArmorstand implements VirtualArmorstand {
    private Player player;
    private final EntityArmorStand armorStand;

    /**
     * Initializes the armorstand.
     *
     * @param player   player
     * @param location location
     * @param id       id
     * @param data     data
     */
    public DisplayArmorstand(Player player, Location location, int id, int data) {
        super();
        this.player = player;
        this.armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());

        final NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("invulnerable", true);
        compound.setBoolean("PersistenceRequired", true);
        compound.setBoolean("NoBasePlate", true);
        this.armorStand.a(compound);
        this.armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

        final org.bukkit.inventory.ItemStack stackBuilder = new org.bukkit.inventory.ItemStack(Material.getMaterial(id), 1, (short) data);
        this.getCraftEntity().setHelmet(stackBuilder);
        this.getCraftEntity().setBodyPose(new EulerAngle(3.15, 0, 0));
        this.getCraftEntity().setLeftLegPose(new EulerAngle(3.15, 0, 0));
        this.getCraftEntity().setRightLegPose(new EulerAngle(3.15, 0, 0));
    }

    /**
     * Spawns the armorstand.
     */
    @Override
    public void spawn() {
        final PacketPlayOutSpawnEntityLiving packetSpawn = new PacketPlayOutSpawnEntityLiving(this.armorStand);
        final PacketPlayOutEntityEquipment packetHead =
                new PacketPlayOutEntityEquipment(this.armorStand.getId(), 3, CraftItemStack.asNMSCopy(((ArmorStand) this.armorStand.getBukkitEntity()).getHelmet()));
        this.sendPacket(packetSpawn);
        this.sendPacket(packetHead);
    }

    /**
     * Teleports the armorstand to the given location.
     *
     * @param mlocation location
     */
    @Override
    public <Location> void teleport(Location mlocation) {
        final org.bukkit.Location location = (org.bukkit.Location) mlocation;
        this.armorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        final PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(this.armorStand);
        this.sendPacket(teleportPacket);
    }

    /**
     * Removes the armorstand.
     */
    @Override
    public void remove() {
        final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(this.armorStand.getId());
        this.sendPacket(destroyPacket);
    }

    /**
     * Returns the location of the armorstand.
     *
     * @return location
     */
    @Override
    public <Location> Location getLocation() {
        return (Location) this.armorStand.getBukkitEntity().getLocation();
    }

    /**
     * Sends the packet.
     *
     * @param packet packet
     */
    private void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, this.player);
    }

    /**
     * Sends the packet.
     *
     * @param player player
     * @param packet packet
     */
    private void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Returns the craftArmorstand.
     *
     * @return stand
     */
    private CraftArmorStand getCraftEntity() {
        return (CraftArmorStand) this.armorStand.getBukkitEntity();
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     */
    @Override
    public void close() {
        this.remove();
        this.player = null;
    }
}
