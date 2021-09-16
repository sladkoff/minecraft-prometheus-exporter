package de.sldk.mc.health;

/**
 * Health check.
 */
public interface HealthCheck {

    /**
     * Checks if the current state is healthy.
     *
     * @return {@code true} if the state is healthy and {@code false} otherwise
     */
    boolean isHealthy();

    /**
     * Creates a health compound check from the provided ones reporting healthy status iff all the checks report it.
     *
     * @param healthChecks merged health checks
     * @return compound health check
     */
    static HealthCheck allOf(final HealthCheck... healthChecks) {
        return new AllOf(healthChecks);
    }

    /**
     * Creates a compound health check from the provided ones reporting healthy status iff any check reports it.
     *
     * @param healthChecks merged health checks
     * @return compound health check
     */
    static HealthCheck anyOf(final HealthCheck... healthChecks) {
        return new AnyOf(healthChecks);
    }

    final class AllOf implements HealthCheck {
        private final HealthCheck[] checks;

        private AllOf(final HealthCheck[] checks) {
            this.checks = checks;
        }

        @Override
        public boolean isHealthy() {
            for (final HealthCheck check : checks) if (!check.isHealthy()) return false;

            return true;
        }
    }

    final class AnyOf implements HealthCheck {
        private final HealthCheck[] checks;

        private AnyOf(final HealthCheck[] checks) {
            this.checks = checks;
        }

        @Override
        public boolean isHealthy() {
            for (final HealthCheck check : checks) if (check.isHealthy()) return true;

            return false;
        }
    }
}
