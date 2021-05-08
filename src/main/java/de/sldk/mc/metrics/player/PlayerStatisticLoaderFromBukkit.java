package de.sldk.mc.metrics.player;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Fetches player stats from the Minecraft API
 */
public class PlayerStatisticLoaderFromBukkit implements PlayerStatisticLoader {

    public static final Statistic[] STATISTICS = Statistic.values();
    private static final EntityType[] ENTITY_TYPES = EntityType.values();
    private static final Material[] MATERIALS = Material.values();

    private final Logger logger;

    public PlayerStatisticLoaderFromBukkit(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * For an online player, map each stat to a value
     */
    @Override
    public Map<Enum<?>, Integer> getPlayerStatistics(OfflinePlayer offlinePlayer) {

        final Player player = offlinePlayer.getPlayer();

        if (player == null) {
            logger.fine(String.format(
                    "Can not load player statistics for '%s' from Bukkit API. The player has probably not been online" +
                            " since reboot.", offlinePlayer.getUniqueId()));
            return null;
        }

        return Arrays.stream(STATISTICS).collect(Collectors.toMap(e -> e, statistic -> {
            if (Statistic.Type.UNTYPED == statistic.getType()) {
                return getUntypedStatistic(player, statistic);
            } else {
                return getStatTypeStream(statistic.getType()).map(type -> getTypedStatistic(player, statistic, type))
                        .reduce(0, Integer::sum);
            }
        }));
    }

    private Integer getUntypedStatistic(Player player, Statistic statistic) {
        try {
            return player.getStatistic(statistic);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Exception fetching statistic " + statistic + " from player", e);
            return 0;
        }
    }

    private Stream<Enum<?>> getStatTypeStream(Statistic.Type type) {
        if (type == Statistic.Type.ENTITY) {
            return Arrays.stream(ENTITY_TYPES);
        } else if (type == Type.ITEM || type == Type.BLOCK) {
            return Arrays.stream(MATERIALS);
        } else {
            return Stream.empty();
        }
    }

    private Integer getTypedStatistic(Player player, Statistic statistic, Enum<?> statType) {
        try {
            if (statType.getClass() == Material.class) {
                return player.getStatistic(statistic, (Material) statType);
            } else if (statType.getClass() == EntityType.class) {
                return player.getStatistic(statistic, (EntityType) statType);
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception fetching statistic " + statistic + " from player", e);
            return 0;
        }
    }

}
