package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.DomainCommand;

import java.util.List;

/**
 * Erstellt eine ServiceRechnung inklusive ReservierungsNummer,
 * einer Liste von Serviceleistungen und einer RechnungsAdresse.
 * Implementiert das Interface DomainCommand.
 *
 * @author Mario Herb
 *
 * @see Reservierung.ReservierungsNummer
 * @see ServiceLeistung.LeistungsId
 * @see Adresse
 * @see DomainCommand
 */
public record ErstelleServiceRechnung(
        Reservierung.ReservierungsNummer reservierungsNummer,
        List<ServiceLeistung.LeistungsId> serviceLeistungen,
        Adresse rechnungsAdresse
) implements DomainCommand {
}