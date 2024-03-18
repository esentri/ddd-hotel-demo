package com.esentri.rezeption.core.domain.serviceleistung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import nitrox.dlc.domain.types.DomainCommand;

/**
 * Diese Klasse wird beim Erstellen einer Serviceleistung verwendet.
 * Sie enthält die Reservierungsnummer, den Servicetyp und den Nettopreis einer Serviceleistung.
 * Sie implementiert das DomainCommand-Interface, das dafür sorgt,
 * dass Aktionen explizit beschrieben und auf Aggregaten durchgeführt werden können.
 *
 * @author Mario Herb
 * @see DomainCommand
 * @see Reservierung
 * @see ServiceTyp
 * @see Preis
 */
public record ErstelleServiceLeistung(Reservierung.ReservierungsNummer reservierungsNummer, ServiceTyp serviceTyp, Preis nettoPreis) implements DomainCommand {
}