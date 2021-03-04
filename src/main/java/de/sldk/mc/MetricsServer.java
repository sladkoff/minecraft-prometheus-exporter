package de.sldk.mc;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import java.net.InetSocketAddress;

public class MetricsServer {

	private final String host;
	private final int port;
	private final PrometheusExporter prometheusExporter;

	private Server server;

	public MetricsServer(String host, int port, PrometheusExporter prometheusExporter) {
		this.host = host;
		this.port = port;
		this.prometheusExporter = prometheusExporter;
	}

	public void start() throws Exception {
		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(new MetricsController(prometheusExporter));

		InetSocketAddress address = new InetSocketAddress(host, port);
		server = new Server(address);
		server.setHandler(gzipHandler);

		server.start();
	}

	public void stop() throws Exception {
		if (server == null) {
			return;
		}

		server.stop();
	}
}
