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

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.ZimmerAuslastung;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.ZimmerAuslastungen;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Diese Klasse stellt Zugang zum ReadModel ZimmerAuslastung zur Verfügung.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
@Service
public class ZimmerAuslastungenImpl implements ZimmerAuslastungen {

    private final ZimmerVerwaltung zimmerVerwaltung;
    private final Buchungen buchungen;

    /**
     * Berechnet und liefert eine Liste von ZimmerAuslastungen für ein bestimmtes Hotel und einen bestimmten Zeitraum.
     *
     * @param hotelId Identität des Hotels
     * @param von Beginndatum des Zeitraums
     * @param bis Enddatum des Zeitraums
     * @return Eine Liste von ZimmerAuslastung Objekten mit der Anzahl der belegten und freien Zimmer für jeden Tag des gewählten Zeitraums.
     * @throws IllegalStateException wenn hotelId, von, bis nicht korrekt definiert sind
     */
    public List<ZimmerAuslastung> auslastung(Hotel.Id hotelId, LocalDate von, LocalDate bis){
        if(hotelId == null){
            throw new IllegalStateException("Auslastung kann nur pro Hotel abgefragt werden!");
        }
        if(von == null || bis == null){
            throw new IllegalStateException("Bei einer Auslastungabfrage muss immer ein abgeschlossener Zeitraum angegeben werden!");
        }
        if(von.isAfter(bis)){
            throw new IllegalStateException("Bei einer Auslastungabfrage darf 'von' nicht nach 'bis' sein!");
        }
        var alleZimmerGruppiert = zimmerVerwaltung.listByHotel(hotelId).stream()
                .collect(Collectors.groupingBy(z -> new KategorieUndKapazitaet(z.getKategorie(), z.getKapazitaet())));
        var datum = von;
        var auslastungsUebersicht = new ArrayList<ZimmerAuslastung>();
        while (!datum.isAfter(bis)){
            var datumFinal = datum;
            for(var katUndKapa : alleZimmerGruppiert.keySet()){
                var zimmer = alleZimmerGruppiert.get(katUndKapa);
                var anzahlZimmerBelegt = (int)zimmer.stream().flatMap(z -> z.getBelegungen().stream()).filter(b -> b.istBelegtAm(datumFinal)).count();
                var anzahlZimmerZusaetzlichReserviert = buchungen.listAktiveBuchungenInZeitraum(hotelId, von, bis, katUndKapa.zimmerKategorie, katUndKapa.kapazitaet())
                    .stream()
                    .filter(b -> b.getCheckInAm() == null)
                    .filter(b -> b.buchungsZeitraumBeinhaltet(datumFinal))
                    .toList();
                auslastungsUebersicht.add(
                    new ZimmerAuslastung(
                        katUndKapa.zimmerKategorie(),
                        katUndKapa.kapazitaet(),
                        zimmer.size(),
                        anzahlZimmerBelegt + anzahlZimmerZusaetzlichReserviert.size(),
                        datumFinal
                    )
                );
            }

            datum = datum.plusDays(1);
        }
        return auslastungsUebersicht;
    }

    /**
     * Hilfs-Record mit Zimmerkategorie und Kapazität.
     */
    private record KategorieUndKapazitaet(ZimmerKategorie zimmerKategorie, int kapazitaet){}
}
