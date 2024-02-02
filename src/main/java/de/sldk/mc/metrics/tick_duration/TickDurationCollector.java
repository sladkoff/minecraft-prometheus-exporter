package de.sldk.mc.metrics.tick_duration;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TickDurationCollector {

    private final TickDurationStrategy strategy;

    public TickDurationCollector(TickDurationStrategy strategy) {
        this.strategy = strategy;
    }

    public static TickDurationCollector forServerImplementation(Plugin plugin) {
        Logger logger = plugin.getLogger();
        PaperTickDurationStrategy paperStrategy = new PaperTickDurationStrategy();

        if (paperStrategy.getTickDurations() != null) {
            logger.log(Level.FINE, "Using Paper tick times method.");
            return new TickDurationCollector(paperStrategy);
        }

        logger.log(Level.FINE, "Using default tick times guessing method.");
        DefaultTickDurationStrategy defaultTickDurationStrategy = new DefaultTickDurationStrategy(logger);
        return new TickDurationCollector(defaultTickDurationStrategy);
    }

    public long[] getTickDurations() {
        return strategy.getTickDurations();
    }
}
