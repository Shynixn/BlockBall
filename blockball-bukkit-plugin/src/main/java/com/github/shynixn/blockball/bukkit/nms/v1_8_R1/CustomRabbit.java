package com.github.shynixn.blockball.bukkit.nms.v1_8_R1;

import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.events.BallHitWallEvent;
import com.github.shynixn.blockball.lib.ReflectionLib;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.util.UnsafeList;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public final class CustomRabbit extends EntityRabbit {
    private Ball ball;

    public CustomRabbit(World world) {
        super(world);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return entityAgeable;
    }

    public CustomRabbit(org.bukkit.World world, boolean isSpecial, Ball ball) {
        super(((CraftWorld) world).getHandle());
        this.b(true);
        try {
            final Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            final Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(this.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(this.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(this.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(this.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            this.goalSelector.a(0, new PathfinderGoalFloat(this));
            this.ball = ball;
        } catch (final Exception exc) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to register pathfinder.", exc);
        }
    }

    void spawn(Location location) {
        NMSRegistry.accessWorldGuardSpawn(location);
        final net.minecraft.server.v1_8_R1.World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.addEntity(this, SpawnReason.CUSTOM);
        this.getSpigotEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 1));
        this.getSpigotEntity().setCustomNameVisible(false);
        this.getSpigotEntity().setCustomName("MyBallsIdentifier");
        NMSRegistry.rollbackWorldGuardSpawn(location);
    }

    Rabbit getSpigotEntity() {
        return (Rabbit) this.getBukkitEntity();
    }

    private void recalcPosition() {
        this.locX = (this.getBoundingBox().a + this.getBoundingBox().d) / 2.0D;
        this.locY = this.getBoundingBox().b;
        this.locZ = (this.getBoundingBox().c + this.getBoundingBox().f) / 2.0D;
    }

    private void spigotTimings(boolean started) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_8_R1.SpigotTimings");
        } catch (final ClassNotFoundException e) {

        }
        if (clazz != null) {
            final Object moveTimer = ReflectionLib.getValueFromFieldByClazz("entityMoveTimer", clazz);
            if (started) {
                ReflectionLib.invokeMethodByObject(moveTimer, "startTiming");
            } else {
                ReflectionLib.invokeMethodByObject(moveTimer, "stopTiming");
            }
        }
    }

    @Override
    public void move(double d0, double d1, double d2) {
        this.spigotTimings(true);
        if (this.T) {
            this.a(this.getBoundingBox().c(d0, d1, d2));
            this.recalcPosition();
        } else {
            try {
                this.checkBlockCollisions();
            } catch (final Throwable var84) {
                final CrashReport crashreport = CrashReport.a(var84, "Checking entity block collision");
                final CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }
            if (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && this.vehicle == null && this.passenger == null) {
                return;
            }
            this.world.methodProfiler.a("move");
            final double d3 = this.locX;
            final double d4 = this.locY;
            final double d5 = this.locZ;
            if (this.H) {
                this.H = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d6 = d0;
            final double d7 = d1;
            double d8 = d2;
            final boolean flag = false;
            if (flag) {
                final double d9;
                for (d9 = 0.05D; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }
                }

                for (; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }

                    d6 = d0;
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }
            }

            final List list = this.world.getCubes(this, this.getBoundingBox().a(d0, d1, d2));
            final AxisAlignedBB axisalignedbb = this.getBoundingBox();

            AxisAlignedBB axisalignedbb1;
            for (final Iterator flag1 = list.iterator(); flag1.hasNext(); d1 = axisalignedbb1.b(this.getBoundingBox(), d1)) {
                axisalignedbb1 = (AxisAlignedBB) flag1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
            final boolean var85 = this.onGround || d7 != d1 && d7 < 0.0D;

            Iterator iterator1;
            AxisAlignedBB axisalignedbb2;
            for (iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(this.getBoundingBox(), d0)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));

            for (iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(this.getBoundingBox(), d2)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));
            if (this.S > 0.0F && var85 && (d6 != d0 || d8 != d2)) {
                final double d10 = d0;
                final double d11 = d1;
                final double d12 = d2;
                final AxisAlignedBB event = this.getBoundingBox();
                this.a(axisalignedbb);
                d1 = (double) this.S;
                final List event1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
                AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                final AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(d6, 0.0D, d8);
                double d13 = d1;

                AxisAlignedBB axisalignedbb6;
                for (final Iterator iterator2 = event1.iterator(); iterator2.hasNext(); d13 = axisalignedbb6.b(axisalignedbb5, d13)) {
                    axisalignedbb6 = (AxisAlignedBB) iterator2.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, d13, 0.0D);
                double d14 = d6;

                AxisAlignedBB axisalignedbb7;
                for (final Iterator iterator3 = event1.iterator(); iterator3.hasNext(); d14 = axisalignedbb7.a(axisalignedbb4, d14)) {
                    axisalignedbb7 = (AxisAlignedBB) iterator3.next();
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0D, 0.0D);
                double d15 = d8;

                AxisAlignedBB axisalignedbb8;
                for (final Iterator axisalignedbb9 = event1.iterator(); axisalignedbb9.hasNext(); d15 = axisalignedbb8.c(axisalignedbb4, d15)) {
                    axisalignedbb8 = (AxisAlignedBB) axisalignedbb9.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, 0.0D, d15);
                AxisAlignedBB var89 = this.getBoundingBox();
                double d16 = d1;

                AxisAlignedBB axisalignedbb10;
                for (final Iterator iterator5 = event1.iterator(); iterator5.hasNext(); d16 = axisalignedbb10.b(var89, d16)) {
                    axisalignedbb10 = (AxisAlignedBB) iterator5.next();
                }

                var89 = var89.c(0.0D, d16, 0.0D);
                double d17 = d6;

                AxisAlignedBB axisalignedbb11;
                for (final Iterator iterator6 = event1.iterator(); iterator6.hasNext(); d17 = axisalignedbb11.a(var89, d17)) {
                    axisalignedbb11 = (AxisAlignedBB) iterator6.next();
                }

                var89 = var89.c(d17, 0.0D, 0.0D);
                double d18 = d8;

                AxisAlignedBB axisalignedbb12;
                for (final Iterator iterator7 = event1.iterator(); iterator7.hasNext(); d18 = axisalignedbb12.c(var89, d18)) {
                    axisalignedbb12 = (AxisAlignedBB) iterator7.next();
                }

                var89 = var89.c(0.0D, 0.0D, d18);
                final double d19 = d14 * d14 + d15 * d15;
                final double d20 = d17 * d17 + d18 * d18;
                if (d19 > d20) {
                    d0 = d14;
                    d2 = d15;
                    this.a(axisalignedbb4);
                } else {
                    d0 = d17;
                    d2 = d18;
                    this.a(var89);
                }

                d1 = (double) (-this.S);

                AxisAlignedBB axisalignedbb13;
                for (final Iterator iterator8 = event1.iterator(); iterator8.hasNext(); d1 = axisalignedbb13.b(this.getBoundingBox(), d1)) {
                    axisalignedbb13 = (AxisAlignedBB) iterator8.next();
                }

                this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
                if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10;
                    d1 = d11;
                    d2 = d12;
                    this.a(event);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d6 != d0 || d8 != d2;
            this.E = d7 != d1;
            this.onGround = this.E && d7 < 0.0D;
            this.F = this.positionChanged || this.E;
            final int i = MathHelper.floor(this.locX);
            final int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            final int k = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            net.minecraft.server.v1_8_R1.Block block = this.world.getType(blockposition).getBlock();
            if (block.getMaterial() == Material.AIR) {
                final net.minecraft.server.v1_8_R1.Block flag2 = this.world.getType(blockposition.down()).getBlock();
                if (flag2 instanceof BlockFence || flag2 instanceof BlockCobbleWall || flag2 instanceof BlockFenceGate) {
                    block = flag2;
                    blockposition = blockposition.down();
                }
            }

            this.a(d1, this.onGround, block, blockposition);
            if (d6 != d0) {
                this.motX = 0.0D;
            }

            if (d8 != d2) {
                this.motZ = 0.0D;
            }

            if (d7 != d1) {
                block.a(this.world, this);
            }

            if (this.positionChanged) {
                Block var86 = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY - (double) this.getHeadHeight()), MathHelper.floor(this.locZ));
                if (d6 > d0) {
                    var86 = var86.getRelative(BlockFace.EAST);
                } else if (d6 < d0) {
                    var86 = var86.getRelative(BlockFace.WEST);
                } else if (d8 > d2) {
                    var86 = var86.getRelative(BlockFace.SOUTH);
                } else if (d8 < d2) {
                    var86 = var86.getRelative(BlockFace.NORTH);
                }
                Bukkit.getPluginManager().callEvent(new BallHitWallEvent(this.ball, var86));
            }
        }
        this.spigotTimings(false);
    }
}
