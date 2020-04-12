package de.sldk.mc.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public abstract class Metric {

    private final static String COMMON_PREFIX = "mc_";

    private final Plugin plugin;
    private final Collector collector;

    private boolean enabled = false;

    protected Metric(Plugin plugin, Collector collector) {
        this.plugin = plugin;
        this.collector = collector;
    }

    protected Plugin getPlugin() {
        return plugin;
    }

    public void collect() {

        if (!enabled) {
            return;
        }

        try {
            doCollect();
        } catch (Exception e) {
            logException(e);
        }
    }

    protected abstract void doCollect();

    private void logException(Exception e) {
        final Logger log = plugin.getLogger();
        final String className = getClass().getSimpleName();

        log.warning(String.format("Failed to collect metric '%s' (see FINER log for stacktrace): %s",
                className, e.toString()));
        log.throwing(className, "collect", e);
    }

    protected static String prefix(String name) {
        return COMMON_PREFIX + name;
    }

    public void enable() {
        CollectorRegistry.defaultRegistry.register(collector);
        enabled = true;
    }

    public void disable() {
        CollectorRegistry.defaultRegistry.unregister(collector);
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
