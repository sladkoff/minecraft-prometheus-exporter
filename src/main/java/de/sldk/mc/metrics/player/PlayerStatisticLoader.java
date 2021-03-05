package de.sldk.mc.metrics.player;

import org.bukkit.OfflinePlayer;

import java.util.Map;

public interface PlayerStatisticLoader {

	Map<Enum<?>, Integer> getPlayerStatistics(OfflinePlayer offlinePlayer);

}
