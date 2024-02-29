package de.sldk.mc.health;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcurrentHealthChecks implements HealthChecks {

    private final Set<HealthCheck> checks;

    private ConcurrentHealthChecks(final Set<HealthCheck> checks) {
        this.checks = checks;
    }

    public static HealthChecks create() {
        return new ConcurrentHealthChecks(ConcurrentHashMap.newKeySet());
    }

    @Override
    public boolean isHealthy() {
        for (final HealthCheck check : checks) if (!check.isHealthy()) return false;
        return true;
    }

    @Override
    public void add(final HealthCheck check) {
        checks.add(check);
    }

    @Override
    public void remove(final HealthCheck check) {
        checks.remove(check);
    }
}
