package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.DomainEvent;

import java.util.List;

/**
 * Die Klasse RechnungErstellt dient dazu, Ereignisse zu repräsentieren, bei denen eine Rechnung erstellt wurde.
 * Sie ist ein record und implementiert das Interface DomainEvent.
 *
 * Eine instanziierte RechnungErstellt enthält eine Rechnungs-ID, eine Reservierungsnummer,
 * einen Gesamtnettopreis und eine Liste von abgerechneten Dienstleistungen.
 *
 * @author Mario Herb
 */
public record RechnungErstellt(Rechnung.RechnungId rechnungId,
                               Reservierung.ReservierungsNummer reservierungsNummer,
                               Preis gesamtNetto,
                               List<ServiceLeistung.LeistungsId> abgerechneteServices) implements DomainEvent {
}
