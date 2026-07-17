package de.sldk.mc.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FoliaUtilsTest {

    @Test
    void isFolia_should_return_false_when_not_on_folia() {
        assertThat(FoliaUtils.isFolia()).isFalse();
    }
}
