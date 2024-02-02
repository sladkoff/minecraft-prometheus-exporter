package de.sldk.mc.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    public CompletableFuture<Void> collect() {
        return CompletableFuture.runAsync(() -> {
            if (!enabled) {
                return;
            }

            /* If metric is capable of async execution run it on a thread pool */
            if (isAsyncCapable()) {

                try {
                    doCollect();
                }
                catch (Exception e) {
                    logException(e);
                }
                return;
            }

            /*
            * Otherwise run the metric on the main thread and make the
            * thread on thread pool wait for completion
            */
            try {
                Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                    try {
                        doCollect();
                    }
                    catch (Exception e) {
                        logException(e);
                    }
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                logException(e);
            }
        });
    }

    protected abstract void doCollect();

    /**
     * This method is called during the Metric collection
     * to determine if it is safe to do it async.
     * By default, all Metrics are sync unless this method
     * is overridden.
     */
    protected boolean isAsyncCapable() {
        return false;
    }

    private void logException(Exception e) {
        final Logger log = plugin.getLogger();
        final String className = getClass().getSimpleName();

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
