package com.esentri.rezeption.core.domain.reservierung;

import nitrox.dlc.domain.types.DomainCommand;

/**
 * Diese Klasse repräsentiert das Domain Command "StorniereReservierung", welches eine Aktion zur Stornierung einer Hotelreservierung darstellt.
 *
 * @param reservierungsNummer die Identität der zu stornierenden Reservierung.
 *
 * @author Mario Herb
 */
public record StorniereReservierung(Reservierung.ReservierungsNummer reservierungsNummer) implements DomainCommand {
}