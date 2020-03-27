package de.sldk.mc.metrics;

import java.util.ArrayList;
import java.util.List;

import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;

public final class HotspotPrefixer {
    protected static List<MetricFamilySamples> prefixFromCollector(Collector collector) {
        List<MetricFamilySamples> collected = collector.collect();
        List<MetricFamilySamples> mfs = new ArrayList<>();

        for (MetricFamilySamples mSample : collected) {
            List<Sample> samples = new ArrayList<>(mSample.samples.size());
            for (Sample sample : mSample.samples) {
                samples.add(new Sample(Metric.prefix(sample.name), sample.labelNames, sample.labelValues, sample.value));
            }

            MetricFamilySamples prefixed = new MetricFamilySamples(Metric.prefix(mSample.name), mSample.type, mSample.help, samples);
            mfs.add(prefixed);
        }
        return mfs;
    }
}