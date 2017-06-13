package com.github.shynixn.blockball.business.bukkit.nms.v1_11_R1;

import com.github.shynixn.blockball.api.events.BallDeathEvent;
import com.github.shynixn.blockball.api.events.BallKickEvent;
import com.github.shynixn.blockball.api.events.BallMoveEvent;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.lib.SConsoleUtils;
import com.github.shynixn.blockball.lib.SEntityCompareable;
import com.github.shynixn.blockball.lib.SSKulls;
import com.github.shynixn.blockball.api.entities.Ball;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Level;

public final class CustomArmorstand extends EntityArmorStand implements Ball, SEntityCompareable {
    private boolean isSpecial;
    private CustomRabbit slime;

    private int counter = 20;
    private int rvalue = 5;
    private Vector startVector;
    private int jumps;

    private double hstrength = 2.8; //1.8
    private double vstrength = 0.8; //0.8

    private boolean isRotating = true;

    public CustomArmorstand(World world) {
        super(world);
    }

    public CustomArmorstand(org.bukkit.World world, boolean special) {
        super(((CraftWorld) world).getHandle());
        this.isSpecial = true;
    }

    public CustomArmorstand(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    private boolean isPassengerNull() {
        return this.passengers == null || this.passengers.isEmpty();
    }

    @Override
    protected void doTick() {
        if (this.isSpecial && this.slime != null) {
            try {
                if (this.isPassengerNull() && this.slime != null && this.getBukkitEntity().getVehicle() == null) {
                    if (!this.isSmall())
                        this.setPositionRotation(this.slime.getSpigotEntity().getLocation().getX(), this.slime.getSpigotEntity().getLocation().getY() - 1.0, this.slime.getSpigotEntity().getLocation().getZ(), this.slime.getSpigotEntity().getLocation().getYaw(), this.slime.getSpigotEntity().getLocation().getPitch());
                    else
                        this.setPositionRotation(this.slime.getSpigotEntity().getLocation().getX(), this.slime.getSpigotEntity().getLocation().getY() - 0.7, this.slime.getSpigotEntity().getLocation().getZ(), this.slime.getSpigotEntity().getLocation().getYaw(), this.slime.getSpigotEntity().getLocation().getPitch());
                    if (this.isRotating) {
                        final EulerAngle a = this.getSpigotEntity().getHeadPose();
                        final int value = (int) a.getX();
                        if (value % this.rvalue != 0) {
                            if (this.rvalue - a.getX() > this.rvalue * 0.5) {
                                this.getSpigotEntity().setHeadPose(new EulerAngle(a.getX() + 0.1, a.getY() + 0.2, a.getZ() + 0.3));
                            } else if (this.rvalue - a.getX() > this.rvalue * 0.3) {
                                this.getSpigotEntity().setHeadPose(new EulerAngle(a.getX() + 0.05, a.getY() + 0.1, a.getZ() + 0.15));
                            } else if (this.rvalue - a.getX() > this.rvalue * 0.2) {
                                this.getSpigotEntity().setHeadPose(new EulerAngle(a.getX() + 0.025, a.getY() + 0.05, a.getZ() + 0.075));
                            } else if (this.rvalue - a.getX() > this.rvalue * 0.1) {
                                this.getSpigotEntity().setHeadPose(new EulerAngle(a.getX() + 0.012, a.getY() + 0.025, a.getZ() + 0.035));
                            }
                        }
                    }
                    if (this.counter <= 0) {
                        for (final Player player : this.getSpigotEntity().getWorld().getPlayers()) {
                            if (player.getLocation().distance(this.slime.getSpigotEntity().getLocation()) < 2) {
                                final BallKickEvent event = new BallKickEvent(player, this);
                                Bukkit.getPluginManager().callEvent(new BallKickEvent(player, this));
                                if (!event.isCancelled()) {
                                    this.startVector = this.slime.getSpigotEntity().getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(this.hstrength);
                                    this.startVector.setY(this.vstrength);
                                    this.slime.getSpigotEntity().setVelocity(this.startVector.clone());
                                    if (this.isRotating)
                                        this.getSpigotEntity().setHeadPose(new EulerAngle(1, this.getSpigotEntity().getHeadPose().getY(), this.getSpigotEntity().getHeadPose().getZ()));
                                    final Random random = new Random();
                                    this.rvalue = random.nextInt(5) + 5;
                                    this.jumps = random.nextInt(5) + 2;
                                    break;
                                }

                            }
                        }
                        this.counter = 2;
                    } else {
                        this.counter--;
                    }
                    if (this.slime.getSpigotEntity().isOnGround() && this.jumps > 0 && this.startVector != null) {
                        this.slime.getSpigotEntity().setVelocity(this.startVector.multiply(0.1 * this.jumps));
                        this.startVector.setY(0.1 * this.jumps);
                        this.jumps--;
                    }
                }
                this.getBukkitEntity().setFireTicks(0);
                this.slime.getSpigotEntity().setFireTicks(0);
                Bukkit.getPluginManager().callEvent(new BallMoveEvent(this));
            } catch (final Exception e) {
                SConsoleUtils.sendColoredMessage(ChatColor.RED + "Critical EntityHackError happend! Shynixn catcher algorithm prevented server crash!");
                SConsoleUtils.sendColoredMessage(ChatColor.RED + "Report this bug to the author Shynixn!");
                Bukkit.getLogger().log(Level.WARNING, "Critical error.", e);
            }
        }
        super.doTick();
    }

    @Override
    public ArmorStand getDesignEntity() {
        return this.getSpigotEntity();
    }

    @Override
    public Rabbit getMovementEntity() {
        return this.slime.getSpigotEntity();
    }

    @Override
    public void spawn(Location location) {
        NMSRegistry.accessWorldGuardSpawn(location);
        final World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.addEntity(this, SpawnReason.CUSTOM);
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("invulnerable", true);
        compound.setBoolean("Invisible", true);
        compound.setBoolean("PersistenceRequired", true);
        compound.setBoolean("NoBasePlate", true);
        this.a(compound);
        this.slime = new CustomRabbit(location.getWorld(), true, this);
        this.slime.spawn(location);
        this.getSpigotEntity().setHeadPose(new EulerAngle(0, 0, 0));
        this.getSpigotEntity().setBodyPose(new EulerAngle(0, 0, 2778));
        this.getSpigotEntity().setRightArmPose(new EulerAngle(2778, 0, 0));
        NMSRegistry.rollbackWorldGuardSpawn(location);
    }

    public ArmorStand getSpigotEntity() {
        return (ArmorStand) this.getBukkitEntity();
    }

    @Override
    public void setSkin(String skin) {
        if (!this.getSpigotEntity().isDead())
            this.getSpigotEntity().setHelmet(SSKulls.getSkull(skin));
    }

    @Override
    public void setSmall(boolean flag) {
        if (!this.getSpigotEntity().isDead())
            super.setSmall(flag);
    }

    @Override
    public int getEntityId() {
        if (!this.getSpigotEntity().isDead())
            return this.getSpigotEntity().getEntityId();
        return -1;
    }

    @Override
    public String getSkin() {
        if (!this.getSpigotEntity().isDead()) {
            if (SSKulls.getNameFromItemStack(this.getSpigotEntity().getHelmet()) == null)
                return SSKulls.getURLFromItemStack(this.getSpigotEntity().getHelmet());
            return SSKulls.getNameFromItemStack(this.getSpigotEntity().getHelmet());
        }
        return null;
    }

    @Override
    public Vector getVelocity() {
        return this.slime.getSpigotEntity().getVelocity();
    }

    @Override
    public void setKickStrengthHorizontal(double strength) {
        if (strength > 0 && strength < 10)
            this.hstrength = strength;
    }

    @Override
    public void setKickStrengthVertical(double strength) {
        if (strength > 0 && strength < 10)
            this.vstrength = strength;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CustomArmorstand) {
            final CustomArmorstand stander = (CustomArmorstand) object;
            if (stander.getEntityId() == this.getEntityId())
                return true;
        }
        return false;
    }

