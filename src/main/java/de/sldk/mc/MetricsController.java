package de.sldk.mc;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsController extends AbstractHandler {

    private Gauge playersOnline = Gauge.build().name("players_online").help("Players online now").create().register();

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        playersOnline.clear();
        playersOnline.set(Bukkit.getOnlinePlayers().size());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(TextFormat.CONTENT_TYPE_004);

        TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

        baseRequest.setHandled(true);
    }
}
