package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;

public class ZimmerBereitsVorhandenException extends RuntimeException {

    public ZimmerBereitsVorhandenException(Zimmernummer zimmernummer) {
        super("Das Hotel hat bereits ein Zimmer mit der Nummer %s.".formatted(zimmernummer.value()));
    }
}
