package com.github.shynixn.blockball.bukkit.nms.v1_10_R1;

import com.github.shynixn.blockball.api.bukkit.event.ball.BallHitWallEvent;
import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.lib.ReflectionUtils;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

/**
 * Rabbit hitbox implementation for minecraft 1.10.0-1.10.2.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public final class CustomRabbit extends EntityRabbit {
    private Ball ball;

    public CustomRabbit(World world) {
        super(world);
    }

    public CustomRabbit(org.bukkit.World world, Ball ball) {
        super(((CraftWorld) world).getHandle());
        this.setSilent(true);
        try {
            final Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            final Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            this.ignoreFinalField(bField);
            this.ignoreFinalField(cField);
            cField.setAccessible(true);
            bField.set(this.goalSelector, Sets.newLinkedHashSet());
            bField.set(this.targetSelector, Sets.newLinkedHashSet());
            cField.set(this.goalSelector, Sets.newLinkedHashSet());
            cField.set(this.targetSelector, Sets.newLinkedHashSet());
            this.goalSelector.a(0, new PathfinderGoalFloat(this));
            this.ball = ball;
        } catch (final Exception exc) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to register pathfinder.", exc);
        }
    }

    private void ignoreFinalField(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    void spawn(Location location) {
        NMSRegistry.accessWorldGuardSpawn(location);
        final World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
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

    private void spigotTimings(boolean started) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_10_R1.SpigotTimings");
        } catch (final ClassNotFoundException ignored) {

        }
        if (clazz != null) {
            final Object moveTimer;
            try {
                moveTimer = ReflectionUtils.invokeFieldByClass(clazz, "entityMoveTimer");
                if (started) {
                    ReflectionUtils.invokeMethodByObject(moveTimer, "startTiming", new Class[]{}, new Object[]{});
                } else {
                    ReflectionUtils.invokeMethodByObject(moveTimer, "stopTiming", new Class[]{}, new Object[]{});
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to invoke entityMoveTimer.");
            }
        }
    }

    @Override
    public void move(double d0, double d1, double d2) {
        this.spigotTimings(true);
        if (this.noclip) {
            this.a(this.getBoundingBox().c(d0, d1, d2));
            this.recalcPosition();
        } else {
            try {
                this.checkBlockCollisions();
            } catch (final Throwable var77) {
                final CrashReport crashreport = CrashReport.a(var77, "Checking entity block collision");
                final CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && this.isVehicle() && this.isPassenger()) {
                return;
            }

            this.world.methodProfiler.a("move");
            final double d3 = this.locX;
            final double d4 = this.locY;
            final double d5 = this.locZ;
            if (this.E) {
                this.E = false;
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
                for (; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }
                }

                for (; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }

                    d6 = d0;
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }
            }

            final List list = this.world.getCubes(this, this.getBoundingBox().a(d0, d1, d2));
            final AxisAlignedBB axisalignedbb = this.getBoundingBox();
            int i = 0;

            int j;
            for (j = list.size(); i < j; ++i) {
                d1 = ((AxisAlignedBB) list.get(i)).b(this.getBoundingBox(), d1);
            }

            this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
            final boolean flag1 = this.onGround || d7 != d1 && d7 < 0.0D;
            j = 0;

            int k;
            for (k = list.size(); j < k; ++j) {
                d0 = ((AxisAlignedBB) list.get(j)).a(this.getBoundingBox(), d0);
            }

            this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));
            j = 0;

            for (k = list.size(); j < k; ++j) {
                d2 = ((AxisAlignedBB) list.get(j)).c(this.getBoundingBox(), d2);
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));
            final double d21;
            double d10;
            if (this.P > 0.0F && flag1 && (d6 != d0 || d8 != d2)) {
                final double d11 = d0;
                final double d12 = d1;
                d21 = d2;
                final AxisAlignedBB event = this.getBoundingBox();
                this.a(axisalignedbb);
                d1 = (double) this.P;
                final List list1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
                AxisAlignedBB f = this.getBoundingBox();
                final AxisAlignedBB axisalignedbb3 = f.a(d6, 0.0D, d8);
                d10 = d1;
                int l = 0;

                for (final int i1 = list1.size(); l < i1; ++l) {
                    d10 = ((AxisAlignedBB) list1.get(l)).b(axisalignedbb3, d10);
                }

                f = f.c(0.0D, d10, 0.0D);
                double d14 = d6;
                int j1 = 0;

                for (final int k1 = list1.size(); j1 < k1; ++j1) {
                    d14 = ((AxisAlignedBB) list1.get(j1)).a(f, d14);
                }

                f = f.c(d14, 0.0D, 0.0D);
                double d15 = d8;
                int l1 = 0;

                for (final int axisalignedbb4 = list1.size(); l1 < axisalignedbb4; ++l1) {
                    d15 = ((AxisAlignedBB) list1.get(l1)).c(f, d15);
                }

                f = f.c(0.0D, 0.0D, d15);
                AxisAlignedBB var80 = this.getBoundingBox();
                double d16 = d1;
                int j2 = 0;

                for (final int k2 = list1.size(); j2 < k2; ++j2) {
                    d16 = ((AxisAlignedBB) list1.get(j2)).b(var80, d16);
                }

                var80 = var80.c(0.0D, d16, 0.0D);
                double d17 = d6;
                int l2 = 0;

                for (final int i3 = list1.size(); l2 < i3; ++l2) {
                    d17 = ((AxisAlignedBB) list1.get(l2)).a(var80, d17);
                }

                var80 = var80.c(d17, 0.0D, 0.0D);
                double d18 = d8;
                int j3 = 0;

                for (final int k3 = list1.size(); j3 < k3; ++j3) {
                    d18 = ((AxisAlignedBB) list1.get(j3)).c(var80, d18);
                }

                var80 = var80.c(0.0D, 0.0D, d18);
                final double d19 = d14 * d14 + d15 * d15;
                final double d20 = d17 * d17 + d18 * d18;
                if (d19 > d20) {
                    d0 = d14;
                    d2 = d15;
                    d1 = -d10;
                    this.a(f);
                } else {
                    d0 = d17;
                    d2 = d18;
                    d1 = -d16;
                    this.a(var80);
                }

                int l3 = 0;

                for (final int i4 = list1.size(); l3 < i4; ++l3) {
                    d1 = ((AxisAlignedBB) list1.get(l3)).b(this.getBoundingBox(), d1);
                }

                this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
                if (d11 * d11 + d21 * d21 >= d0 * d0 + d2 * d2) {
                    d0 = d11;
                    d1 = d12;
                    d2 = d21;
                    this.a(event);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d6 != d0 || d8 != d2;
            this.B = d7 != d1;
            this.onGround = this.B && d7 < 0.0D;
            this.C = this.positionChanged || this.B;
            j = MathHelper.floor(this.locX);
            k = MathHelper.floor(this.locY - 0.20000000298023224D);
            final int j4 = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(j, k, j4);
            IBlockData iblockdata = this.world.getType(blockposition);
            if (iblockdata.getMaterial() == Material.AIR) {
                final BlockPosition block1 = blockposition.down();
                final IBlockData flag2 = this.world.getType(block1);
                final net.minecraft.server.v1_10_R1.Block event1 = flag2.getBlock();
                if (event1 instanceof BlockFence || event1 instanceof BlockCobbleWall || event1 instanceof BlockFenceGate) {
                    iblockdata = flag2;
                    blockposition = block1;
                }
            }

            this.a(d1, this.onGround, iblockdata, blockposition);
            if (d6 != d0) {
                this.motX = 0.0D;
            }

            if (d8 != d2) {
                this.motZ = 0.0D;
            }

            final net.minecraft.server.v1_10_R1.Block var81 = iblockdata.getBlock();
            if (d7 != d1) {
                var81.a(this.world, this);
            }

            if (this.positionChanged) {
                org.bukkit.block.Block var84 = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                if (d6 > d0) {
                    var84 = var84.getRelative(BlockFace.EAST);
                } else if (d6 < d0) {
                    var84 = var84.getRelative(BlockFace.WEST);
                } else if (d8 > d2) {
                    var84 = var84.getRelative(BlockFace.SOUTH);
                } else if (d8 < d2) {
                    var84 = var84.getRelative(BlockFace.NORTH);
                }
                Bukkit.getPluginManager().callEvent(new BallHitWallEvent(this.ball, var84));
            }
        }
        this.spigotTimings(false);
    }

    @Override
    public void recalcPosition() {
        final AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.locX = (axisalignedbb.a + axisalignedbb.d) / 2.0D;
        this.locY = axisalignedbb.b;
        this.locZ = (axisalignedbb.c + axisalignedbb.f) / 2.0D;
    }
}
