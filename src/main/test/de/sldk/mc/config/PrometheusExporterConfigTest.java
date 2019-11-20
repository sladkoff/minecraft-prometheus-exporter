package de.sldk.mc.config;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrometheusExporterConfigTest {

    @Test
    void test() {
        Assertions.assertEquals(9, PrometheusExporterConfig.METRICS.size());
    }

}