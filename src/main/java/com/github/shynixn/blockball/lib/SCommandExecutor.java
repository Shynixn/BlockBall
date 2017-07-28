package com.github.shynixn.blockball.lib;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Deprecated
public class SCommandExecutor implements CommandExecutor {
    protected JavaPlugin plugin;

    public SCommandExecutor() {
        super();
        this.plugin = JavaPlugin.getPlugin(BlockBallPlugin.class);
        if (this.plugin == null)
            throw new IllegalArgumentException("Pluginloader failed to load " + this.getClass().getSimpleName() + '.');
        this.plugin.getCommand(this.getCommand()).setExecutor(this);
    }

    public final String getCommand() {
        for (final Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation.annotationType() == Command.class) {
                final Command info = (Command) annotation;
                return info.command();
            }
        }
        return null;
    }

    @Override
    public final boolean onCommand(CommandSender arg0, org.bukkit.command.Command arg1, String arg2, String[] arg3) {
        for (final Method method : this.getClass().getDeclaredMethods()) {
            for (final Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType() == PlayerCommand.class) {
                    try {
                        method.setAccessible(true);
                        method.invoke(this, arg0, arg3);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        Bukkit.getLogger().log(Level.INFO, "Cannot access command.", e);
                    }
                }
                if (annotation.annotationType() == ConsoleCommand.class) {
                    try {
                        method.setAccessible(true);
                        method.invoke(this, arg0, arg3);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        Bukkit.getLogger().log(Level.INFO, "Cannot access command.", e);
                    }
                }
            }
        }
        return true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Command {
        String command() default "help";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PlayerCommand {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ConsoleCommand {
    }
}
