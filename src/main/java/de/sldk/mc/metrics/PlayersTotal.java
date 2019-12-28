package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlayersTotal extends Metric {

    private Gauge players = Gauge.build()
            .name(prefix("players_total"))
            .help("Unique players (online + offline)")
            .register();

    public PlayersTotal(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void doCollect() {
        players.set(Bukkit.getOfflinePlayers().length);
    }
}
