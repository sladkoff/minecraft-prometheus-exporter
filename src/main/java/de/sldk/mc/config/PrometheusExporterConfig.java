package de.sldk.mc.config;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import de.sldk.mc.MetricRegistry;
import de.sldk.mc.PrometheusExporter;
import de.sldk.mc.metrics.Entities;
import de.sldk.mc.metrics.GarbageCollectorWrapper;
import de.sldk.mc.metrics.LivingEntities;
import de.sldk.mc.metrics.LoadedChunks;
import de.sldk.mc.metrics.Memory;
import de.sldk.mc.metrics.Metric;
import de.sldk.mc.metrics.PlayerOnline;
import de.sldk.mc.metrics.PlayerStatistics;
import de.sldk.mc.metrics.PlayersOnlineTotal;
import de.sldk.mc.metrics.PlayersTotal;
import de.sldk.mc.metrics.ThreadsWrapper;
import de.sldk.mc.metrics.Tps;

public class PrometheusExporterConfig {

    public static final PluginConfig<String> HOST = new PluginConfig<>("host", "localhost");
    public static final PluginConfig<Integer> PORT = new PluginConfig<>("port", 9225);
    public static final List<MetricConfig> METRICS = Arrays.asList(
            metricConfig("entities_total", true, Entities::new),
            metricConfig("loaded_chunks_total", true, LoadedChunks::new),
            metricConfig("jvm_memory", true, Memory::new),
            metricConfig("players_online_total", true, PlayersOnlineTotal::new),
            metricConfig("players_total", true, PlayersTotal::new),
            metricConfig("tps", true, Tps::new),

            metricConfig("jvm_threads", true, ThreadsWrapper::new),
            metricConfig("jvm_gc", true, GarbageCollectorWrapper::new),

            metricConfig("player_online", false, PlayerOnline::new),
            metricConfig("player_statistic", false, PlayerStatistics::new));

    private final PrometheusExporter prometheusExporter;

    public PrometheusExporterConfig(PrometheusExporter prometheusExporter) {
        this.prometheusExporter = prometheusExporter;
    }

    private static MetricConfig metricConfig(String key, boolean defaultValue, Function<Plugin, Metric> metricInitializer) {
        return new MetricConfig(key, defaultValue, metricInitializer);
    }

    public void loadDefaultsAndSave() {
        FileConfiguration configFile = prometheusExporter.getConfig();

        PrometheusExporterConfig.HOST.setDefault(configFile);
        PrometheusExporterConfig.PORT.setDefault(configFile);
        PrometheusExporterConfig.METRICS.forEach(metric -> metric.setDefault(configFile));

        configFile.options().copyDefaults(true);

        prometheusExporter.saveConfig();
    }

    public void enableConfiguredMetrics() {
        PrometheusExporterConfig.METRICS
                .forEach(metricConfig -> {
                    Metric metric = metricConfig.getMetric(prometheusExporter);
                    Boolean enabled = get(metricConfig);

                    if (Boolean.TRUE.equals(enabled)) {
                        metric.enable();
                    }

                    prometheusExporter.getLogger().fine("Metric " + metric.getClass().getSimpleName() + " enabled: " + enabled);

                    MetricRegistry.getInstance().register(metric);
                });
    }

    public <T> T get(PluginConfig<T> config) {
        return config.get(prometheusExporter.getConfig());
    }
}
