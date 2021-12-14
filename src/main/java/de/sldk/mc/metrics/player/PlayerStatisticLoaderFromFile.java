package de.sldk.mc.metrics.player;

import com.google.common.base.Suppliers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fetches player stats stored in
 * <code>data/<default-world>/stats/&lt;UUID&gt;.json</code> files.
 */
public class PlayerStatisticLoaderFromFile implements PlayerStatisticLoader {
    public final String SERVER_PROPERTIES = "server.properties";
    public final String DEFAULT_WORLD = "world";

    private final Plugin plugin;
    private final Logger logger;
    private final Supplier<Map<UUID, Map<Enum<?>, Integer>>> statsFileLoader =
            Suppliers.memoize(this::readPlayerStatsFiles);

    private static final Map<String, Enum<?>> mapStatNameToStat = Arrays
            .stream(PlayerStatisticLoaderFromBukkit.STATISTICS)
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e));

    public PlayerStatisticLoaderFromFile(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public Map<Enum<?>, Integer> getPlayerStatistics(OfflinePlayer offlinePlayer) {

        final UUID uuid = offlinePlayer.getUniqueId();

        final Map<UUID, Map<Enum<?>, Integer>> fileData = statsFileLoader.get();

        return fileData.getOrDefault(uuid, new HashMap<>());
    }

    /**
     * Reads all valid files in the <code>stats</code> folder and maps them to a
     * player.
     */
    private Map<UUID, Map<Enum<?>, Integer>> readPlayerStatsFiles() {
        try {
            File minecraftDataFolder = plugin.getServer().getWorldContainer().getCanonicalFile();

            Path statsFolder =
                    Paths.get(minecraftDataFolder.getAbsolutePath(), getDefaultWorld(SERVER_PROPERTIES), "stats");
            if (!Files.exists(statsFolder)) {
                return new HashMap<>();
            }

            logger.fine("Reading player stats from folder " + statsFolder);

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

    public String getDefaultWorld(String fileName) {
        try (BufferedReader read = new BufferedReader(new FileReader(fileName))) {
            String line;
            String prefix = "level-name=";
            while ((line = read.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    return line.replace(prefix, "");
                }
            }
        } catch (IOException e) {
            logger.log(Level.FINE, "Failed to read level name from server properties file. ", e);
        }

        return DEFAULT_WORLD;
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
