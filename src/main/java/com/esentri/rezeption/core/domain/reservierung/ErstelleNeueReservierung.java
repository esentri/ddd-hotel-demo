package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import nitrox.dlc.domain.types.DomainCommand;

import java.time.LocalDate;

/**
 * Eine Reservierung im Hotel wird mit dieser Klasse erstellt.
 *
 * Es handelt sich hierbei um eine Anforderung (Command), eine Reservierung im System zu erstellen.
 * Die Klasse beinhaltet alle notwendigen Informationen für eine Reservierung wie:
 * - Identifikation des Hotels
 * - das geplante Anreisedatum
 * - die geplante Anzahl der Nächte
 * - der angebotene Preis für das Zimmer
 * - die gewünschte Zimmerkategorie
 * - die gewünschte Kapazität
 * - Vor- und Nachname des Gastes
 *
 * Diese Klasse implementiert das DomainCommand Interface, um ihre Natur als Anforderung zur Veränderung des Zustandes des Systems auszudrücken.
 *
 * @author Mario Herb
 * @see DomainCommand
 * @see Hotel.HotelId
 * @see ZimmerKategorie
 * @see Preis
 */
public record ErstelleNeueReservierung(
                                  Hotel.HotelId hotelId,
                                  LocalDate geplanteAnkunftAm,
                                  int geplanteAnzahlNaechte,
                                  Preis angebotenerZimmerpreis,
                                  ZimmerKategorie gewuenschteZimmerKategorie,
                                  int gewuenschteKapazitaet,
                                  String gastVorname,
                                  String gastNachname) implements DomainCommand {}
