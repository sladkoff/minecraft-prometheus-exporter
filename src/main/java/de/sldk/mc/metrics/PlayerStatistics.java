package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerStatistics extends PlayerMetric {

    private static final Gauge PLAYER_STATS = Gauge.build()
            .name(prefix("player_statistic"))
            .help("Player statistics")
            .labelNames("player_name", "player_uid", "statistic")
            .create();

    public PlayerStatistics(Plugin plugin) {
        super(plugin, PLAYER_STATS);
    }

    @Override
    public void collect(OfflinePlayer player) {

        Map<String, Integer> statistics = getStatistics(player.getPlayer());
        final String playerNameLabel = getNameOrUid(player);
        final String playerUidLabel = getUid(player);

        statistics.forEach((stat, value) -> PLAYER_STATS.labels(playerNameLabel, playerUidLabel, stat).set(value));
    }

    private static Map<String, Integer> getStatistics(Player player) {

        if (player == null) {
            return Collections.emptyMap();
        }

        EntityType[] entityTypes = EntityType.values();
        Material[] materials = Material.values();

        Statistic[] statistics = Statistic.values();

        return Arrays.stream(statistics).collect(Collectors.toMap(Enum::name, statistic -> {

            if (Statistic.Type.UNTYPED == statistic.getType()) {
                return player.getStatistic(statistic);
            } else if (Statistic.Type.ENTITY == statistic.getType()) {
                return Arrays.stream(entityTypes).map(type -> getSafeStatistic(player, statistic, type))
                        .filter(Objects::nonNull)
                        .reduce(0,  Integer::sum);
            } else if (Statistic.Type.ITEM == statistic.getType()
                    || Statistic.Type.BLOCK == statistic.getType()) {
                return Arrays.stream(materials).map(material -> getSafeStatistic(player, statistic, material))
                        .filter(Objects::nonNull)
                        .reduce(0,  Integer::sum);
            }

            return 0;
        }));
    }

    private static Integer getSafeStatistic(Player player, Statistic statistic, Material material) {
        try {
            return player.getStatistic(statistic, material);
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer getSafeStatistic(Player player, Statistic statistic, EntityType type) {
        try {
            return player.getStatistic(statistic, type);
        } catch (Exception e) {
            return null;
        }
    }
}
