package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import nitrox.dlc.domain.types.DomainEvent;

import java.time.LocalDate;


/**
 * Ein DomainEvent, das darstellt, dass ein Gast in eine Reservierung eingecheckt wurde.
 * Bei dem Event werden die ReservierungsNummer, die ZimmerNummer und der Buchungszeitraum festgehalten.
 *
 * @param reservierungsNummer Die Identifikationsnummer der Reservierung
 * @param zimmerNummer Die Identifikationsnummer des Zimmers
 * @param von Das Startdatum der Reservierung
 * @param bis Das Enddatum der Reservierung
 *
 * @author Mario Herb
 */
public record ReservierungEingecheckt(Reservierung.ReservierungsNummer reservierungsNummer,
                                      Zimmer.ZimmerNummer zimmerNummer,
                                      LocalDate von,
                                      LocalDate bis
) implements DomainEvent {}