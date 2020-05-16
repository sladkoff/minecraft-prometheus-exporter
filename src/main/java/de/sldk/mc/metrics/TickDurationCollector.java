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
            long[] longestArray = null;

            try {
                /* Get the actual minecraft server class */
                Server server = Bukkit.getServer();
                Method getServerMethod = server.getClass().getMethod("getServer");
                Object minecraftServer = getServerMethod.invoke(server);

                /* Look for the only array of longs in that class, which is tick duration */
                for (Field field : minecraftServer.getClass().getSuperclass().getDeclaredFields()) {
                    if (field.getType().isArray() && field.getType().getComponentType().equals(long.class)) {
                        /* Check all the long[] items in this class, and remember the one with the most elements */
                        long[] array = (long[]) field.get(minecraftServer);
                        if (array != null && (longestArray == null || array.length > longestArray.length)) {
                            longestArray = array;
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.FINE, "Caught exception looking for tick times array: ", e);
            }

            if (longestArray != null) {
                tickDurationReference = longestArray;
            } else {
                /* No array was found, use an placeholder */
                tickDurationReference = new long[1];
                tickDurationReference[0] = -1;

                plugin.getLogger().log(Level.WARNING, "Failed to find tick times buffer via reflection. Tick duration metrics will not be available.");
            }
        }
    }

    /**
     * Returns either the internal minecraft long array for tick times in ns,
     * or a long array containing just one element of value -1 if reflection
     * was unable to locate the minecraft tick times buffer
     */
    protected static long[] getTickDurations() {
        return tickDurationReference;
    }
}
