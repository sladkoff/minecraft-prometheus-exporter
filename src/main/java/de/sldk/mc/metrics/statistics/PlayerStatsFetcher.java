package de.sldk.mc.metrics.statistics;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Fetches player stats from the Minecraft API
 */
public class PlayerStatsFetcher {

    private final Logger logger;

    /**
     * All statistics we'll be querying
     */
    public static final Statistic[] STATISTICS = Statistic.values();
    private static final EntityType[] ENTITY_TYPES = EntityType.values();
    private static final Material[] MATERIALS = Arrays
            .stream(Material.values())
            .filter(PlayerStatsFetcher::isMaterialLegacy)
            .toArray(Material[]::new);

    public PlayerStatsFetcher(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * For an online player, map each stat to a value
     */
    public Map<Enum<?>, Integer> getPlayerStats(Player player) {
        return Arrays.stream(STATISTICS)
                .collect(Collectors.toMap(
                        e -> e,
                        statistic -> {
                            if (Statistic.Type.UNTYPED == statistic.getType()) {
                                return getUntypedStatistic(player, statistic);
                            } else {
                                return getStatTypeStream(statistic.getType())
                                        .map(type -> getTypedStatistic(player, statistic, type))
                                        .reduce(0, Integer::sum);
                            }
                        }));
    }

    private Integer getUntypedStatistic(Player player, Statistic statistic) {
        Integer stat;
        try {
            stat = player.getStatistic(statistic);
        } catch (IllegalArgumentException e) {
            logger.info("exception fetching statistic " + statistic + " from player");
            stat = null;
        }
        return stat != null ? stat : 0;
    }

    private Stream<Enum<?>> getStatTypeStream(Statistic.Type type) {
        if (type == Statistic.Type.ENTITY) {
            logger.info("Returning ENTITY stream");
            return Arrays.stream(ENTITY_TYPES);
        } else if (type == Type.ITEM || type == Type.BLOCK) {
            logger.info("Returning MATERIALS stream");
            return Arrays.stream(MATERIALS);
        } else {
            logger.info("Returning empty stream");
            return Stream.empty();
        }
    }

    private Integer getTypedStatistic(Player player, Statistic statistic, Enum<?> statType) {
        Integer stat = null;
        try {
            if (statType.getClass() == Material.class) {
                stat = player.getStatistic(statistic, (Material) statType);
            } else if (statType.getClass() == EntityType.class) {
                stat = player.getStatistic(statistic, (EntityType) statType);
            }
        } catch (IllegalArgumentException e) {
            logger.info("exception fetching statistic " + statistic + " from player");
            stat = null;
        }
        return stat != null ? stat : 0;
    }

    /**
     * Annoying method. Used to filter out legacy {@link Material}s.
     * <p>
     * I want to use {@link Material#isLegacy()}, but it's deprecated for no reason.
     */
    @SuppressWarnings("deprecation")
    public static boolean isMaterialLegacy(Material material) {
        try {
            return material.getKey().hashCode() != 0;
        } catch (
                IllegalArgumentException e) {
            return false;
        }
    }
}
