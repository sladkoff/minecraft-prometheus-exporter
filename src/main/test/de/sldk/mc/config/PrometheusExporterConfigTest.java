package de.sldk.mc.config;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrometheusExporterConfigTest {

    @Test
    void test() {
        Assertions.assertEquals(8, PrometheusExporterConfig.METRICS.size());
    }

}