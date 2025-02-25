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
    private var metricsServer: MetricsServer? = null

    @Override
    override fun onEnable() {
        config.loadDefaultsAndSave()
        config.enableConfiguredMetrics()

        val healthChecks = ConcurrentHealthChecks.create()
        server.servicesManager.register(HealthChecks::class.java, healthChecks, this, ServicePriority.Normal)

        startMetricsServer(healthChecks)
    }

    private fun getConfigValue(envKey: String, propertyKey: String, defaultValue: String): String {
        return System.getenv(envKey) ?: System.getProperty(propertyKey) ?: defaultValue
    }

    private fun startMetricsServer(healthChecks: HealthChecks) {
        val host = getConfigValue(
            envKey = "MINECRAFT_PROMETHEUS_EXPORTER_HOST",
            propertyKey = "minecraft.prometheus.exporter.host",
            defaultValue = config[PrometheusExporterConfig.HOST].toString()
        )

        val port = getConfigValue(
            envKey = "MINECRAFT_PROMETHEUS_EXPORTER_PORT",
            propertyKey = "minecraft.prometheus.exporter.port",
            defaultValue = config[PrometheusExporterConfig.PORT].toString()
        ).toInt()

        metricsServer = MetricsServer(host, port, this, healthChecks)

        try {
            metricsServer?.start()
            getLogger().info("Started Prometheus metrics endpoint at: $host:$port")
        } catch (e: Exception) {
            getLogger().severe("Could not start embedded Jetty server: " + e.message)
            getServer().getPluginManager().disablePlugin(this)
        }
    }

    @Override
    override fun onDisable() {
        try {
            metricsServer?.stop()
        } catch (e: Exception) {
            getLogger().log(Level.WARNING, "Failed to stop metrics server gracefully: " + e.message)
            getLogger().log(Level.FINE, "Failed to stop metrics server gracefully", e)
        }
    }
}
