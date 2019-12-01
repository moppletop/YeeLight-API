package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.model.YeeDuration;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.util.JacksonJSONProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class TestApp {

    YeeApiImpl api = (YeeApiImpl) new YeeApiBuilder()
            .jsonProvider(JacksonJSONProvider.INSTANCE)
            .autoDiscovery(false)
            .build();

    @Test
    @SneakyThrows
    void test() {
     //   api.discoverLights();

//        await().atMost(Duration.FIVE_SECONDS)
//                .until(() -> !api.getLights().isEmpty());

        api.getManager().registerLight(YeeLight.builder()
                .id(1000)
                .location("yeelight://192.168.0.15:55443")
                .build());

        api.getLights().forEach(yeeLight -> {
            System.out.println(yeeLight);
            api.setPower(yeeLight.getId(), true, YeeDuration.instant());
        });

        Thread.sleep(100000);
    }


}
