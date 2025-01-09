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

package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.buchung.Gast;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.outport.Buchungen;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung der {@link Buchungen} Klasse.
 * Speichert alle Buchungen InMemory
 *
 * @author Mario Herb
 */
@Repository
public class BuchungenImpl extends BaseInMemoryRepository<Buchung.BuchungsNummer, Buchung> implements Buchungen {

    public BuchungenImpl() {
        super(new ArrayList<>());
        allAggregates.addAll(List.of(
           new Buchung(
                   TestDataIds.BUCHUNG_ID_OFFEN.id(),
                   TestDataIds.HOTEL_ID.id(),
                   LocalDate.now(),
                   null,
                   null,
                   null,
                   new Preis(BigDecimal.valueOf(100)),
                   3,
                   ZimmerKategorie.BUSINESS_SUITE,
                   1,

                   new Gast(
                           TestDataIds.GAST_BUCHUNG_OFFEN_ID.id(),
                           "Chuck",
                           "Bubu",
                           null,
                           null,
                           null,
                          null
                   ),
                   null
                   ,0
           ),
                new Buchung(
                        TestDataIds.BUCHUNG_ID_EINGECHECKT.id(),
                        TestDataIds.HOTEL_ID.id(),
                        LocalDate.now().minusDays(1),
                        LocalDateTime.now().minusDays(1),
                        null,
                        null,
                        new Preis(BigDecimal.valueOf(80)),
                        4,
                        ZimmerKategorie.EINZELZIMMER,
                        1,

                        new Gast(
                                TestDataIds.GAST_BUCHUNG_EINGECHECKT_ID.id(),
                                "John",
                                "Doe",
                                LocalDate.of(1970,1,1),
                                null,
                                null,
                                null
                        ),
                        TestDataIds.ZIMMER_ID_EINZELZIMMER.id(),
                        0
                )
        ));

    }

    /**
     * Findet und gibt eine Liste von aktiven (nicht ausgecheckt und nicht storniert) Buchungen für ein bestimmtes Hotel zurück, die bestimmte Kriterien erfüllen.
     *
     * @param hotelId das Id des Hotels
     * @param von  Datum an dem die Buchungen anfangen
     * @param bis  Datum an dem die Buchungen enden
     * @param gewuenschteKategorie die gewünschte Zimmerkategorie (optional)
     * @param gewuenschteKapazitaet die gewünschte Zimmerkapazität (optional)
     * @return eine Liste von Buchungen
     */
    @Override
    public List<Buchung> listAktiveBuchungenInZeitraum(Hotel.Id hotelId, LocalDate von, LocalDate bis, ZimmerKategorie gewuenschteKategorie, Integer gewuenschteKapazitaet) {
         var stream = allAggregates.stream()
                .filter(b -> b.getHotelId().equals(hotelId)
                        && b.getCheckOutAm() == null && b.getStorniertAm() == null
                );
         if(gewuenschteKapazitaet != null){
             stream = stream.filter(b -> b.getGewuenschteKapazitaet() == gewuenschteKapazitaet);
         }
         if(gewuenschteKategorie != null){
             stream = stream.filter(b -> b.getGewuenschteZimmerKategorie() == gewuenschteKategorie);
         }
         stream = stream.filter(b -> buchungsZeitraumUeberschneidung(von, bis, b));
         return stream.toList();
    }

    /**
     * Prüft, ob der Zeitraum einer bestimmten Buchung mit einem gegebenen Datum überschneidet.
     *
     * @param von  Datum an dem die buchungen anfangen
     * @param bis  Datum an dem die buchungen enden
     * @param b  die Buchung
     * @return einen Boolean der angibt ob es eine Überschneidung gibt oder nicht
     */
    private boolean buchungsZeitraumUeberschneidung(LocalDate von, LocalDate bis, Buchung b){
        var checkInLocalDate = b.getGeplanteAnkunftAm();
        if(b.getCheckInAm() != null ) {
            checkInLocalDate = b.getCheckInAm().toLocalDate();
        }
        var plannedCheckOutLocalDate = checkInLocalDate.plusDays(b.getGeplanteAnzahlNaechte());
        if(bis.isEqual(checkInLocalDate) || bis.isBefore(checkInLocalDate)){
            return false;
        }
        return !von.isEqual(plannedCheckOutLocalDate) && !von.isAfter(plannedCheckOutLocalDate);
    }
}
