package de.sldk.mc.metrics;

import org.bukkit.plugin.Plugin;

public abstract class Metric {

    private final static String COMMON_PREFIX = "mc_";

    private boolean enabled = false;
    private final Plugin plugin;

    protected Metric(Plugin plugin) {
        this.plugin = plugin;
    }

    protected Plugin getPlugin() {
        return plugin;
    }

    public void collect() {
        if (enabled) {
            doCollect();
        }
    }

    protected abstract void doCollect();

    protected static String prefix(String name) {
        return COMMON_PREFIX + name;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
