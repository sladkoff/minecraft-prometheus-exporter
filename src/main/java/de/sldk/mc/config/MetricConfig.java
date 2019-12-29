package de.sldk.mc.config;

import de.sldk.mc.metrics.Metric;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class MetricConfig extends PluginConfig<Boolean> {

    private static final String CONFIG_PATH_PREFIX = "enable_metrics";

    private Function<Plugin, Metric> metricInitializer;

    protected MetricConfig(String key, Boolean defaultValue, Function<Plugin, Metric> metricInitializer) {
        super(CONFIG_PATH_PREFIX + "." + key, defaultValue);
        this.metricInitializer = metricInitializer;
    }

    public Metric getMetric(Plugin plugin) {
        return metricInitializer.apply(plugin);
    }
}
