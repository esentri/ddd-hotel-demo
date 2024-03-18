package com.esentri.rezeption.core.domain.rechnung;

import nitrox.dlc.domain.types.DomainCommand;

/**
 * Ein DomainCommand zur Markierung einer Rechnung als bezahlt.
 * Dieses Command ändert den Zustand der Rechnung im System auf bezahlt.
 * Die Ausführung kann abgelehnt werden, wenn die Rechnung nicht existiert oder bereits bezahlt wurde.
 *
 *
 * @param rechnungId Die ID der Rechnung, die als bezahlt markiert werden soll.
 * @see com.esentri.rezeption.ddd.DomainCommand
 * @see com.esentri.rezeption.core.domain.rechnung.Rechnung
 * @author Mario Herb
 */
public record MarkiereRechnungBezahlt(Rechnung.RechnungId rechnungId) implements DomainCommand {

}