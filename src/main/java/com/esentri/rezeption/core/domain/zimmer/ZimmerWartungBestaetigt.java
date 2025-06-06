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

package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.WartungsPlanungId;
import io.domainlifecycles.domain.types.DomainEvent;

/**
 * ZimmerWartungBestaetigt repräsentiert das Ereignis, wenn eine Wartung bestätigt wird.
 * Er enthält die Referenz auf den Wartungsplan und die Zimmernummer, für die die Wartung bestätigt wurde.
 *
 * @author Mario Herb
 */
public record ZimmerWartungBestaetigt(WartungsPlanungId planungsReferenz, Zimmer.ZimmerNummer zimmerNummer) implements DomainEvent {
}
