package de.sldk.mc.collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.HashMap;
import java.util.Map;

public class LoadedChunksCollector implements Listener {

    private final Map<String, Integer> loadedChunksTotalMap = new HashMap<>();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        Integer currentCount = loadedChunksTotalMap.get(world.getName());
        if (currentCount == null) {
            loadedChunksTotalMap.put(world.getName(), world.getLoadedChunks().length);
        }
        else {
            loadedChunksTotalMap.put(world.getName(), currentCount + 1);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        World world = event.getWorld();
        Integer currentCount = loadedChunksTotalMap.get(world.getName());
        if (currentCount == null) {
            loadedChunksTotalMap.put(world.getName(), world.getLoadedChunks().length);
        }
        else {
            loadedChunksTotalMap.put(world.getName(), currentCount - 1);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        loadedChunksTotalMap.remove(world.getName());
    }

    public int getLoadedChunkTotal(String worldName) {
        return loadedChunksTotalMap.computeIfAbsent(worldName, k -> {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return 0;
            }
            return world.getLoadedChunks().length;
        });
    }
}
