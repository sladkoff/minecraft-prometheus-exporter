package de.sldk.mc.metrics.folia;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoliaTickStatistics {

    private static final Logger LOGGER = Logger.getLogger(FoliaTickStatistics.class.getName());
    private static final RegioniserReflection REGIONISER_REFLECTION = new RegioniserReflection();

    private final Supplier<List<Object>> regionSupplier;

    public FoliaTickStatistics() {
        this.regionSupplier = new MemoizingSupplier<>(() -> getRegions(Bukkit.getServer()), 5, TimeUnit.MILLISECONDS);
    }

    public double getTps() {
        long nanoTime = System.nanoTime();
        return regionSupplier.get().stream()
                .map(region -> getSchedulingHandle(region))
                .filter(Objects::nonNull)
                .mapToDouble(handle -> getTpsFromHandle(handle, nanoTime))
                .average()
                .orElse(20.0);
    }

    public long getTickDurationAverage() {
        long nanoTime = System.nanoTime();
        return (long) regionSupplier.get().stream()
                .map(region -> getSchedulingHandle(region))
                .filter(Objects::nonNull)
                .mapToDouble(handle -> getTimePerTickAverage(handle, nanoTime))
                .average()
                .orElse(50_000_000L);
    }

    public long getTickDurationMedian() {
        long nanoTime = System.nanoTime();
        return (long) regionSupplier.get().stream()
                .map(region -> getSchedulingHandle(region))
                .filter(Objects::nonNull)
                .mapToDouble(handle -> getTimePerTickMedian(handle, nanoTime))
                .average()
                .orElse(50_000_000L);
    }

    public long getTickDurationMin() {
        long nanoTime = System.nanoTime();
        return regionSupplier.get().stream()
                .map(region -> getSchedulingHandle(region))
                .filter(Objects::nonNull)
                .mapToLong(handle -> getTimePerTickMin(handle, nanoTime))
                .min()
                .orElse(50_000_000L);
    }

    public long getTickDurationMax() {
        long nanoTime = System.nanoTime();
        return regionSupplier.get().stream()
                .map(region -> getSchedulingHandle(region))
                .filter(Objects::nonNull)
                .mapToLong(handle -> getTimePerTickMax(handle, nanoTime))
                .max()
                .orElse(50_000_000L);
    }

    private static Object getSchedulingHandle(Object region) {
        try {
            Object data = region.getClass().getMethod("getData").invoke(region);
            return data.getClass().getMethod("getRegionSchedulingHandle").invoke(data);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to get region scheduling handle", e);
            return null;
        }
    }

    private static double getTpsFromHandle(Object handle, long nanoTime) {
        try {
            Object report = getTickReport(handle, nanoTime);
            if (report == null) return 20.0;
            Object tpsData = report.getClass().getMethod("tpsData").invoke(report);
            Object segmentAll = tpsData.getClass().getMethod("segmentAll").invoke(tpsData);
            return (double) segmentAll.getClass().getMethod("average").invoke(segmentAll);
        } catch (Exception e) {
            return 20.0;
        }
    }

    private static double getTimePerTickAverage(Object handle, long nanoTime) {
        try {
            Object report = getTickReport(handle, nanoTime);
            if (report == null) return 50_000_000L;
            Object timePerTick = report.getClass().getMethod("timePerTickData").invoke(report);
            Object segmentAll = timePerTick.getClass().getMethod("segmentAll").invoke(timePerTick);
            return (double) segmentAll.getClass().getMethod("average").invoke(segmentAll);
        } catch (Exception e) {
            return 50_000_000L;
        }
    }

    private static double getTimePerTickMedian(Object handle, long nanoTime) {
        try {
            Object report = getTickReport(handle, nanoTime);
            if (report == null) return 50_000_000L;
            Object timePerTick = report.getClass().getMethod("timePerTickData").invoke(report);
            Object segmentAll = timePerTick.getClass().getMethod("segmentAll").invoke(timePerTick);
            return (double) segmentAll.getClass().getMethod("median").invoke(segmentAll);
        } catch (Exception e) {
            return 50_000_000L;
        }
    }

    private static long getTimePerTickMin(Object handle, long nanoTime) {
        try {
            Object report = getTickReport(handle, nanoTime);
            if (report == null) return 50_000_000L;
            Object timePerTick = report.getClass().getMethod("timePerTickData").invoke(report);
            Object segmentAll = timePerTick.getClass().getMethod("segmentAll").invoke(timePerTick);
            return (long) (double) segmentAll.getClass().getMethod("least").invoke(segmentAll);
        } catch (Exception e) {
            return 50_000_000L;
        }
    }

    private static long getTimePerTickMax(Object handle, long nanoTime) {
        try {
            Object report = getTickReport(handle, nanoTime);
            if (report == null) return 50_000_000L;
            Object timePerTick = report.getClass().getMethod("timePerTickData").invoke(report);
            Object segmentAll = timePerTick.getClass().getMethod("segmentAll").invoke(timePerTick);
            return (long) (double) segmentAll.getClass().getMethod("greatest").invoke(segmentAll);
        } catch (Exception e) {
            return 50_000_000L;
        }
    }

    private static Object getTickReport(Object handle, long nanoTime) {
        try {
            return handle.getClass().getMethod("getTickReport1m", long.class).invoke(handle, nanoTime);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getRegions(Server server) {
        List<Object> regions = new ArrayList<>();
        for (World world : server.getWorlds()) {
            Object regionizer = REGIONISER_REFLECTION.getRegioniser(world);
            if (regionizer != null) {
                try {
                    regionizer.getClass().getMethod("computeForAllRegions", java.util.function.Consumer.class)
                            .invoke(regionizer, (java.util.function.Consumer<Object>) regions::add);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Failed to compute regions for world", e);
                }
            }
        }
        return regions;
    }

    private static final class RegioniserReflection {
        private boolean initialised = false;
        private Method getHandleMethod;
        private Field regioniserField;

        private void initialise(Class<? extends World> craftWorldClass) {
            try {
                this.getHandleMethod = craftWorldClass.getMethod("getHandle");
                this.regioniserField = this.getHandleMethod.getReturnType().getField("regioniser");
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Failed to initialise regioniser reflection", e);
            }
        }

        public Object getRegioniser(World world) {
            if (!this.initialised) {
                this.initialised = true;
                initialise(world.getClass());
            }
            if (this.getHandleMethod == null || this.regioniserField == null) {
                return null;
            }

            try {
                Object handle = this.getHandleMethod.invoke(world);
                return this.regioniserField.get(handle);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Failed to get regioniser for world", e);
                return null;
            }
        }
    }

    private static final class MemoizingSupplier<T> implements Supplier<T> {
        private final Supplier<T> delegate;
        private final long expiryNanos;
        private volatile long lastFetchNanos;
        private volatile T cachedValue;

        MemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
            this.delegate = delegate;
            this.expiryNanos = unit.toNanos(duration);
            this.lastFetchNanos = Long.MIN_VALUE;
        }

        @Override
        public T get() {
            long now = System.nanoTime();
            if (now - lastFetchNanos >= expiryNanos) {
                T value = delegate.get();
                cachedValue = value;
                lastFetchNanos = now;
            }
            return cachedValue;
        }
    }
}
