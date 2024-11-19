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

package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import io.domainlifecycles.domain.types.Entity;
import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

/**
 * Die Klasse RechnungsPosition repräsentiert eine Position auf einer Rechnung in der Domain.
 * Sie gehorcht den Eigenschaften einer Entity gemäß dem DDD-Ansatz (Domain-driven Design)
 * und beinhaltet Informationen wie Preis, ServiceLeistung und eine Beschreibung der Position.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class RechnungsPosition implements Entity<RechnungsPosition.RechnungsPositionId> {

    /**
     * Der Identifier der Rechnungsposition, implementiert als record-Typ.
     */
    public record RechnungsPositionId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final RechnungsPositionId id;

    /**
     * Der Preis der Rechnungsposition.
     */
    private final Preis preis;

    /**
     * Der Identifier der ServiceLeistung, die mit dieser Rechnungsposition abgerechnet wird.
     */
    private final ServiceLeistung.Id leistungsId;

    /**
     * Eine Beschreibung der Rechnungsposition.
     */
    private final String beschreibung;

    private long concurrencyVersion;

    @Override
    public RechnungsPositionId id() {
        return id;
    }

    /**
     * Erstellt eine neue Rechnungsposition mit gegebenen Identifier, Preis, Beschreibung und ServiceLeistung.
     * Es wird überprüft, ob der Preis und die ServiceLeistung nicht null sind und ob die Beschreibung nicht länger
     * als 1000 Zeichen ist, wobei in diesem Fall eine IllegalArgumentException ausgelöst wird.
     *
     * @param id Der Identifier der Rechnungsposition.
     * @param preis Der Preis der Rechnungsposition.
     * @param beschreibung Eine Beschreibung der Rechnungsposition.
     * @param leistungsId Der Identifier der ServiceLeistung.
     */
    RechnungsPosition(RechnungsPositionId id,
                      Preis preis,
                      String beschreibung,
                      ServiceLeistung.Id leistungsId
    ) {
        if (preis == null) {
            throw new IllegalArgumentException("Preis darf nicht null sein");
        }
        if (leistungsId == null) {
            throw new IllegalArgumentException("Id darf nicht null sein");
        }
        if (beschreibung != null && beschreibung.length() > 1000) {
            throw new IllegalArgumentException("Beschreibung darf nicht mehr als 1000 Zeichen enthalten.");
        }
        this.id = id;
        this.preis = preis;
        this.leistungsId = leistungsId;
        this.beschreibung = beschreibung;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}