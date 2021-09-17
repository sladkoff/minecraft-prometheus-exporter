package de.sldk.mc;

import de.sldk.mc.config.PrometheusExporterConfig;
import de.sldk.mc.health.ConcurrentHealthChecks;
import de.sldk.mc.health.HealthChecks;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PrometheusExporter extends JavaPlugin {

    private final PrometheusExporterConfig config = new PrometheusExporterConfig(this);
    private MetricsServer server;

    @Override
    public void onEnable() {

        config.loadDefaultsAndSave();

        config.enableConfiguredMetrics();

        HealthChecks healthChecks = ConcurrentHealthChecks.create();
        getServer().getServicesManager().register(HealthChecks.class, healthChecks, this, ServicePriority.Normal);

        startMetricsServer(healthChecks);
    }

    private void startMetricsServer(HealthChecks healthChecks) {
        String host = config.get(PrometheusExporterConfig.HOST);
        Integer port = config.get(PrometheusExporterConfig.PORT);

		server = new MetricsServer(host, port, this, healthChecks);

        try {
            server.start();
            getLogger().info("Started Prometheus metrics endpoint at: " + host + ":" + port);
        } catch (Exception e) {
            getLogger().severe("Could not start embedded Jetty server");
        }
    }

    @Override
    public void onDisable() {
        try {
            server.stop();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to stop metrics server gracefully: " + e.getMessage());
            getLogger().log(Level.FINE, "Failed to stop metrics server gracefully", e);
        }
    }

}
