package de.sldk.mc.metrics.player;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

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

            logger.info("Reading player stats from folder  " + statsFolder.toString());

            Stream<Path> statFiles = Files.walk(statsFolder);
            try {
                return statFiles.filter(Files::isRegularFile).filter(this::isFileNameUuid)
                        .peek(e -> logger.info("Found player stats file: " + e.getFileName().toString()))
                        .collect(Collectors.toMap(this::fileNameToUuid, path -> {
                            try {
                                return getPlayersStats(path);
                            } catch (IOException e) {
                                return new HashMap<>();
                            }
                        }));
            } finally {
                statFiles.close();
            }
        } catch (IOException e) {
            logger.info("Error - abandoning file reading");
            e.printStackTrace();
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
            UUID uuid = fileNameToUuid(path);
            return uuid.hashCode() != 0;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Invalid UUID string")) {
                logger.info("File '" + path.getFileName().toString()
                        + "' found in stats folder, but UUID was invalid - ignoring");
                return false;
            } else {
                throw e;
            }
        }
    }

    private UUID fileNameToUuid(Path path) {
        String x = path.getFileName().toString().split("\\.")[0];
        return UUID.fromString(x);
    }
}
