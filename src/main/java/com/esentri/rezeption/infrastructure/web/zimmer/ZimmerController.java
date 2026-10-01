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

package com.esentri.rezeption.infrastructure.web.zimmer;

import com.esentri.rezeption.application.zimmer.ZimmerApplicationService;
import com.esentri.rezeption.domain.auslastung.Zimmerauslastung;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST-Adapter für zimmerbezogene Auswertungen. Ruft ausschließlich den
 * {@link ZimmerApplicationService} auf, keine eigene Fachlogik.
 */
@RestController
@RequestMapping("/api/zimmer")
public class ZimmerController {

    private final ZimmerApplicationService zimmerApplicationService;

    public ZimmerController(ZimmerApplicationService zimmerApplicationService) {
        this.zimmerApplicationService = zimmerApplicationService;
    }

    @GetMapping("/auslastung")
    public List<Zimmerauslastung> auslastung(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate von,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bis,
        @RequestParam(required = false) Zimmerkategorie kategorie
    ) {
        return zimmerApplicationService.findeZimmerauslastung(von, bis, kategorie);
    }
}
