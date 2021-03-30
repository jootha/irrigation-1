package org.sixdouglas.formation.spring.irrigation;

import org.sixdouglas.formation.spring.irrigation.producer.GreenHouseProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class DripIrrigation {
    private static Logger LOGGER = LoggerFactory.getLogger(DripIrrigation.class);

    public Flux<Drop> followDrops() {
        // Create a Flux that would emit a Drop every 20 millis seconds
        return Flux.interval(Duration.ofMillis(20))
                .map(aLong -> Drop.builder()
                        .greenHouseId(1)
                        .rowId(1)
                        .dropperId(1)
                        .instant(Instant.now())
                        .build());
    }

    public Flux<Drop> followDropper(int greenHouseId, int rowId, int dropperId) {
        // use the GreenHouseProducer.getDrops() function as producer, but filter the output to fit the given criteria
        return GreenHouseProducer.getDrops()
                .onErrorContinue((throwable, object) -> LOGGER.error("Error : {}, {}", object.toString(), throwable.getMessage()))
                .filter(drop -> drop.getGreenHouseId() == greenHouseId && drop.getRowId() == rowId && drop.getDropperId() == dropperId)
                .limitRequest(8)
                .timeout(Duration.ofMillis(500))
                .log();
    }

    public Flux<DetailedDrop> followDetailedDropper(int greenHouseId, int rowId, int dropperId) {
        //TODO use the GreenHouseProducer.getDrops() function as producer, but filter the output to fit the given criteria
        //TODO    then map it to a DetailedDrop using the getDetailedDrop() function

        return GreenHouseProducer.getDrops()
                .onErrorContinue((throwable, object) -> LOGGER.error("Error : {}, {}", object.toString(), throwable.getMessage()))
                .filter(drop -> drop.getGreenHouseId() == greenHouseId && drop.getRowId() == rowId && drop.getDropperId() == dropperId)
                .limitRequest(8)
                .timeout(Duration.ofMillis(500))
                .log()
                .flatMap(this::getDetailedDrop);
    }

    private Mono<DetailedDrop> getDetailedDrop(Drop drop) {
        //TODO use the GreenHouseProducer.getDropper() function to find the Dropper information wrap in a Greenhouse
        //TODO    then map it to build a DetailedDrop
        return null;
    }
}
