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

import com.esentri.rezeption.core.domain.reservierung.CheckIn;
import com.esentri.rezeption.core.domain.reservierung.CheckOut;
import com.esentri.rezeption.core.domain.reservierung.CheckeAus;
import com.esentri.rezeption.core.domain.reservierung.CheckeEin;
import com.esentri.rezeption.core.domain.reservierung.ErstelleNeueReservierung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.reservierung.Reservierungseingang;
import com.esentri.rezeption.core.domain.reservierung.StorniereReservierung;
import com.esentri.rezeption.core.domain.reservierung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.inport.ReservierungUseCases;
import com.esentri.rezeption.core.outport.Reservierungen;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung des ReservierungUseCases. Verantwortlich für die Verarbeitung von Reservierung-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@AllArgsConstructor
public class ReservierungUseCasesImpl implements ReservierungUseCases {

    private final Reservierungen reservierungen;

    private final Reservierungseingang reservierungseingang;

    private final CheckIn checkIn;

    private final CheckOut checkOut;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(ErstelleNeueReservierung erstelleNeueReservierung) {
        return reservierungseingang.handle(erstelleNeueReservierung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten) {
        return reservierungen.findById(vervollstaendigeGastDaten.reservierungsNummer())
                .map(r ->
                        reservierungen.update(r.handle(vervollstaendigeGastDaten)))
                .map(Reservierung::id)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeEin checkeEin) {
        return checkIn.handle(checkeEin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeAus checkeAus) {
        return checkOut.handle(checkeAus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(StorniereReservierung storniereReservierung) {
        return reservierungen.findById(storniereReservierung.reservierungsNummer())
                .map(r ->
                        reservierungen.update(r.storniere()))
                .map(Reservierung::id)
                .orElseThrow();
    }


}
