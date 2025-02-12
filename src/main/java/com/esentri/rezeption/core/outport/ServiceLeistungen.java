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

package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import io.domainlifecycles.domain.types.Repository;

import java.util.List;

/**
 * Repository für ServiceLeistungen.
 *
 * @author Mario Herb
 */
public interface ServiceLeistungen extends Repository<ServiceLeistung.Id, ServiceLeistung> {

    /**
     * Sucht eine Liste von ServiceLeistungen basierend auf ihrer BuchungsNummer.
     *
     * @param buchungsNummer Die BuchungsNummer der gesuchten ServiceLeistungen.
     * @return Eine Liste von ServiceLeistungen, welche die gegebene BuchungsNummer haben. Eine leere Liste, falls keine passenden ServiceLeistungen gefunden werden könnten.
     */
    List<ServiceLeistung> find(Buchung.BuchungsNummer buchungsNummer);
}
