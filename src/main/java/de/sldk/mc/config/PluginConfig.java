package de.sldk.mc.config;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig<T> {

    protected final String key;
    protected final T defaultValue;

    protected PluginConfig(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public void setDefault(FileConfiguration config) {
        config.addDefault(this.key, this.defaultValue);
    }

    @SuppressWarnings("unchecked")
    public T get(FileConfiguration config) {
        return (T) config.get(this.key);
    }
}
