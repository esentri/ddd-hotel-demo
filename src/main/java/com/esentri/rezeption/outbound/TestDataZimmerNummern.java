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

package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.domain.zimmer.ZimmerNummer;
import io.domainlifecycles.domain.types.Identity;


/**
 * Diese Enum-Klasse dient zur Bereitstellung von Testdaten-ZimmerNummern.
 *
 * @author Mario Herb
 */
public enum TestDataZimmerNummern {
    ZIMMER_NUMMER_EINZELZIMMER(new ZimmerNummer("1")),
    ZIMMER_NUMMER_BUSINESS_SUITE(new ZimmerNummer("2")),
    ZIMMER_NUMMER_PRESIDENTIAL_SUITE(new ZimmerNummer("3"));

    private final ZimmerNummer zimmerNummer;

    public ZimmerNummer nummer(){
        return zimmerNummer;
    }

    TestDataZimmerNummern(ZimmerNummer zimmerNummer) {
        this.zimmerNummer = zimmerNummer;
    }
}
