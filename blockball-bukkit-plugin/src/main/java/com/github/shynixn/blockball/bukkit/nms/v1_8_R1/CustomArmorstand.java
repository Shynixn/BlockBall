package com.github.shynixn.blockball.bukkit.nms.v1_8_R1;

import com.github.shynixn.blockball.api.bukkit.event.ball.BallDeathEvent;
import com.github.shynixn.blockball.api.bukkit.event.ball.BallInteractWithEntityEvent;
import com.github.shynixn.blockball.api.bukkit.event.ball.BallMoveEvent;
import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Level;

public final class CustomArmorstand extends EntityArmorStand implements Ball {
    private boolean isSpecial;
    private CustomRabbit slime;
    private BallMeta ballMeta;

    private int counter = 20;
    private int rvalue = 5;
    private Vector startVector;
    private int jumps;

    private final boolean isRotating = true;

    public CustomArmorstand(World world) {
        super(world);
    }

    public CustomArmorstand(org.bukkit.World world, BallMeta meta) {
        super(((CraftWorld) world).getHandle());
        this.isSpecial = true;
        this.ballMeta = meta;
    }

    public CustomArmorstand(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    protected void doTick() {
        if (this.isSpecial && this.slime != null) {
            try {
                if (this.passenger == null && this.slime != null && this.getBukkitEntity().getVehicle() == null) {
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
                            if (Config.getInstance().isEngineV2Enabled()) {
                                if (this.slime.getSpigotEntity().getVelocity().getY() < 0.2
                                        && this.slime.getSpigotEntity().getVelocity().getX() < 0.03
                                        && this.slime.getSpigotEntity().getVelocity().getZ() < 0.03) {
                                    final int rollingDistance = 3;
                                    for (int i = 0; i < rollingDistance; i++) {
                                        this.slime.getSpigotEntity()
                                                .setVelocity(this.slime.getSpigotEntity().getVelocity()
                                                        .add(this.slime.getSpigotEntity().getVelocity().normalize().multiply(0.05))); //New
                                    }
                                }
                            }
                        }
                    }
                    if (Config.getInstance().isEngineV2Enabled()) {
                        this.manageEngineV2Dribbling();
                    } else {
                        this.manageEngineV1Dribbling();
                    }
                }
                this.getBukkitEntity().setFireTicks(0);
                this.slime.getSpigotEntity().setFireTicks(0);
                Bukkit.getPluginManager().callEvent(new BallMoveEvent(this));
            } catch (final IllegalArgumentException e) {

            } catch (final Exception e) {
                Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Critical EntityHackError happened! Shynixn catcher algorithm prevented server crash!");
                Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Report this bug to the author Shynixn!");
                Bukkit.getLogger().log(Level.WARNING, "Critical error.", e);
            }
        }
        super.doTick();
    }

    private void manageEngineV1Dribbling() {
        if (this.counter <= 0) {
            for (final Player player : this.getSpigotEntity().getWorld().getPlayers()) {
                if (player.getLocation().distance(this.slime.getSpigotEntity().getLocation()) < 2) {
                    final BallInteractWithEntityEvent event = new BallInteractWithEntityEvent(this, player);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.startVector = this.slime.getSpigotEntity().getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(this.getMeta().getHorizontalStrength());
                        this.startVector.setY(this.getMeta().getVerticalStrength());
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

    private void manageEngineV2Dribbling() {
        if (this.counter <= 0) {
            for (final Player player : this.getSpigotEntity().getWorld().getPlayers()) {
                if (player.getLocation().distance(this.slime.getSpigotEntity().getLocation()) < 2) {
                    final BallInteractWithEntityEvent event = new BallInteractWithEntityEvent(this, player);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.startVector = this.slime.getSpigotEntity()
                                .getLocation()
                                .toVector()
                                .subtract(player.getLocation().toVector())
                                .normalize()
                                .multiply(this.getMeta().getHorizontalStrength());
                        this.rvalue = this.random.nextInt(5) + 8;
                        this.jumps = this.random.nextInt(5) + 3;
                        this.startVector.setY(0.1 * this.jumps);
                        try {
                            this.slime.getSpigotEntity().setVelocity(this.startVector.multiply(0.1 * this.jumps));
                        } catch (final IllegalArgumentException ex) {

                        }
                        if (this.isRotating) {
                            this.getSpigotEntity().setHeadPose(new EulerAngle(1, this.getSpigotEntity().getHeadPose().getY(), this.getSpigotEntity().getHeadPose().getZ()));

                        }
                    }
                }
            }
            this.counter = 2;
        } else {
            this.counter--;
        }
    }

    /**
     * Returns the entity who is responsible for the hitbox.
     *
     * @return entity
     */
    @Override
    public Rabbit getHitboxEntity() {
        return this.slime.getSpigotEntity();
    }

    /**
     * Returns the metaData of the entity.
     *
     * @return meta
     */
    @Override
    public BallMeta getMeta() {
        return ballMeta;
    }

    public ArmorStand getSpigotEntity() {
        return (ArmorStand) this.getDesignEntity();
    }

    /**
     * Comparison.
     * @param object object
     * @return isSame
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof CustomArmorstand) {
            final CustomArmorstand stander = (CustomArmorstand) object;
            if (stander.getSpigotEntity().getEntityId() == this.getSpigotEntity().getEntityId())
                return true;
        }
        return false;
    }

    /**
     * Kicks the ball with the given strength parameters.
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    @Override
    public void kick(Object entity, double horizontalStrength, double verticalStrength) {
        BallInteractWithEntityEvent event = null;
        if (entity instanceof Player) {
            event = new BallInteractWithEntityEvent(this, (Entity) entity);
            Bukkit.getPluginManager().callEvent(event);
        }
        if (event == null || !event.isCancelled()) {
            this.startVector = this.slime.getSpigotEntity()
                    .getLocation()
                    .toVector()
                    .subtract(((Entity)entity).getLocation().toVector())
                    .normalize()
                    .multiply(horizontalStrength);
            this.startVector.setY(verticalStrength);
            try {
                this.slime.getSpigotEntity().setVelocity(this.startVector.clone());
            } catch (final IllegalArgumentException ex) {

            }
            if (this.isRotating)
                this.getSpigotEntity().setHeadPose(new EulerAngle(1, this.getSpigotEntity().getHeadPose().getY(), this.getSpigotEntity().getHeadPose().getZ()));
            this.rvalue = this.random.nextInt(5) + 9;
            this.jumps = this.random.nextInt(5) + 5;
        }
    }

    /**
     * Kicks the ball with the default strength values.
     *
     * @param entity entity
     */
    @Override
    public void kick(Object entity) {
        this.kick(entity, this.getMeta().getHorizontalStrength(), this.getMeta().getVerticalStrength());
    }

    /**
     * Passes the ball with the given strength parameters.
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    @Override
    public void pass(Object entity, double horizontalStrength, double verticalStrength) {
        BallInteractWithEntityEvent event = null;
        if (entity instanceof Player) {
            event = new BallInteractWithEntityEvent(this, (Entity) entity);
            Bukkit.getPluginManager().callEvent(event);
        }
        if (event == null || !event.isCancelled()) {
            this.startVector = this.slime.getSpigotEntity().getLocation().toVector().subtract(((Entity)entity).getLocation().toVector()).normalize().multiply(horizontalStrength * 0.8);
            this.startVector.setY(verticalStrength * 0.5);
            try {
                this.slime.getSpigotEntity().setVelocity(this.startVector.clone());
            } catch (final IllegalArgumentException ex) {

            }
            if (this.isRotating)
                this.getSpigotEntity().setHeadPose(new EulerAngle(1, this.getSpigotEntity().getHeadPose().getY(), this.getSpigotEntity().getHeadPose().getZ()));
            this.rvalue = this.random.nextInt(5) + 9;
            this.jumps = this.random.nextInt(5) + 5;
        }
    }

    /**
     * Passes the ball with the default strength values.
     *
     * @param entity entity
     */
    @Override
    public void pass(Object entity) {
        this.pass(entity, this.getMeta().getHorizontalStrength(), this.getMeta().getVerticalStrength());
    }

    /**
     * Bounces the ball back to it's previous location.
     */
    @Override
    public void bounceBack() {
        private void bumpBallBack() {
            if (this.lastBallLocation != null) {
                final Vector knockback = this.lastBallLocation.toVector().subtract(this.ball.getLocation().toVector());
                this.ball.getLocation().setDirection(knockback);
                this.ball.setVelocity(knockback);
                final Vector direction = this.arena.getBallSpawnLocation().toVector().subtract(this.ball.getLocation().toVector());
                this.ball.setVelocity(direction.multiply(0.1));
                this.bumper = 40;
                this.bumperCounter++;
                if (this.bumperCounter == 5) {
                    this.ball.teleport(this.arena.getBallSpawnLocation());
                }
            }
        }
    }

    /**
     * Shoots the ball in the during vector direction with the default strength values.
     *
     * @param vector vector
     */
    @Override
    public void setVelocity(Object vector) {
        this.slime.getSpigotEntity().setVelocity((Vector) vector);
    }

    /**
     * Returns the velocity the ball is flying after a kick/pass or velocity manipulation.
     *
     * @return velocity
     */
    @Override
    public Object getVelocity() {
        return this.slime.getSpigotEntity().getVelocity();
    }

    /**
     * Respawns the entity at the given location and applies everything from the ballMeta.
     *
     * @param mLocation location
     */
    @Override
    public void spawn(Object mLocation) {
        final Location location = (Location) mLocation;
        if (!this.isDead()) {
            this.remove();
        }
        NMSRegistry.accessWorldGuardSpawn(location);
        final net.minecraft.server.v1_8_R1.World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.addEntity(this, SpawnReason.CUSTOM);
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("invulnerable", true);
        compound.setBoolean("Invisible", true);
        compound.setBoolean("PersistenceRequired", true);
        compound.setBoolean("NoBasePlate", true);
        this.a(compound);
        this.slime = new CustomRabbit(location.getWorld(),this);
        this.slime.spawn(location);
        this.getSpigotEntity().setHeadPose(new EulerAngle(0, 0, 0));
        this.getSpigotEntity().setBodyPose(new EulerAngle(0, 0, 2778));
        this.getSpigotEntity().setRightArmPose(new EulerAngle(2778, 0, 0));
        NMSRegistry.rollbackWorldGuardSpawn(location);
    }

    /**
     * Damages the ball entity with the given amount.
     *
     * @param amount amount
     */
    @Override
    public void damage(double amount) {
        if (!this.getSpigotEntity().isDead()) {
            final PacketPlayOutAnimation animation = new PacketPlayOutAnimation(this, 1);
            for (final Player player : this.getSpigotEntity().getWorld().getPlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(animation);
            }
        }
    }

    /**
     * Removes the entity.
     */
    @Override
    public void remove() {
        if (!this.getSpigotEntity().isDead()) {
            Bukkit.getPluginManager().callEvent(new BallDeathEvent(this));
            this.getSpigotEntity().remove();
        }
    }

    /**
     * Teleports the ball to the given location.
     *
     * @param location location
     */
    @Override
    public void teleport(Object location) {
        this.slime.getSpigotEntity().teleport((Location) location);
        this.getSpigotEntity().teleport((Location) location);
    }

    /**
     * Returns the location of the ball.
     *
     * @return location
     */
    @Override
    public Object getLocation() {
        if (this.slime != null)
            this.slime.getSpigotEntity().getLocation();
        return this.getSpigotEntity().getLocation();
    }

    /**
     * Returns if the ball entity is Dead.
     *
     * @return isDead
     */
    @Override
    public boolean isDead() {
        return this.getSpigotEntity().isDead();
    }

    /**
     * Returns the entity who is responsible for the ball design and skin.
     *
     * @return entity
     */
    @Override
    public Object getDesignEntity() {
        return super.getBukkitEntity();
    }
}
