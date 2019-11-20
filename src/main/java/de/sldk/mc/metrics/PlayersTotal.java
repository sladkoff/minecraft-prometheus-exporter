package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlayersTotal extends Metric {

    private Gauge players = Gauge.build()
            .name(prefix("players_total"))
            .help("Online and offline players")
            .labelNames("state")
            .register();

    public PlayersTotal(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void doCollect() {
        players.labels("online").set(Bukkit.getOnlinePlayers().size());
        players.labels("offline").set(Bukkit.getOfflinePlayers().length);
    }
}
