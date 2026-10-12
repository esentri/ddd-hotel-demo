package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.Reservierungsnummer;

public class ReservierungBereitsUebernommenException extends RuntimeException {

    public ReservierungBereitsUebernommenException(Reservierungsnummer reservierungsnummer) {
        super("Die Reservierung '%s' wurde bereits uebernommen.".formatted(reservierungsnummer.value()));
    }
}
