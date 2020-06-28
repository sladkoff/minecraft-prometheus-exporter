package de.sldk.mc.metrics;

import io.prometheus.client.Collector;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public abstract class PlayerMetric extends Metric {

  public PlayerMetric(Plugin plugin, Collector collector) {
    super(plugin, collector);
  }

  @Override
  public final void doCollect() {

    getPlugin().getLogger().info(
        "Offline Players: " + Arrays.toString(Bukkit.getOfflinePlayers())
    );

    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
      getPlugin().getLogger().info("Getting stats for player: " + player.getName());
      collect(player);
    }
  }

  protected abstract void collect(OfflinePlayer player);

  protected String getUid(OfflinePlayer player) {
    return player.getUniqueId().toString();
  }

  protected String getNameOrUid(OfflinePlayer player) {
    return player.getName() != null ? player.getName() : player.getUniqueId().toString();
  }

}
