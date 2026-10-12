package com.esentri.hotel.rezeption.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class RezeptionConfiguration {

    /**
     * Liefert "heute" fuer die Pruefung des Anreisetags. Vorerst gilt die Zeitzone des Servers.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
