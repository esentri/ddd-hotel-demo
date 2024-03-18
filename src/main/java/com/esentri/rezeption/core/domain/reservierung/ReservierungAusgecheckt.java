package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import nitrox.dlc.domain.types.DomainEvent;

import java.time.LocalDateTime;

/**
 * Diese Klasse modelliert das Auscheck-Event einer Hotelreservierung.
 * Das Event wird ausgelöst, wenn ein Gast aus seinem reservierten Zimmer auscheckt.
 * Die Klasse enthält die Reservierungsnummer der betroffenen Reservierung, das Datum und die Uhrzeit des Auscheckens und die Zimmernummer des betroffenen Zimmers.
 *
 * @author Mario Herb
 */
public record ReservierungAusgecheckt(Reservierung.ReservierungsNummer reservierungsNummer,
                                      LocalDateTime am,
                                      Zimmer.ZimmerNummer zimmerNummer) implements DomainEvent {
}
