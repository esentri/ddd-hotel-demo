package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import io.domainlifecycles.domain.types.Factory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Erzeugt neue Buchungen: vergibt die Identitaeten von Buchung und Hauptgast, baut den Hauptgast aus den Gastdaten
 * auf und stellt sicher, dass eine neue Buchung nicht in der Vergangenheit beginnt.
 */
public class BuchungsFactory implements Factory {

    private final Clock clock;

    public BuchungsFactory() {
        this(Clock.systemDefaultZone());
    }

    BuchungsFactory(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock darf nicht null sein");
    }

    /**
     * Reserviert eine neue Buchung.
     *
     * @param vorname           Vorname des Hauptgastes
     * @param nachname          Nachname des Hauptgastes
     * @param geburtsdatum      Geburtsdatum des Hauptgastes, kann bis zum Check-in nachgereicht werden
     * @param belegungszeitraum der gebuchte Zeitraum, beginnt fruehestens heute
     * @return die neue Buchung im Status RESERVIERT
     */
    public Buchung reserviere(String vorname, String nachname, LocalDate geburtsdatum, Zeitraum belegungszeitraum) {
        Objects.requireNonNull(belegungszeitraum, "Der Belegungszeitraum darf nicht null sein");
        if (belegungszeitraum.start().isBefore(LocalDate.now(clock))) {
            throw new IllegalArgumentException("Eine Buchung kann nicht in der Vergangenheit beginnen");
        }
        HauptGast hauptGast = new HauptGast(new HauptGastId(UUID.randomUUID()), vorname, nachname, geburtsdatum, 0);
        return Buchung.neueBuchung(new BuchungsId(UUID.randomUUID()), hauptGast, belegungszeitraum);
    }
}
