package de.sldk.mc;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class MetricsController extends AbstractHandler {

    private final MetricRegistry metricRegistry = MetricRegistry.getInstance();
    private final PrometheusExporter exporter;

    public MetricsController(PrometheusExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws IOException {

        if (!target.equals("/metrics")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /*
         * Bukkit API calls have to be made from the main thread.
         * That's why we use the BukkitScheduler to retrieve the server stats.
         * */
        Future<Object> future = exporter.getServer().getScheduler().callSyncMethod(exporter, () -> {
            metricRegistry.collectMetrics();
            return null;
        });

        try {
            future.get();

            response.setStatus(HttpStatus.OK_200);
            response.setContentType(TextFormat.CONTENT_TYPE_004);

            TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

            request.setHandled(true);
        } catch (InterruptedException | ExecutionException e) {
            exporter.getLogger().log(Level.WARNING, "Failed to read server statistic: " + e.getMessage());
            exporter.getLogger().log(Level.FINE, "Failed to read server statistic: ", e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }
}
