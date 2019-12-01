package com.moppletop.yeelight.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuilderTesting {

    @Test
    void noJsonProvider() {
        assertThatThrownBy(() -> new YeeApiBuilder().build()).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}
