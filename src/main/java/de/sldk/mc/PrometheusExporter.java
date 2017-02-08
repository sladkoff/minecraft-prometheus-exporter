package de.sldk.mc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;

public class PrometheusExporter extends JavaPlugin {

    FileConfiguration config = getConfig();
    private Server server;

    @Override
    public void onEnable() {

        config.addDefault("port", 9552);
        config.options().copyDefaults(true);
        saveConfig();

        server = new Server(config.getInt("port"));

        server.setHandler(new MetricsController());

        try {
            server.start();

            getLogger().info("Started embedded Jetty for Prometheus metrics.");

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
                e.printStackTrace();
            }
        }
    }
}
