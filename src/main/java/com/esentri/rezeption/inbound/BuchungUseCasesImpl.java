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
import com.esentri.rezeption.core.domain.buchung.CheckeBuchungAus;
import com.esentri.rezeption.core.domain.buchung.CheckeBuchungEin;
import com.esentri.rezeption.core.domain.buchung.ErstelleNeueBuchung;
import com.esentri.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.rezeption.core.domain.buchung.VervollstaendigeBuchungGastDaten;
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
    public Buchung.BuchungsNummer handleErstelleNeueBuchung(ErstelleNeueBuchung erstelleNeueBuchung) {
        return buchungseingang.handle(erstelleNeueBuchung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handleVervollstaendigeBuchungGastDaten(VervollstaendigeBuchungGastDaten vervollstaendigeBuchungGastDaten) {
        return buchungen.findById(vervollstaendigeBuchungGastDaten.buchungsNummer())
                .map(r ->
                        buchungen.update(r.handleVervollstaendigeBuchungGastDaten(vervollstaendigeBuchungGastDaten)))
                .map(Buchung::getId)
                .orElseThrow(()->
                        new IllegalStateException(
                            String.format("Buchung mit der BuchungsNummer '%s' nicht gefunden!",
                                vervollstaendigeBuchungGastDaten.buchungsNummer()
                        )
                    )
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handleCheckeBuchungEin(CheckeBuchungEin checkeBuchungEin) {
        return checkIn.handleCheckeBuchungEin(checkeBuchungEin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handleCheckeBuchungAus(CheckeBuchungAus checkeBuchungAus) {
        return checkOut.handleCheckeBuchungAus(checkeBuchungAus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Buchung.BuchungsNummer handleStorniereBuchung(StorniereBuchung storniereBuchung) {
        return buchungen.findById(storniereBuchung.buchungsNummer())
                .map(r ->
                        buchungen.update(r.storniere()))
                .map(Buchung::getId)
                .orElseThrow(()->
                    new IllegalStateException(
                        String.format("Buchung mit der BuchungsNummer '%s' nicht gefunden!",
                                storniereBuchung.buchungsNummer()
                        )
                    )
                );
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
