package de.sldk.mc.exporter;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.sldk.mc.MetricsServer;
import de.sldk.mc.PrometheusExporter;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.common.TextFormat;
import io.restassured.RestAssured;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.URIUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class PrometheusExporterTest {

	@Mock
	private PrometheusExporter exporterMock;
	@Mock
	private Server mockServer;
	@Mock
	private BukkitScheduler mockScheduler;

	private int metricsServerPort;
	private MetricsServer metricsServer;

	@BeforeEach
	void setup() throws Exception {
		CollectorRegistry.defaultRegistry.clear();
		metricsServerPort = getRandomFreePort();
		metricsServer = new MetricsServer("localhost", metricsServerPort, exporterMock);
		metricsServer.start();
	}

	private int getRandomFreePort() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			return serverSocket.getLocalPort();
		}
	}

	@AfterEach
	void cleanup() throws Exception {
		metricsServer.stop();
	}

	@Test
	void metrics_server_should_return_valid_prometheus_response() {
		mockBukkitApis();
		mockPrometheusCounter("mc_mock_metric", "This is a mock metric", 419);

		String requestPath = URIUtil.newURI("http", "localhost", metricsServerPort, "/metrics", null);
		String responseText = RestAssured.when()
				.get(requestPath)
				.then()
				.statusCode(HttpStatus.OK_200)
				.contentType(TextFormat.CONTENT_TYPE_004)
				.extract()
				.asString();

		String[] lines = responseText.split("\n");
		assertThat(lines[0]).isEqualTo("# HELP mc_mock_metric_total This is a mock metric");
		assertThat(lines[1]).isEqualTo("# TYPE mc_mock_metric_total counter");
		assertThat(lines[2]).isEqualTo("mc_mock_metric_total 419.0");
	}

	private void mockBukkitApis() {
		when(exporterMock.getServer()).thenReturn(mockServer);
		when(mockServer.getScheduler()).thenReturn(mockScheduler);
		when(mockScheduler.callSyncMethod(any(), any())).thenReturn(CompletableFuture.completedFuture(null));
	}

	private void mockPrometheusCounter(String name, String help, int value) {
		Counter mockPrometheusCounter = Counter.build().name(name).help(help).register();
		mockPrometheusCounter.inc(value);
	}

	@Test
	void metrics_server_should_return_404_on_unknown_paths() {
		String requestPath = URIUtil.newURI("http", "localhost", metricsServerPort, "/unknown-path", null);

		RestAssured.when()
				.get(requestPath)
				.then()
				.statusCode(HttpStatus.NOT_FOUND_404);
	}

}
