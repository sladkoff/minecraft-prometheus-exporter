package de.sldk.mc;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MetricsController extends AbstractHandler {

    private final PrometheusExporter exporter;

    private Gauge players = Gauge.build().name("mc_players_total").help("Online and offline players").labelNames("state").create().register();
    private Gauge loadedChunks = Gauge.build().name("mc_loaded_chunks_total").help("Chunks loaded per world").labelNames("world").create().register();
    private Gauge playersOnline = Gauge.build().name("mc_players_online_total").help("Players currently online per world").labelNames("world").create().register();
    private Gauge entities = Gauge.build().name("mc_entities_total").help("Entities loaded per world").labelNames("world").create().register();
    private Gauge livingEntities = Gauge.build().name("mc_living_entities_total").help("Living entities loaded per world").labelNames("world").create().register();
    private Gauge memory = Gauge.build().name("mc_jvm_memory").help("JVM memory usage").labelNames("type").create().register();

    public MetricsController(PrometheusExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!target.equals("/metrics")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /*
        * Bukkit API calls have to be made from the main thread.
        * That's why we use the BukkitScheduler to retrieve the server stats.
        * */
        Future<Object> future = exporter.getServer().getScheduler().callSyncMethod(exporter, new Callable<Object>() {
            public Object call() throws Exception {
                players.clear();
                players.labels("online").set(Bukkit.getOnlinePlayers().size());
                players.labels("offline").set(Bukkit.getOfflinePlayers().length);

                for (World world : Bukkit.getWorlds()) {
                    loadedChunks.labels(world.getName()).set(world.getLoadedChunks().length);
                    playersOnline.labels(world.getName()).set(world.getPlayers().size());
                    entities.labels(world.getName()).set(world.getEntities().size());
                    livingEntities.labels(world.getName()).set(world.getLivingEntities().size());
                }

                memory.labels("max").set(Runtime.getRuntime().maxMemory());
                memory.labels("free").set(Runtime.getRuntime().freeMemory());

                return null;
            }
        });

        try {
            future.get();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(TextFormat.CONTENT_TYPE_004);

            TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

            baseRequest.setHandled(true);
        } catch (InterruptedException | ExecutionException e) {
            exporter.getLogger().warning("Failed to read server statistics");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
