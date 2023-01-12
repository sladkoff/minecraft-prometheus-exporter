package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WhitelistedPlayers extends Metric {

    private static final Gauge PLAYERS = Gauge.build()
            .name(prefix("whitelisted_players"))
            .help("players count on the white list")
            .create();

    public WhitelistedPlayers(Plugin plugin) {
        super(plugin, PLAYERS);
    }

    @Override
    public void doCollect() {
        PLAYERS.set(Bukkit.getWhitelistedPlayers().size());
    }
}
