package de.sldk.mc.metrics;

import de.sldk.mc.MetricRegistry;

public abstract class Metric {

    private final static String COMMON_PREFIX = "mc_";

    public Metric() {
        MetricRegistry.getInstance().register(this);
    }

    public abstract void collect();

    public boolean isEnabledByDefault() {
        return true;
    }

    protected static String prefix(String name) {
        return COMMON_PREFIX + name;
    }

}
