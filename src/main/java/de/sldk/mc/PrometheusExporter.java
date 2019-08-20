package de.sldk.mc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;

import java.net.InetSocketAddress;
import java.util.logging.Level;

public class PrometheusExporter extends JavaPlugin {

    private FileConfiguration config = getConfig();
    private Server server;

    @Override
    public void onEnable() {

        PluginConfig.HOST.setDefault(config);
        PluginConfig.PORT.setDefault(config);

        config.options().copyDefaults(true);
        saveConfig();

        int port = PluginConfig.PORT.get(config);
        String host = PluginConfig.HOST.get(config);

        InetSocketAddress address = new InetSocketAddress(host, port);
        server = new Server(address);
        server.setHandler(new MetricsController(this));

        try {
            server.start();
            getLogger().info("Started Prometheus metrics endpoint at: " + host + ":" + port);

        } catch (Exception e) {
            getLogger().severe("Could not start embedded Jetty server");
        }
    }

    @Override
    public void onDisable() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to stop metrics server gracefully: " + e.getMessage());
                getLogger().log(Level.FINE, "Failed to stop metrics server gracefully", e);
            }
        }
    }

}
