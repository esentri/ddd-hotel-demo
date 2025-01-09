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

import com.esentri.rezeption.core.domain.buchung.BuchungAusgecheckt;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.BeantrageZimmerWartung;
import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerAuslastung;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungAbgelehnt;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungBestaetigt;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungEingeplant;
import com.esentri.rezeption.core.inport.ZimmerUseCases;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ZimmerAuslastungen;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import io.domainlifecycles.domain.types.ListensTo;
import io.domainlifecycles.domain.types.Publishes;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Die Implementierung der ZimmerUseCases-Schnittstelle, die als Einstiegspunkt zum Managen von Zimmern in einem Hotel dient.
 * Diese Klasse bietet Funktionen für das Beantragen von Zimmerwartungen und das Abrufen verfügbarer Zimmer.
 *
 * @author Mario Herb
 */
@Service
@RequiredArgsConstructor
public class ZimmerUseCasesImpl implements ZimmerUseCases {

    private final ZimmerVerwaltung zimmerVerwaltung;

    private final DomainEventPublisher domainEventPublisher;

    private final ZimmerAuslastungen zimmerAuslastungen;

    /**
     * {@inheritDoc}
     */
    @Override
    @EventListener
    @ListensTo(domainEventType = ZimmerWartungEingeplant.class)
    public void onEvent(ZimmerWartungEingeplant zimmerWartungEingeplant) {
        zimmerWartungEingeplant.zimmerNummern()
            .forEach(zimmerNummer ->
                handle(
                    new BeantrageZimmerWartung(
                        zimmerWartungEingeplant.planungReferenz(),
                        zimmerWartungEingeplant.von(),
                        zimmerWartungEingeplant.bis(),
                        zimmerNummer
                    )
                )
            );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Zimmer.ZimmerNummer> verfuegbareZimmer(Hotel.Id hotelId, LocalDate von, LocalDate bis, ZimmerKategorie zimmerKategorie, int kapazitaet) {
        var probeBelegung = new Belegung(
            von,
            bis,
            BelegungTyp.GAST
        );
        var verfuegbareZimer = zimmerVerwaltung.listZimmerByKategorieAndKapazitaet(
                hotelId,
                zimmerKategorie,
                kapazitaet
            )
            .stream()
            .filter(zimmer -> !zimmer.hatUeberschneidendeBelegung(probeBelegung))
            .map(Zimmer::id)
            .toList();
        return verfuegbareZimer;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Publishes(domainEventTypes = {ZimmerWartungBestaetigt.class, ZimmerWartungAbgelehnt.class})
    public void handle(BeantrageZimmerWartung beantrageZimmerWartung){
        var zimmer = zimmerVerwaltung.findById(beantrageZimmerWartung.zimmerNummer()).orElseThrow();
        if(zimmer.neueBelegung(beantrageZimmerWartung.von(), beantrageZimmerWartung.bis(), BelegungTyp.WARTUNG)){
            zimmerVerwaltung.update(zimmer);
            domainEventPublisher.publish(new ZimmerWartungBestaetigt(beantrageZimmerWartung.wartungsPlanungId(), zimmer.id()));
        }else{
            domainEventPublisher.publish(new ZimmerWartungAbgelehnt(beantrageZimmerWartung.wartungsPlanungId(), zimmer.id()));
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @EventListener
    @ListensTo(domainEventType = BuchungAusgecheckt.class)
    public void onEvent(BuchungAusgecheckt buchungAusgecheckt) {
        var zimmer = zimmerVerwaltung.findById(buchungAusgecheckt.zimmerNummer()).orElseThrow();
        zimmerVerwaltung.update(zimmer.aktuelleBelegungBeenden());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ZimmerAuslastung> auslastung(Hotel.Id hotelId, LocalDate von, LocalDate bis){
        return zimmerAuslastungen.auslastung(hotelId, von, bis);
    }
}
