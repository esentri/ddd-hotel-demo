package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;

import java.time.LocalDate;
import java.util.UUID;

public final class BuchungTestdaten {

    public static final HotelId HOTEL = new HotelId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    public static final HotelId ANDERES_HOTEL = new HotelId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
    public static final LocalDate ANREISE = LocalDate.of(2026, 11, 2);
    public static final Aufenthaltszeitraum ZEITRAUM = new Aufenthaltszeitraum(ANREISE, ANREISE.plusDays(3));

    private BuchungTestdaten() {
    }

    public static UebernimmReservierung reservierung(String reservierungsnummer) {
        return new UebernimmReservierung(
            HOTEL,
            new Reservierungsnummer(reservierungsnummer),
            new Name("Erika", "Mustermann"),
            new Email("erika@example.com"),
            new Telefonnummer("+49 89 123456"),
            new Adresse("Hauptstrasse 1", "80331", "Muenchen", "Deutschland"),
            ZEITRAUM,
            Zimmerkategorie.DOPPELZIMMER);
    }

    public static UebernimmReservierung reservierungOhneKontaktdaten(String reservierungsnummer) {
        return new UebernimmReservierung(
            HOTEL,
            new Reservierungsnummer(reservierungsnummer),
            new Name("Max", "Mustermann"),
            null,
            null,
            null,
            ZEITRAUM,
            Zimmerkategorie.DOPPELZIMMER);
    }

    public static Gastdaten gastdaten() {
        return new Gastdaten(new Name("Max", "Mustermann"), new Email("max@example.com"), null,
            new Adresse("Ringstrasse 5", "10115", "Berlin", "Deutschland"));
    }
}
