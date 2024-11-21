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

package com.esentri.rezeption.core.domain.serviceleistung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import io.domainlifecycles.domain.types.AggregateRoot;
import io.domainlifecycles.domain.types.Identity;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Das ServiceLeistung, als AggregatRoot implementiert,
 * mit einer Beschreibung, einem Servicetyp und einem Nettopreis.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class ServiceLeistung implements AggregateRoot<ServiceLeistung.Id> {

    public record Id(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final Id id;

    private final Reservierung.ReservierungsNummer reservierungsNummer;

    private Rechnung.Id abgerechnetPer;

    private final String beschreibung;
    private final ServiceTyp serviceTyp;
    private final LocalDateTime erhaltenAm;
    private final Preis nettoPreis;

    private long concurrencyVersion;

    public ServiceLeistung(Id id,
                           Reservierung.ReservierungsNummer reservierungsNummer,
                           String beschreibung,
                           LocalDateTime erhaltenAm,
                           ServiceTyp serviceTyp,
                           Preis nettoPreis
    ) {
        Objects.requireNonNull(id, "Die Id darf nicht null sein.");
        Objects.requireNonNull(reservierungsNummer, "Die referenzierte ReservierungsNummer darf nicht null sein.");
        Objects.requireNonNull(serviceTyp, "Der ServiceTyp darf nicht null sein.");
        Objects.requireNonNull(nettoPreis, "Der NettoPreis darf nicht null sein.");
        this.id = id;
        this.reservierungsNummer = reservierungsNummer;
        this.validateBeschreibung(beschreibung);
        this.beschreibung = beschreibung;
        this.serviceTyp = serviceTyp;
        this.nettoPreis = nettoPreis;
        this.erhaltenAm = erhaltenAm;
    }

    private void validateBeschreibung(String beschreibung) {
        if (beschreibung != null && beschreibung.length() > 1000) {
            throw new IllegalArgumentException("Die Beschreibung sollte maximal 1000 Zeichen lang sein.");
        }
    }

    @Override
    public Id id() {
        return id;
    }

    public ServiceLeistung abgerechnet(Rechnung.Id rechnungId){
        Objects.requireNonNull(rechnungId, "Die Id darf nicht null sein.");
        this.abgerechnetPer = rechnungId;
        return this;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}
