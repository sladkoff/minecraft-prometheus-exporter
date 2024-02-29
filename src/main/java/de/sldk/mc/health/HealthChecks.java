package de.sldk.mc.health;

/**
 * Dynamic compound health checks.
 */
public interface HealthChecks extends HealthCheck {

    /**
     * Adds the provided health check to this one.
     *
     * @param check added health check
     */
    void add(HealthCheck check);

    /**
     * Removes the provided health check from this one.
     *
     * @param check removed health check
     */
    void remove(HealthCheck check);
}
