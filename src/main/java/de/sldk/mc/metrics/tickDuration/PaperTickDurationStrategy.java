package de.sldk.mc.metrics.tickDuration;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Method;

public class PaperTickDurationStrategy implements TickDurationStrategy {

    public PaperTickDurationStrategy() {
    }

    /**
     * Attempts to get tick times from Paper
     * returns null if fails
     */
    private static long[] getPaperTickTimes() {
        try {
            /* Get the actual minecraft server class */
            Server server = Bukkit.getServer();

            /* Attempt to get Paper tick times method */
            Method paperGetTickTimesMethod = server.getClass().getMethod("getTickTimes");

            Object tickTimes = paperGetTickTimesMethod.invoke(server);

            /* Check the method actual return type */
            if (!(tickTimes instanceof long[])) {
                return null;
            }

            return (long[]) tickTimes;
        } catch (Exception e) {
            return null;
        }
    }

    public long[] getTickDurations() {
        return getPaperTickTimes();
    }
}
