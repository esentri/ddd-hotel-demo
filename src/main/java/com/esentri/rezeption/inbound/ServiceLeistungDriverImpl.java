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

package com.esentri.rezeption.inbound;

import com.esentri.rezeption.core.domain.rechnung.RechnungErstellt;
import com.esentri.rezeption.core.domain.serviceleistung.ErstelleServiceLeistung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.inport.ServiceLeistungDriver;
import com.esentri.rezeption.core.outport.ServiceLeistungRepository;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.ListensTo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Die Implementierung der ServiceLeistungDriver-Schnittstelle, die als Einstiegspunkt zum Managen von ServiceLeistungen in einem Hotel dient.
 *
 * @author Mario Herb
 */
@Service
@RequiredArgsConstructor
public class ServiceLeistungDriverImpl implements ServiceLeistungDriver {

    private final ServiceLeistungRepository serviceLeistungRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceLeistung.LeistungsId handle(ErstelleServiceLeistung erstelleServiceLeistung) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ListensTo(domainEventType = RechnungErstellt.class)
    public void onEvent(RechnungErstellt rechnungErstellt) {
        rechnungErstellt.abgerechneteServices().forEach(lid ->{
            var sl = serviceLeistungRepository.findById(lid).orElseThrow();
            serviceLeistungRepository.update(sl.abgerechnet(rechnungErstellt.rechnungId()));
        });

    }


}
