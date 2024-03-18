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

import com.esentri.rezeption.core.domain.reservierung.CheckInService;
import com.esentri.rezeption.core.domain.reservierung.CheckOutService;
import com.esentri.rezeption.core.domain.reservierung.CheckeAus;
import com.esentri.rezeption.core.domain.reservierung.CheckeEin;
import com.esentri.rezeption.core.domain.reservierung.ErstelleNeueReservierung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.reservierung.ReservierungsEingangService;
import com.esentri.rezeption.core.domain.reservierung.StorniereReservierung;
import com.esentri.rezeption.core.domain.reservierung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.inport.ReservierungDriver;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung des ReservierungDriver. Verantwortlich für die Verarbeitung von Reservierung-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@AllArgsConstructor
public class ReservierungDriverImpl implements ReservierungDriver {

    private final ReservierungRepository reservierungRepository;

    private final ReservierungsEingangService reservierungsEingangService;

    private final CheckInService checkInService;

    private final CheckOutService checkOutService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(ErstelleNeueReservierung erstelleNeueReservierung) {
        return reservierungsEingangService.handle(erstelleNeueReservierung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten) {
        return reservierungRepository.findById(vervollstaendigeGastDaten.reservierungsNummer())
                .map(r ->
                        reservierungRepository.update(r.handle(vervollstaendigeGastDaten)))
                .map(Reservierung::id)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeEin checkeEin) {
        return checkInService.handle(checkeEin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeAus checkeAus) {
        return checkOutService.handle(checkeAus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(StorniereReservierung storniereReservierung) {
        return reservierungRepository.findById(storniereReservierung.reservierungsNummer())
                .map(r ->
                        reservierungRepository.update(r.storniere()))
                .map(Reservierung::id)
                .orElseThrow();
    }


}