    @Override
    public boolean isSameEntity(Entity entity) {
        if (!this.getSpigotEntity().isDead()) {
            if (this.getBukkitEntity().getEntityId() == entity.getEntityId() || this.slime.getSpigotEntity().getEntityId() == entity.getEntityId())
                return true;
        }
        return false;
    }

    @Override
    public void damage() {
        if (!this.getSpigotEntity().isDead()) {
            final PacketPlayOutAnimation animation = new PacketPlayOutAnimation(this, 1);
            for (final Player player : this.getSpigotEntity().getWorld().getPlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(animation);
            }
        }
    }

    @Override
    public void despawn() {
        if (!this.getSpigotEntity().isDead()) {
            Bukkit.getPluginManager().callEvent(new BallDeathEvent(this));
            this.getSpigotEntity().remove();
        }
    }

    @Override
    public Location getLocation() {
        if (this.slime != null)
            this.slime.getSpigotEntity().getLocation();
        return this.getSpigotEntity().getLocation();
    }

    @Override
    public void teleport(Location location) {
        this.slime.getSpigotEntity().teleport(location);
        this.getSpigotEntity().teleport(location);
    }

    @Override
    public void setVelocity(Vector vector) {
        if (this.slime != null) {
            this.slime.getSpigotEntity().setVelocity(vector);
        }
    }

    @Override
    public boolean isDead() {
        return this.getSpigotEntity().isDead();
    }

    @Override
    public void resetSkin() {
        this.setSkin("http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d");
    }

    @Override
    public boolean isRotating() {
        return this.isRotating;
    }

    @Override
    public void setRotating(boolean isRotating) {
        this.isRotating = isRotating;
    }
}
