package org.sixdouglas.formation.spring.irrigation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DripIrrigationTest {

    private DripIrrigation dripIrrigation;
    private Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    @BeforeEach
    public void setup() {
        dripIrrigation = new DripIrrigation();
    }

    @Test
    void testFollowDrops() {

        Flux<Drop> dropFlux = dripIrrigation.followDrops()
                .limitRequest(5)
                .timeout(Duration.ofMillis(500));

        StepVerifier.create(dropFlux).assertNext(drop -> {
            assertEquals(1, drop.getGreenHouseId(), "Greenhouse ID should be 1");
            assertEquals(1, drop.getRowId(), "Row ID should be 1");
            assertEquals(1, drop.getDropperId(), "Dropper ID should be 1");
            assertTrue(pattern.matcher(drop.getUuid()).matches(), "Drop UUID should looks like '01234567-9ABC-DEF0-1234-56789ABCDEF0'");
            assertNotNull(drop.getInstant(), "Instant should not be null");
            Instant nowInstant = Instant.now();
            assertTrue(nowInstant.isAfter(drop.getInstant()), "Instant should be before now");
            Instant truncatedNowInstant = nowInstant.truncatedTo(ChronoUnit.MILLIS);
            assertTrue(truncatedNowInstant.toEpochMilli() - drop.getInstant().truncatedTo(ChronoUnit.MILLIS).toEpochMilli() <= 300, "Instant [" + drop.getInstant().truncatedTo(ChronoUnit.MILLIS) + "] should be less than 300 milli-seconds appart from now [" + truncatedNowInstant + "]");
        })
                .expectNextCount(4)
                .expectComplete()
                .verify();

    }

    @Test
    void followDropper() {

        Flux<Drop> dropFlux = dripIrrigation.followDropper(2, 1, 6)
                .limitRequest(8)
                .timeout(Duration.ofMillis(500));

        StepVerifier.create(dropFlux).assertNext(drop -> {
            assertEquals(2, drop.getGreenHouseId(), "Greenhouse ID should be 2");
            assertEquals(1, drop.getRowId(), "Row ID should be 1");
            assertEquals(6, drop.getDropperId(), "Dropper ID should be 6");
            assertTrue(pattern.matcher(drop.getUuid()).matches(), "Drop UUID should looks like '01234567-9ABC-DEF0-1234-56789ABCDEF0'");
            assertNotNull(drop.getInstant(), "Instant should not be null");
            Instant nowInstant = Instant.now();
            assertTrue(nowInstant.isAfter(drop.getInstant()), "Instant should be before now");
            Instant truncatedNowInstant = nowInstant.truncatedTo(ChronoUnit.MILLIS);
            assertTrue(truncatedNowInstant.toEpochMilli() - drop.getInstant().truncatedTo(ChronoUnit.MILLIS).toEpochMilli() <= 300, "Instant [" + drop.getInstant().truncatedTo(ChronoUnit.MILLIS) + "] should be less than 300 milli-seconds appart from now [" + truncatedNowInstant + "]");
        })
                .expectNextCount(7)
                .expectComplete()
                .verify();
    }

    @Test
    void followDetailedDropper() {
        Flux<DetailedDrop> dropFlux = dripIrrigation.followDetailedDropper(2, 1, 6)
                .limitRequest(3)
                .timeout(Duration.ofMillis(200));

        StepVerifier.create(dropFlux)
                .assertNext(detailedDrop -> {
                    assertTrue(pattern.matcher(detailedDrop.getUuid()).matches(), "Drop UUID should looks like '01234567-9ABC-DEF0-1234-56789ABCDEF0'");
                    assertNotNull(detailedDrop.getInstant(), "Instant should not be null");
                    Instant nowInstant = Instant.now();
                    assertTrue(nowInstant.isAfter(detailedDrop.getInstant()), "Instant should be before now");
                    Instant truncatedNowInstant = nowInstant.truncatedTo(ChronoUnit.MILLIS);
                    assertTrue(truncatedNowInstant.toEpochMilli() - detailedDrop.getInstant().truncatedTo(ChronoUnit.MILLIS).toEpochMilli() <= 300, "Instant [" + detailedDrop.getInstant().truncatedTo(ChronoUnit.MILLIS) + "] should be less than 300 milli-seconds appart from now [" + truncatedNowInstant + "]");

                    assertNotNull(detailedDrop.getGreenHouse());
                    assertEquals(detailedDrop.getGreenHouse().getName(), "Bamboos");
                    assertNotNull(detailedDrop.getGreenHouse().getRows());
                    assertEquals(detailedDrop.getGreenHouse().getRows().size(), 1);
                    assertEquals(detailedDrop.getGreenHouse().getRows().get(0).getName(), "A");
                    assertNotNull(detailedDrop.getGreenHouse().getRows().get(0).getDroppers());
                    assertEquals(detailedDrop.getGreenHouse().getRows().get(0).getDroppers().size(), 1);
                    assertEquals(detailedDrop.getGreenHouse().getRows().get(0).getDroppers().get(0).getName(), "B-A-6");
                })
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
