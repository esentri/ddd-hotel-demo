package com.esentri.hotel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Feste Uhr fuer alle Spring-Tests: "heute" ist der Anreisetag der Testdaten.
 */
@Configuration
public class TestClockConfiguration {

    public static final ZonedDateTime HEUTE = ZonedDateTime.of(2026, 11, 2, 10, 0, 0, 0, ZoneId.of("Europe/Berlin"));

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(HEUTE.toInstant(), HEUTE.getZone());
    }
}
