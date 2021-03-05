package de.sldk.mc.metrics.player;

import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.Map;

public class EmptyStatisticLoader implements PlayerStatisticLoader {

	@Override
	public Map<Enum<?>, Integer> getPlayerStatistics(OfflinePlayer offlinePlayer) {
		return Collections.emptyMap();
	}
}
