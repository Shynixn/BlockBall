package com.github.shynixn.blockball.lib;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Shynixn
 */

@Deprecated
public abstract class AsyncRunnable<T> implements Runnable {
    @SPluginLoader.PluginLoader
    private static JavaPlugin plugin;
    private boolean isSynchrone;
    private T[] paramcache;

    public static boolean isPrimaryThread() {
        final Thread mainThread = (Thread) ReflectionLib.getValueFromFieldByObject("primaryThread", ReflectionLib.invokeMethod(ReflectionLib.getClassFromName("net.minecraft.server.VERSION.MinecraftServer"), "getServer"));
        return Thread.currentThread() != mainThread;
    }

    public static void throwExceptionIfSynchroneThread() {
        if (isPrimaryThread())
            throw new RuntimeException("Cannot access data from primary thread!");
    }

    public static void throwExceptionIfAsnychroneThread() {
        if (!isPrimaryThread())
            throw new RuntimeException("Cannot access data from secondary thread!");
    }

    public T getParam(int number) {
        if (this.paramcache.length > number && number >= 0)
            return this.paramcache[number];
        return null;
    }

    public boolean isSynchrone() {
        return this.isSynchrone;
    }

    public static void toAsynchroneThread(final AsyncRunnable runnable, final Object... params) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            runnable.paramcache = params;
            runnable.isSynchrone = false;
            runnable.run();
        });
    }

    protected static void toSynchroneThread(final AsyncRunnable runnable, final Object... params) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            runnable.paramcache = params;
            runnable.isSynchrone = true;
            runnable.run();
        });
    }
}
