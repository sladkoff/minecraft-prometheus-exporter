package de.sldk.mc;

import org.bukkit.configuration.file.FileConfiguration;

class PluginConfig<T> {

    static final PluginConfig<String> HOST = new PluginConfig<>("host", "localhost");
    static final PluginConfig<Integer> PORT = new PluginConfig<>("port", 9225);
    static final PluginConfig<Boolean> PLAYER_METRICS = new PluginConfig<>("individual-player-statistics", false);

    private final String key;
    private final T defaultValue;

    private PluginConfig(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    void setDefault(FileConfiguration config) {
        config.addDefault(this.getKey(), this.defaultValue);
    }

    String getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    T get(FileConfiguration config) {
        return (T) config.get(this.getKey());
    }
}
