package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.DomainCommand;

import java.util.List;


/**
 * Repräsentiert den Befehl zum Erstellen einer Rechnung für eine Reservierung.
 * Beinhaltet eine Reservierungsnummer, eine Liste von Service-Leistungen und eine Rechnungsadresse.
 *
 * @author Mario Herb
 * @see Reservierung.ReservierungsNummer
 * @see ServiceLeistung.LeistungsId
 * @see Adresse
 */
public record ErstelleRechnungFuerReservierung(Reservierung.ReservierungsNummer reservierungsNummer,
                                               List<ServiceLeistung.LeistungsId> serviceLeistungen,
                                               Adresse rechnungsAdresse) implements DomainCommand {
}