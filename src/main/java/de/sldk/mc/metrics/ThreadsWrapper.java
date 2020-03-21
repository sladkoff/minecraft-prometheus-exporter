package de.sldk.mc.metrics;

import java.util.List;

import org.bukkit.plugin.Plugin;

import io.prometheus.client.Collector;
import io.prometheus.client.hotspot.ThreadExports;

public class ThreadsWrapper extends Metric {
    public ThreadsWrapper(Plugin plugin) {
        super(plugin, new ThreadExportsCollector());
    }

    @Override
    protected void doCollect() {}

    private static class ThreadExportsCollector extends Collector {
        private static final ThreadExports threadExports = new ThreadExports();

        @Override
        public List<MetricFamilySamples> collect() {
            return HotspotPrefixer.prefixFromCollector(threadExports);
        }
    }
}