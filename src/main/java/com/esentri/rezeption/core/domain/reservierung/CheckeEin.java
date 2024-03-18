package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import nitrox.dlc.domain.types.DomainCommand;

/**
 * Record-Klasse für den Check-In-Vorgang in einer Reservierung.
 * Enthält die ReservierungsNummer, die ZimmerNummer und die geplante Anzahl an Nächten.
 * Implementiert das DomainCommand Interface.
 * @author Mario Herb
 */
public record CheckeEin(
        Reservierung.ReservierungsNummer reservierungsNummer,
        Zimmer.ZimmerNummer zimmerNummer,

        int geplanteAnzahlNaechte
        ) implements DomainCommand {
}
