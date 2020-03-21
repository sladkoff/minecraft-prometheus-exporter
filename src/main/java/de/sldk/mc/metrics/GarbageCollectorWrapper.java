package de.sldk.mc.metrics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;
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
            MetricFamilySamples collected = garbageCollectorExports.collect().get(0);
            List<Sample> samples = new ArrayList<>(collected.samples.size());
            for(Sample sample : collected.samples) {
                samples.add(new Sample(prefix(sample.name), sample.labelNames, sample.labelValues, sample.value));
            }
            MetricFamilySamples prefixed = new MetricFamilySamples(prefix(collected.name), collected.type, collected.help, samples);
            List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
            mfs.add(prefixed);
            return mfs;
        }
    }
}