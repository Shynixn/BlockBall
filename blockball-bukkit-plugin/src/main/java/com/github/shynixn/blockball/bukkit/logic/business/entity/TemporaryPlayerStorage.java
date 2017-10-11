package com.github.shynixn.blockball.bukkit.logic.business.entity;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.io.Closeable;
import java.util.HashMap;

class TemporaryPlayerStorage implements AutoCloseable{
    ItemStack[] inventory;
    ItemStack[] armorContent;
    Integer level;
    Float exp;
    boolean isFlying;
    GameMode gameMode;
    Integer foodLevel;
    Double health;
    Scoreboard scoreboard;
    float walkingSpeed = 0.2F;
    Team team;
    Player player;

    public TemporaryPlayerStorage(Player player) {
        this.player = player;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TemporaryPlayerStorage && ((TemporaryPlayerStorage) obj).player.equals(this.player);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        if (storage.inventory != null) {
            player.getInventory().setContents(storage.inventory);
        }
        if (storage.armorContent != null) {
            player.getInventory().setArmorContents(storage.armorContent);
        }
        if (storage.gameMode != null) {
            player.setGameMode(storage.gameMode);
        }
        this.arena.getTeamMeta().getBossBar().stopPlay(this.bossBar, player);
        this.removePlayerFromScoreboard(player);
        if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
            NMSRegistry.setBossBar(player, null);
        }
        if (!ReflectionLib.getServerVersion().contains("1_8")) {
            Interpreter19.setGlowing(player, false);
        }
        if (this.getHologram() != null) {
            this.getHologram().remove(player);
        }
        if (storage.level != null) {
            player.setLevel(storage.level);
        }
        if (storage.exp != null) {
            player.setExp(storage.exp);
        }
        if (storage.foodLevel != null) {
            player.setFoodLevel(storage.foodLevel);
        }
        if (storage.health != null) {
            player.setHealthScale(storage.health);
        }
        if (storage.scoreboard != null) {
            player.setScoreboard(storage.scoreboard);
        }
        player.setWalkSpeed(storage.walkingSpeed);
        player.setFlying(false);
        player.setAllowFlight(storage.isFlying);
        player.updateInventory();
        this.temporaryStorage.remove(player);
    }
}