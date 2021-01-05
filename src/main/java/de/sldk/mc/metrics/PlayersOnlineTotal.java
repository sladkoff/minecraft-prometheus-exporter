package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class PlayersOnlineTotal extends WorldMetric {

    private static final Gauge PLAYERS_ONLINE = Gauge.build()
            .name(prefix("players_online_total"))
            .help("Players currently online per world")
            .labelNames("world")
            .create();

    public PlayersOnlineTotal(Plugin plugin) {
        super(plugin, PLAYERS_ONLINE);
    }

    @Override
    protected void clear() {
    }

    @Override
    protected void collect(World world) {
        PLAYERS_ONLINE.labels(world.getName()).set(world.getPlayers().size());
    }
}
