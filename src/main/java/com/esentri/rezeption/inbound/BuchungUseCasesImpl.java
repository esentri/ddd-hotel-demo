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

import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.buchung.Buchungseingang;
import com.esentri.rezeption.core.domain.buchung.CheckIn;
import com.esentri.rezeption.core.domain.buchung.CheckOut;
import com.esentri.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.rezeption.core.domain.buchung.ErstelleNeueBuchung;
import com.esentri.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.rezeption.core.domain.buchung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.inport.BuchungUseCases;
import com.esentri.rezeption.core.outport.Buchungen;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementierung des BuchungUseCases. Verantwortlich für die Verarbeitung von Buchung-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@AllArgsConstructor
public class BuchungUseCasesImpl implements BuchungUseCases {

    private final Buchungen buchungen;

    private final Buchungseingang buchungseingang;

    private final CheckIn checkIn;

    private final CheckOut checkOut;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handle(ErstelleNeueBuchung erstelleNeueBuchung) {
        return buchungseingang.handle(erstelleNeueBuchung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten) {
        return buchungen.findById(vervollstaendigeGastDaten.buchungsNummer())
                .map(r ->
                        buchungen.update(r.handle(vervollstaendigeGastDaten)))
                .map(Buchung::id)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handle(CheckeEin checkeEin) {
        return checkIn.handle(checkeEin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handle(CheckeAus checkeAus) {
        return checkOut.handle(checkeAus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handle(StorniereBuchung storniereBuchung) {
        return buchungen.findById(storniereBuchung.buchungsNummer())
                .map(r ->
                        buchungen.update(r.storniere()))
                .map(Buchung::id)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Buchung> listAktiveBuchungenInZeitraum(
            Hotel.Id hotelId,
            LocalDate von,
            LocalDate bis,
            ZimmerKategorie gewuenschteKategorie,
            Integer gewuenschteKapazitaet
    ) {
        return buchungen.listAktiveBuchungenInZeitraum(hotelId, von, bis, gewuenschteKategorie, gewuenschteKapazitaet);
    }

}
