package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class PlayersOnlineTotal extends WorldMetric {

    private Gauge playersOnline = Gauge.build()
            .name(prefix("players_online_total"))
            .help("Players currently online per world")
            .labelNames("world")
            .register();

    public PlayersOnlineTotal(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void collect(World world) {
        playersOnline.labels(world.getName()).set(world.getPlayers().size());
    }
}
