package de.sldk.mc.metrics.statistics;

import de.sldk.mc.metrics.PlayerMetric;
import io.prometheus.client.Gauge;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * Offline player -> fetch data from files
 * <p>
 * Online player -> fetch data from Minecraft API
 */
public class PlayerStatistics extends PlayerMetric {

    private static final Gauge PLAYER_STATS = Gauge.build()
            .name(prefix("player_statistic"))
            .help("Player statistics")
            .labelNames("player_name", "player_uid", "statistic")
            .create();

    private static Logger logger;

    private final StatsFileReader statsFileReader;
    private final PlayerStatsFetcher playerStatsFetcher;

    public PlayerStatistics(Plugin plugin) {
        super(plugin, PLAYER_STATS);
        logger = plugin.getLogger();

        statsFileReader = new StatsFileReader(plugin);
        playerStatsFetcher = new PlayerStatsFetcher(plugin);
    }

    @Override
    public void collect(OfflinePlayer player) {
        logger.info("Collect running for PlayerStatistics");

        Map<Enum<?>, Integer> statistics;
        if (player.getPlayer() == null) {
            statistics = statsFileReader.getPlayersStats(player.getUniqueId());
        } else {
            logger.info("OfflinePlayer is null");
            statistics = playerStatsFetcher.getPlayerStats(player.getPlayer());
        }

        final String playerNameLabel = getNameOrUid(player);
        final String playerUidLabel = getUid(player);

        statistics.forEach((stat, value) -> {
                    logger.info("pushing stat: " + stat + ", val: " + value);
                    PLAYER_STATS.labels(playerNameLabel, playerUidLabel, stat.name()).set(value);
                }
        );
    }
}
