package de.sldk.mc.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public CompletableFuture<Void> collect() {
        if (!isEnabled()) {
            return null;
        }

        if (isAsyncCapable()) {
            CompletableFuture.runAsync(() -> {
                try {
                    doCollect();
                } catch (Exception e) {
                    logException(e);
                }
            });
        }

        CompletableFuture<Void> future = new CompletableFuture<>();

        // don't call .get() - this blocks the ForkJoinPool.commonPool and may deadlock the server in some cases
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            try {
                doCollect();
            } catch (Exception e) {
                logException(e);
            }
            future.complete(null);
            return null;
        });

        return future;
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

    public boolean isFoliaCapable() {
        return false;
    }

    private void logException(Exception e) {
        final Logger log = plugin.getLogger();
        final String className = getClass().getSimpleName();

        log.throwing(className, "collect", e);
        log.info("Failed to collect " + className + ": ");
        e.printStackTrace();
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
