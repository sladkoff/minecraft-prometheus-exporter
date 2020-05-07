package de.sldk.mc.metrics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import io.prometheus.client.Gauge;

public abstract class TickDurationCollector extends Metric {
    /*
     * If reflection is successful, this will hold a reference directly to the
     * MinecraftServer internal tick duration tracker
     */
    private static long[] tickDurationReference = null;

    public TickDurationCollector(Plugin plugin, Gauge gauge, String name) {
        super(plugin, gauge);

        /*
         * If there is not yet a handle to the internal tick duration buffer, try
         * to acquire one using reflection.
         *
         * This searches for any long[] array in the MinecraftServer class. It should
         * work across many versions of Spigot/Paper and various obfuscation mappings
         */
        if (tickDurationReference == null) {
            try {
                /* Get the actual minecraft server class */
                Server server = Bukkit.getServer();
                Method getServerMethod = server.getClass().getMethod("getServer");
                Object minecraftServer = getServerMethod.invoke(server);

                /* Look for the only array of longs in that class, which is tick duration */
                for (Field field : minecraftServer.getClass().getSuperclass().getDeclaredFields()) {
                    if (field.getType().isArray() && field.getType().getComponentType().equals(long.class)) {
                        tickDurationReference = (long[]) field.get(minecraftServer);
                        return;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.FINE, "Caught exception looking for tick times array: ", e);
            }
            plugin.getLogger().log(Level.WARNING, "Failed to find tick times buffer via reflection; " + name + " data not available");
        }
    }

    protected static long[] getTickDurations() {
        return tickDurationReference;
    }
}
