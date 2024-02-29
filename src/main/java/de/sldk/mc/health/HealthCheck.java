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
     * Creates a compound health check from the provided ones reporting healthy status if all the checks report it.
     *
     * @param checks merged health checks
     * @return compound health check
     */
    static HealthCheck allOf(final HealthCheck... checks) {
        return new AllOf(checks);
    }

    /**
     * Creates a compound health check from the provided ones reporting healthy status if any check reports it.
     *
     * @param checks merged health checks
     * @return compound health check
     */
    static HealthCheck anyOf(final HealthCheck... checks) {
        return new AnyOf(checks);
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
