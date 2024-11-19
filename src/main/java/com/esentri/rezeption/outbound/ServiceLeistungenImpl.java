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
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceTyp;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse ServiceLeistungenImpl bietet eine Implementierung des
 * ServiceLeistungen-Interfaces. Sie erweitert BaseInMemoryRepository.
 *
 * @author Mario Herb
 */
@Repository
public class ServiceLeistungenImpl extends BaseInMemoryRepository<ServiceLeistung.Id, ServiceLeistung> implements ServiceLeistungen {

    /**
     * Der Standardkonstruktor erstellt ein neues Objekt dieses Typs und füllt das Repository
     * mit vordefinierten Daten.
     */
    public ServiceLeistungenImpl() {
        super(new ArrayList<>());
        allAggregates.addAll(List.of(
                new ServiceLeistung(
                        TestDataIds.SERVICE_LEISTUNG_ID.id(),
                        TestDataIds.RESERVIERUNG_ID_EINGECHECKT.id(),
                        "Flasche Dom Perignon 2013",
                        LocalDateTime.now().minusDays(1),
                        ServiceTyp.BAR,
                        new Preis(BigDecimal.valueOf(300))
                )
        ));
    }

    /**
     * Diese Methode findet ServiceLeistung-Instanzen,
     * die zu der übergebenen Reservierungsnummer gehören.
     *
     * @param reservierungsNummer Die Reservierungsnummer, nach der gesucht werden soll.
     * @return eine Liste von ServiceLeistung-Instanzen, die der angegebenen Reservierungsnummer entsprechen.
     */
    @Override
    public List<ServiceLeistung> find(Reservierung.ReservierungsNummer reservierungsNummer) {
        return allAggregates.stream().filter(sl -> reservierungsNummer.equals(sl.getReservierungsNummer())).toList();
    }
}