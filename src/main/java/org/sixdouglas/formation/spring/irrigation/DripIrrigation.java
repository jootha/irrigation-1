package org.sixdouglas.formation.spring.irrigation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@Component
public class DripIrrigation {
    private static Logger LOGGER = LoggerFactory.getLogger(DripIrrigation.class);

    public Flux<Drop> followDrops() {
        //TODO Create a Flux that would emit a Drop every 20 millis seconds
                return Flux.interval(Duration.ofMillis(20))
                        .map(aLong -> Drop.builder()
                                .greenHouseId(1)
                                .rowId(1)
                                .dropperId(1)
                                .instant(Instant.now())
                                .build());

    }
}
