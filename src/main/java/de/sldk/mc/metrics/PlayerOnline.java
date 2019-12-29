package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class PlayerOnline extends PlayerMetric {

    private static final Gauge PLAYERS_WITH_NAMES = Gauge.build()
            .name(prefix("player_online"))
            .help("Online state by player name")
            .labelNames("name")
            .create();

    public PlayerOnline(Plugin plugin) {
        super(plugin, PLAYERS_WITH_NAMES);
    }

    @Override
    public void collect(OfflinePlayer player) {
        PLAYERS_WITH_NAMES.labels(player.getName()).set(player.isOnline() ? 1 : 0);
    }
}
