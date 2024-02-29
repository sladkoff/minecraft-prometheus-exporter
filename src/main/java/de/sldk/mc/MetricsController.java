package de.sldk.mc;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class MetricsController extends Handler.Abstract {

    private final MetricRegistry metricRegistry = MetricRegistry.getInstance();
    private final PrometheusExporter exporter;

    public MetricsController(PrometheusExporter exporter) {
        this.exporter = exporter;
    }


    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        try {
            metricRegistry.collectMetrics().get();

            response.setStatus(HttpStatus.OK_200);

            HttpFields.Mutable responseHeaders = response.getHeaders();
            responseHeaders.put(HttpHeader.CONTENT_TYPE, TextFormat.CONTENT_TYPE_004);

            writeMetricsToResponse(request, response);
        } catch (Exception e) {
            exporter.getLogger().log(Level.WARNING, "Failed to read server statistic: " + e.getMessage());
            exporter.getLogger().log(Level.FINE, "Failed to read server statistic: ", e);
            Response.writeError(request, response, callback, HttpStatus.INTERNAL_SERVER_ERROR_500, "Failed to read server statistics");
        }
        callback.succeeded();
        return true;
    }

    private void writeMetricsToResponse(Request request, Response response) throws IOException {
        var out = Response.asBufferedOutputStream(request, response);
        try (var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
            writer.flush();
        }
    }
}
