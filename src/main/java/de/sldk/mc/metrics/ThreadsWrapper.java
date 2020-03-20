package de.sldk.mc.metrics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;
import io.prometheus.client.hotspot.ThreadExports;

public class ThreadsWrapper extends Metric {
    public ThreadsWrapper(Plugin plugin) {
        super(plugin, new ThreadExportsCollector());
    }

    @Override
    protected void doCollect() {}

    private static class ThreadExportsCollector extends Collector {
        private static ThreadExports threadExports = new ThreadExports();
        @Override
        public List<MetricFamilySamples> collect() {
            List<MetricFamilySamples> collected = threadExports.collect();
            List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
            
            for(MetricFamilySamples mSample : collected) {
                List<Sample> samples = new ArrayList<>(mSample.samples.size());
                for(Sample sample : mSample.samples) {
                    samples.add(new Sample(prefix(sample.name), sample.labelNames, sample.labelValues, sample.value));
                }

                MetricFamilySamples prefixed = new MetricFamilySamples(prefix(mSample.name), mSample.type, mSample.help, samples);
                mfs.add(prefixed);
            }
            return mfs;
        }
    }
}