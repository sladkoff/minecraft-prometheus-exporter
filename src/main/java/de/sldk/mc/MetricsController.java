package de.sldk.mc;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsController extends AbstractHandler {

    private Gauge playersOnline = Gauge.build().name("mc_players_online").help("Players online now").create().register();
    private Gauge worldChunks = Gauge.build().name("mc_loaded_chunks").help("Loaded chunks per world").labelNames("world").create().register();
    private Gauge worldPlayers = Gauge.build().name("mc_world_players").help("Players per world").labelNames("world").create().register();
    private Gauge worldEntities = Gauge.build().name("mc_world_entities").help("Entities per world").labelNames("world").create().register();
    private Gauge worldLivingEntities = Gauge.build().name("mc_world_living_entities").help("Living entities per world").labelNames("world").create().register();

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        playersOnline.clear();
        playersOnline.labels().set(Bukkit.getOnlinePlayers().size());

        Server server = Bukkit.getServer();
        for (World world : server.getWorlds()) {
            worldChunks.labels(world.getName()).set(world.getLoadedChunks().length);
            worldPlayers.labels(world.getName()).set(world.getPlayers().size());
            worldEntities.labels(world.getName()).set(world.getEntities().size());
            worldLivingEntities.labels(world.getName()).set(world.getLivingEntities().size());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(TextFormat.CONTENT_TYPE_004);

        TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

        baseRequest.setHandled(true);
    }
}
