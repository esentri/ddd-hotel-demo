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
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.Gast;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung der {@link ReservierungRepository} Klasse.
 * Speichert alle Reservierungsdaten InMemory
 *
 * @author Mario Herb
 */
@Repository
public class ReservierungRepositoryImpl extends BaseInMemoryRepository<Reservierung.ReservierungsNummer, Reservierung> implements ReservierungRepository {

    public ReservierungRepositoryImpl() {
        super(new ArrayList<>());
        allAggregates.addAll(List.of(
           new Reservierung(
                   TestDataIds.RESERVIERUNG_ID_OFFEN.id(),
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
                           TestDataIds.GAST_RESERVIERUNG_OFFEN_ID.id(),
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
                new Reservierung(
                        TestDataIds.RESERVIERUNG_ID_EINGECHECKT.id(),
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
                                TestDataIds.GAST_RESERVIERUNG_EINGECHECKT_ID.id(),
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
     * Findet und gibt eine Liste von aktiven Reservierungen für ein bestimmtes Hotel zurück, die bestimmte Kriterien erfüllen.
     *
     * @param hotelId das Id des Hotels
     * @param von  Datum an dem die buchungen anfangen
     * @param bis  Datum an dem die buchungen enden
     * @param gewuenschteKategorie die gewünschte Zimmerkategorie
     * @param gewuenschteKapazitaet die gewünschte Zimmerkapazität
     * @return eine Liste von Reservierungen
     */
    @Override
    public List<Reservierung> listAktiveBuchungenInZeitraum(Hotel.HotelId hotelId, LocalDate von, LocalDate bis, ZimmerKategorie gewuenschteKategorie, int gewuenschteKapazitaet) {
        return allAggregates.stream()
                .filter(b -> b.getHotelId().equals(hotelId)
                        && b.getCheckOutAm() == null && b.getStorniertAm() == null
                )
                .filter(b -> b.getGewuenschteZimmerKategorie().equals(gewuenschteKategorie))
                .filter(b -> b.getGewuenschteKapazitaet() == gewuenschteKapazitaet)
                .filter(b -> buchungsZeitraumUeberschneidung(von, bis, b))
                .toList();
    }

    /**
     * Prüft, ob der Buchungszeitraum einer bestimmten Reservierung mit einem gegebenen Datum überschneidet.
     *
     * @param von  Datum an dem die buchungen anfangen
     * @param bis  Datum an dem die buchungen enden
     * @param b  die Reservierung
     * @return einen Boolean der angibt ob es eine Überschneidung gibt oder nicht
     */
    private boolean buchungsZeitraumUeberschneidung(LocalDate von, LocalDate bis, Reservierung b){
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
