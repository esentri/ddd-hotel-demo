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

package com.esentri.rezeption.configuration;

import com.esentri.rezeption.core.domain.rechnung.RechnungsErstellungsService;
import com.esentri.rezeption.core.domain.reservierung.CheckInService;
import com.esentri.rezeption.core.domain.reservierung.CheckOutService;
import com.esentri.rezeption.core.domain.reservierung.ReservierungsEingangService;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.RechnungRepository;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import com.esentri.rezeption.core.outport.ServiceLeistungRepository;
import com.esentri.rezeption.core.outport.ZimmerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklasse ist für die Einrichtung der Bean-Definitionen verantwortlich.
 * Diese Klasse stellt die Servicebeans zur Verfügung, die von der Anwendung benötigt werden.
 *
 * @author Mario Herb
 */
@Configuration
public class DomainConfig {

    /**
     * Erstellt und konfiguriert eine Instanz des RechnungsErstellungsServices.
     *
     * @param rechnungRepository Repository für Rechnungsdetails.
     * @param reservierungRepository Repository für Reservierungsdetails.
     * @param serviceLeistungRepository Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten RechnungsErstellungsService.
     */
    @Bean
    public RechnungsErstellungsService rechnungsErstellungsService(
            RechnungRepository rechnungRepository,
            ReservierungRepository reservierungRepository,
            ServiceLeistungRepository serviceLeistungRepository,
            DomainEventPublisher domainEventPublisher
    ){
        return new RechnungsErstellungsService(
                rechnungRepository,
                reservierungRepository,
                serviceLeistungRepository,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckInService.
     *
     * @param reservierungRepository Repository für Reservierungsdetails.
     * @param zimmerRepository Repository für Zimmerdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckInService.
     */
    @Bean
    public CheckInService checkInService(
            ReservierungRepository reservierungRepository,
            ZimmerRepository zimmerRepository,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckInService(
                reservierungRepository,
                zimmerRepository,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckOutService.
     *
     * @param reservierungRepository Repository für Reservierungsdetails.
     * @param rechnungRepository Repository für Rechnungsdetails.
     * @param serviceLeistungRepository Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckOutService.
     */
    @Bean
    public CheckOutService checkOutService(
            ReservierungRepository reservierungRepository,
            RechnungRepository rechnungRepository,
            ServiceLeistungRepository serviceLeistungRepository,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckOutService(
                reservierungRepository,
                rechnungRepository,
                serviceLeistungRepository,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des ReservierungsEingangService.
     *
     * @param reservierungRepository Repository für Reservierungsdetails.
     * @param zimmerRepository Repository für Zimmerdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten ReservierungsEingangService.
     */
    @Bean
    public ReservierungsEingangService reservierungsEingangService(
            ReservierungRepository reservierungRepository,
            ZimmerRepository zimmerRepository,
            DomainEventPublisher domainEventPublisher
    ){
        return new ReservierungsEingangService(
                reservierungRepository,
                zimmerRepository,
                domainEventPublisher
        );
    }

}