package com.esentri.rezeption.core.domain.reservierung;

import nitrox.dlc.domain.types.DomainCommand;

/**
 * Die Klasse CheckeAus repräsentiert einen Befehl zum Auschecken einer Reservierung.
 * Es implementiert das Interface DomainCommand und sorgt für die korrekte Ausführung des Ablaufs.
 *
 * Es wird verwendet, um eine Anfrage zum Auschecken einer Reservierung zu repräsentieren
 * und enthält die Reservierungsnummer, die ausgecheckt werden muss.
 *
 * @author Mario Herb
 * @see DomainCommand
 */
public record CheckeAus(Reservierung.ReservierungsNummer reservierungsNummer) implements DomainCommand {
}