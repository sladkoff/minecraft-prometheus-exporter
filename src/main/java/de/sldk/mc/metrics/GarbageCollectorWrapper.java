package de.sldk.mc.metrics;

import java.util.List;

import org.bukkit.plugin.Plugin;

import io.prometheus.client.Collector;
import io.prometheus.client.hotspot.GarbageCollectorExports;

public class GarbageCollectorWrapper extends Metric {
    public GarbageCollectorWrapper(Plugin plugin) {
        super(plugin, new GarbageCollectorExportsCollector());
    }

    @Override
    protected void doCollect() {}

    private static class GarbageCollectorExportsCollector extends Collector {
        private static final GarbageCollectorExports garbageCollectorExports = new GarbageCollectorExports();

        @Override
        public List<MetricFamilySamples> collect() {
            return HotspotPrefixer.prefixFromCollector(garbageCollectorExports);
        }
    }
}