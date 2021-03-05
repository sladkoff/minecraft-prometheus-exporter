package de.sldk.mc.metrics;

import de.sldk.mc.metrics.player.PlayerStatisticLoaderFromBukkit;
import de.sldk.mc.metrics.player.EmptyStatisticLoader;
import de.sldk.mc.metrics.player.PlayerStatisticLoaderFromFile;
import de.sldk.mc.metrics.player.PlayerStatisticLoader;
import io.prometheus.client.Gauge;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
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

	private final LinkedHashSet<PlayerStatisticLoader> statisticLoaderChain = new LinkedHashSet<>();

    public PlayerStatistics(Plugin plugin) {
        super(plugin, PLAYER_STATS);

        logger = plugin.getLogger();

		statisticLoaderChain.add(new PlayerStatisticLoaderFromBukkit(plugin));
		statisticLoaderChain.add(new PlayerStatisticLoaderFromFile(plugin));
		statisticLoaderChain.add(new EmptyStatisticLoader());
    }

    @Override
    public void collect(OfflinePlayer player) {

		for (PlayerStatisticLoader playerStatisticLoader : statisticLoaderChain) {
			if (collectSuccessful(playerStatisticLoader, player)) {
				return;
			}
		}
    }

    private boolean collectSuccessful(PlayerStatisticLoader loader, OfflinePlayer player) {
		final String playerNameLabel = getNameOrUid(player);
		final String playerUidLabel = getUid(player);

		try {
			Map<Enum<?>, Integer> statistics = loader.getPlayerStatistics(player);

			if (statistics == null || statistics.isEmpty()) {
				return false;
			}

			statistics.forEach(
					(stat, value) -> PLAYER_STATS.labels(playerNameLabel, playerUidLabel, stat.name()).set(value));

			return true;
		} catch (Exception e) {
			String message =
					String.format("%s: Could not load statistics for player '%s'", loader.getClass().getSimpleName(),
							player.getUniqueId());
			logger.log(Level.WARNING, message, e);
			return false;
		}
	}
}
