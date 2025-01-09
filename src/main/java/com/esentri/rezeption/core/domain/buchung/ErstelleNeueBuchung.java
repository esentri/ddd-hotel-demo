/*
 *  Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.esentri.rezeption.core.domain.buchung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import io.domainlifecycles.domain.types.DomainCommand;

import java.time.LocalDate;

/**
 * Eine Buchung im Hotel wird mit dieser Klasse erstellt.
 *
 * Es handelt sich hierbei um eine Anforderung (Command), eine Buchung im System zu erstellen.
 * Die Klasse beinhaltet alle notwendigen Informationen für eine Buchung wie:
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
 * @see Hotel.Id
 * @see ZimmerKategorie
 * @see Preis
 */
public record ErstelleNeueBuchung(
                                  Hotel.Id hotelId,
                                  LocalDate geplanteAnkunftAm,
                                  int geplanteAnzahlNaechte,
                                  Preis angebotenerZimmerpreis,
                                  ZimmerKategorie gewuenschteZimmerKategorie,
                                  int gewuenschteKapazitaet,
                                  String gastVorname,
                                  String gastNachname) implements DomainCommand {}
