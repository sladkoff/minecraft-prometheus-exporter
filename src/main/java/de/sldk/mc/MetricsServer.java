package de.sldk.mc;

import de.sldk.mc.health.HealthChecks;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import java.net.InetSocketAddress;

public class MetricsServer {

	private final String host;
	private final int port;
	private final PrometheusExporter prometheusExporter;
	private final HealthChecks healthChecks;

	private Server server;

	public MetricsServer(String host, int port, PrometheusExporter prometheusExporter, HealthChecks healthChecks) {
		this.host = host;
		this.port = port;
		this.prometheusExporter = prometheusExporter;
		this.healthChecks = healthChecks;
	}

	public void start() throws Exception {
		GzipHandler metricsHandler = new GzipHandler();
		metricsHandler.setHandler(MetricsController.create(prometheusExporter));

		InetSocketAddress address = new InetSocketAddress(host, port);
		server = new Server(address);
		server.setHandler(new HandlerList(
				metricsHandler,
				HealthController.create(healthChecks)
		));

		server.start();
	}

	public void stop() throws Exception {
		if (server == null) {
			return;
		}

		server.stop();
	}
}
