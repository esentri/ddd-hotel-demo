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
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.outport.ZimmerRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist eine Implementierung des {@code ZimmerRepository} Interface und erbt von {@code BaseInMemoryRepository}
 * Sie stellt Methoden bereit, um {@code Zimmer} Instanzen zu verwalten.
 *
 * @author Mario Herb
 * @see ZimmerRepository
 * @see BaseInMemoryRepository
 */
@Repository
public class ZimmerRepositoryImpl extends BaseInMemoryRepository<Zimmer.ZimmerNummer, Zimmer> implements ZimmerRepository {


    public ZimmerRepositoryImpl() {
        super(new ArrayList<>());
        var z1 = new Zimmer(
                TestDataIds.ZIMMER_ID_EINZELZIMMER.id(),
                TestDataIds.HOTEL_ID.id(),
                ZimmerKategorie.EINZELZIMMER,
                1,
                0
        );
        z1.neueBelegung(LocalDate.now().minusDays(1), LocalDate.now().plusDays(3), BelegungTyp.GAST);
        allAggregates.addAll(
                List.of(
                    z1,
                    new Zimmer(
                            TestDataIds.ZIMMER_ID_BUSINESS_SUITE.id(),
                            TestDataIds.HOTEL_ID.id(),
                            ZimmerKategorie.BUSINESS_SUITE,
                            2,
                            1
                    ),
                    new Zimmer(
                            TestDataIds.ZIMMER_ID_PRESIDENTIAL_SUITE.id(),
                            TestDataIds.HOTEL_ID.id(),
                            ZimmerKategorie.PRESIDENTIAL_SUITE,
                            4,
                            3
                    )
                )
        );
    }

    /**
     * Listet alle Zimmer einer bestimmten Kategorie und einer minimalen Kapazität in einem bestimmten Hotel auf.
     *
     * @param hotelId Die ID des Hotels, in dem die Zimmer gesucht werden.
     * @param zimmerKategorie Die Kategorie der Zimmer, die gesucht werden.
     * @param kapazitaet Die minimale Kapazität der Zimmer, die gesucht werden.
     * @return Eine Liste mit Zimmer-Instanzen, die den angegebenen Kriterien entsprechen.
     */
    @Override
    public List<Zimmer> listZimmerByKategorieAndKapazitaet(Hotel.HotelId hotelId, ZimmerKategorie zimmerKategorie, int kapazitaet) {
        return allAggregates.stream()
                .filter(z -> z.getHotelId().equals(hotelId))
                .filter(z -> z.getKategorie().equals(zimmerKategorie))
                .filter(z -> z.getKapazitaet() >= kapazitaet)
                .toList();
    }

    /**
     * Listet alle Zimmer in einem bestimmten Hotel auf.
     *
     * @param hotelId Die ID des Hotels, in dem die Zimmer gesucht werden.
     * @return Eine Liste von Zimmer-Instanzen im angegebenen Hotel.
     */
    @Override
    public List<Zimmer> listByHotel(Hotel.HotelId hotelId) {
        return allAggregates.stream().filter(z -> z.getHotelId().equals(hotelId)).toList();
    }
}
