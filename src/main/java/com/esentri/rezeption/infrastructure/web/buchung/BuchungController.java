/*
 *  Copyright 2026 the original author or authors.
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

package com.esentri.rezeption.infrastructure.web.buchung;

import com.esentri.rezeption.application.buchung.BuchungApplicationService;
import com.esentri.rezeption.domain.buchung.AktualisiereGastdaten;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.buchung.CheckeGastEin;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST-Adapter für die Anwendungsfälle rund um eine Buchung. Ruft ausschließlich den
 * {@link BuchungApplicationService} auf, keine eigene Fachlogik.
 */
@RestController
@RequestMapping("/api/buchungen")
public class BuchungController {

    private final BuchungApplicationService buchungApplicationService;

    public BuchungController(BuchungApplicationService buchungApplicationService) {
        this.buchungApplicationService = buchungApplicationService;
    }

    @PostMapping("/{buchungsId}/checkin")
    public UUID checkIn(@PathVariable UUID buchungsId, @RequestBody CheckInRequest request) {
        var command = new CheckeGastEin(new BuchungsId(buchungsId), new ZimmerId(request.zimmerId()));
        return buchungApplicationService.checkeGastEin(command).value();
    }

    @PostMapping("/{buchungsId}/checkout")
    public UUID checkOut(@PathVariable UUID buchungsId) {
        var command = new CheckeGastAus(new BuchungsId(buchungsId));
        return buchungApplicationService.checkeGastAus(command).value();
    }

    @PutMapping("/{buchungsId}/gastdaten")
    public UUID aktualisiereGastdaten(@PathVariable UUID buchungsId, @RequestBody GastdatenRequest request) {
        var command = new AktualisiereGastdaten(
            new BuchungsId(buchungsId),
            request.vorname(),
            request.nachname(),
            request.geburtsdatum()
        );
        return buchungApplicationService.aktualisiereGastdaten(command).value();
    }

    public record CheckInRequest(UUID zimmerId) {}

    public record GastdatenRequest(String vorname, String nachname, LocalDate geburtsdatum) {}
}
