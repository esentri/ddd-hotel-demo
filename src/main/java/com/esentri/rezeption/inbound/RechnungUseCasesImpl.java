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

import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerReservierung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleServiceRechnung;
import com.esentri.rezeption.core.domain.rechnung.LadeRechnungPDF;
import com.esentri.rezeption.core.domain.rechnung.MarkiereRechnungBezahlt;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.rechnung.RechnungsErstellung;
import com.esentri.rezeption.core.inport.RechnungUseCases;
import com.esentri.rezeption.core.outport.Rechnungen;
import com.esentri.rezeption.core.outport.RechnungsPDFErstellung;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung des RechnungDrivers. Verantwortlich für die Verarbeitung von Rechnungs-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@RequiredArgsConstructor
public class RechnungUseCasesImpl implements RechnungUseCases {

    private final Rechnungen rechnungen;

    private final RechnungsErstellung rechnungsErstellung;

    private final RechnungsPDFErstellung rechnungsPDFErstellung;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.Id handle(MarkiereRechnungBezahlt markiereRechnungBezahlt) {
        return rechnungen.findById(markiereRechnungBezahlt.rechnungId())
            .map(r -> rechnungen.update(r.markiereBezahlt()).getId())
            .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.Id handle(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung) {
        return rechnungsErstellung.handle(erstelleRechnungFuerReservierung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.Id handle(ErstelleServiceRechnung erstelleServiceRechnung) {
        return rechnungsErstellung.handle(erstelleServiceRechnung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] handle(LadeRechnungPDF ladeRechnungPDF) {
        return rechnungsPDFErstellung.erzeugePDF(ladeRechnungPDF.rechnungsId());
    }

}
