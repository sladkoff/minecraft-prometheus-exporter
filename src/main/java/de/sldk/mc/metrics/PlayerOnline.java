package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOnline extends PlayerMetric {

    private static final Gauge PLAYERS_WITH_NAMES = Gauge.build()
            .name(prefix("player_online"))
            .help("Online state by player name")
            .labelNames("name", "uid")
            .create();

    // cache for player names to avoid expensive NBT reads for offline players
    private final Map<UUID, String> nameCache = new ConcurrentHashMap<>();

    public PlayerOnline(Plugin plugin) {
        super(plugin, PLAYERS_WITH_NAMES);
        // Asynchronously cache player names on startup to minimize NBT reads during metric collection
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            for (OfflinePlayer p : plugin.getServer().getOfflinePlayers()) {
                try {
                    String name = p.getName();
                    if (name != null && !name.isEmpty()) {
                        nameCache.put(p.getUniqueId(), name);
                    }
                } catch (Exception ignored) {
                    // ignore any exceptions when trying to read player names, just skip caching for those players
                }
            }
            plugin.getLogger().info("[PlayerOnline] Cached " + nameCache.size() + " player names");
        });
    }

    @Override
    public void collect(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        String name;
        boolean isOnline = player.isOnline();
        
        try {
            // Online players: get directly (don't read NBT)
            if (isOnline) {
                name = player.getName();
                if (name == null || name.isEmpty()) {
                    name = uuid.toString();
                }
                // cache the name for future offline lookups
                nameCache.put(uuid, name);
            } 
            // Offline players: try cache first, then safe retrieval if not cached (but avoid heavy NBT reads)
            else {
                name = nameCache.get(uuid);
                if (name == null || name.isEmpty()) {
                    // try to read the name safely without triggering heavy NBT reads for offline players
                    try {
                        // hasPlayedBefore() is a safe check that doesn't trigger NBT reads, it just checks if the player has ever joined before
                        if (player.hasPlayedBefore()) {
                            String tempName = player.getName();
                            if (tempName != null && !tempName.isEmpty()) {
                                name = tempName;
                                nameCache.put(uuid, name);
                            } else {
                                name = uuid.toString();
                            }
                        } else {
                            name = uuid.toString();
                        }
                    } catch (Exception e) {
                        name = uuid.toString();
                    }
                }
            }
        } catch (Exception e) {
            name = uuid.toString();
        }
        
        // ensure name is not null or empty
        if (name == null || name.isEmpty()) {
            name = uuid.toString();
        }
        
        PLAYERS_WITH_NAMES.labels(name, uuid.toString()).set(isOnline ? 1 : 0);
    }
}