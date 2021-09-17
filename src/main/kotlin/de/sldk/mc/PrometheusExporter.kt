@file:JvmName("PrometheusExporter")

package de.sldk.mc

import de.sldk.mc.config.PrometheusExporterConfig
import de.sldk.mc.health.ConcurrentHealthChecks
import de.sldk.mc.health.HealthChecks
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


class PrometheusExporter : JavaPlugin() {
    private val config: PrometheusExporterConfig = PrometheusExporterConfig(this)
    private var server: MetricsServer? = null

    @Override
    override fun onEnable() {
        config.loadDefaultsAndSave()
        config.enableConfiguredMetrics()

        val healthChecks = ConcurrentHealthChecks.create()
        getServer().servicesManager.register(HealthChecks::class.java, healthChecks, this, ServicePriority.Normal)

        startMetricsServer(healthChecks)
    }

    private fun startMetricsServer(healthChecks: HealthChecks) {
        val host = config[PrometheusExporterConfig.HOST]
        val port = config[PrometheusExporterConfig.PORT]

        server = MetricsServer(host, port, this, healthChecks)

        try {
            server?.start()
            getLogger().info("Started Prometheus metrics endpoint at: $host:$port")
        } catch (e: Exception) {
            getLogger().severe("Could not start embedded Jetty server: " + e.message)
            getServer().getPluginManager().disablePlugin(this)
        }
    }

    @Override
    override fun onDisable() {
        try {
            server?.stop()
        } catch (e: Exception) {
            getLogger().log(Level.WARNING, "Failed to stop metrics server gracefully: " + e.message)
            getLogger().log(Level.FINE, "Failed to stop metrics server gracefully", e)
        }
    }
}
