package de.sldk.mc.metrics.player;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fetches player stats stored in
 * <code>data/world/stats/&lt;UUID&gt;.json</code> files.
 */
public class PlayerStatisticLoaderFromFile implements PlayerStatisticLoader {

    private final Plugin plugin;
    private final Logger logger;

    private final Map<UUID, Map<Enum<?>, Integer>> fileData;

    private static final Map<String, Enum<?>> mapStatNameToStat = Arrays
            .stream(PlayerStatisticLoaderFromBukkit.STATISTICS)
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e));

    public PlayerStatisticLoaderFromFile(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        fileData = readPlayerStatsFiles();
    }

    @Override
    public Map<Enum<?>, Integer> getPlayerStatistics(OfflinePlayer offlinePlayer) {

        final UUID uuid = offlinePlayer.getUniqueId();

        if (fileData.containsKey(uuid)) {
            return fileData.get(uuid);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Reads all valid files in the <code>stats</code> folder and maps them to a
     * player.
     */
    private Map<UUID, Map<Enum<?>, Integer>> readPlayerStatsFiles() {
        try {
            File minecraftDataFolder = plugin.getServer().getWorldContainer().getCanonicalFile();

            Path statsFolder = Paths.get(minecraftDataFolder.getAbsolutePath(), "world", "stats");
            if (!Files.exists(statsFolder)) {
                return new HashMap<>();
            }

            logger.fine("Reading player stats from folder " + statsFolder.toString());

            try (Stream<Path> statFiles = Files.walk(statsFolder)) {
                return statFiles.filter(Files::isRegularFile)
                        .filter(this::isFileNameUuid)
                        .peek(path -> logger.fine("Found player stats file: " + path.getFileName().toString()))
                        .collect(Collectors.toMap(this::fileNameToUuid, path -> {
                            try {
                                return getPlayersStats(path);
                            } catch (Exception e) {
                                String msg = String.format("Could not read player stats from JSON at '%s'", path);
                                logger.log(Level.FINE, msg, e);
                                return new HashMap<>();
                            }
                        }));
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "Failed to read player stats from file. ", e);
            return new HashMap<>();
        }
    }

    /**
     * Given a player's stats json file, map each stat to a value.
     */
    private Map<Enum<?>, Integer> getPlayersStats(Path path) throws IOException {
        DocumentContext ctx = JsonPath.parse(path.toFile());
        Map<String, Object> fileStats = ctx.read(JsonPath.compile("$.stats.minecraft:custom"));
        return fileStats.keySet().stream().filter(mapStatNameToStat::containsKey)
                .filter(e -> fileStats.get(e) instanceof Integer)
                .collect(Collectors.toMap(mapStatNameToStat::get, e -> (Integer) fileStats.get(e)));
    }

    private boolean isFileNameUuid(Path path) {
        try {
            fileNameToUuid(path);
        } catch (Exception e) {
            String msg = String.format("Could not extract valid player UUID from player stats file '%s'", path);
            logger.log(Level.FINE, msg, e);
            return false;
        }
        return true;
    }

    private UUID fileNameToUuid(Path path) {
        String uuidPart = path.getFileName().toString().split("\\.")[0];
        return UUID.fromString(uuidPart);
    }
}
