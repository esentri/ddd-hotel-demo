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

import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import nitrox.dlc.domain.types.ReadModel;

import java.time.LocalDate;

/**
 * Diese Klasse repräsentiert eine ZimmerAuslastung und implementiert ein ReadModel.
 *
 * Ein ReadModel kann nützlich sein, wenn die Grenzen eines Aggregats zu weit oder zu eng sind.
 * Sie sind nicht wirklich Teil des inneren Kernbereichs, jedoch hilft es zu wissen, welche
 * ReadModel in der Anwendung verwendet/verfügbar sind.
 *
 * @author Mario Herb
 * @see <a href="http://gorodinski.com/blog/2012/04/25/read-models-as-a-tactical-pattern-in-domain-driven-design-ddd/">ReadModel as a tactical pattern</a>
 * @see ZimmerKategorie
 */
public record ZimmerAuslastung(
        ZimmerKategorie zimmerKategorie,
        int kapazitaet,
        int anzahlGesamt,
        int anzahlBelegt,
        LocalDate vom
) implements ReadModel {}
