package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.Map;

public class PlayersOnline extends PlayerMetric {

    private Gauge playersWithNames = Gauge.build()
            .name(prefix("player_online"))
            .help("Online state by player name")
            .labelNames("name")
            .register();

    @Override
    public void collect(OfflinePlayer player) {
        playersWithNames.labels(player.getName()).set(player.isOnline() ? 1 : 0);
    }
}
