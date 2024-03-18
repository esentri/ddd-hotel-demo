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

package com.esentri.rezeption.core.domain.hotel;

import com.esentri.rezeption.core.domain.Adresse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nitrox.dlc.domain.types.AggregateRoot;
import nitrox.dlc.domain.types.Identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Die Klasse Hotel repräsentiert einen Hotel-Betrieb in unserem System, implementiert als Aggregatwurzel nach DDD-Ansatz.
 * Ein Hotel braucht eine ID, einen Namen und eine Adresse.
 * <p>
 * Wir gewährleisten die Bekanntmachung einer Hotelinstanz nur durch ihre ID, da ein Hotel nach unserer Geschäftslogik als kohärente Einheit agiert.
 * Bei Änderungen, setzt das System den Namen und die Adresse des Hotels neu,
 * wobei die Eingabe auf null überprüft wird, um die Datenintegrität sicherzustellen.
 * <p>
 * Weitere Details entnehmen Sie bitte den Kommentaren zu den einzelnen Methoden.
 *
 * @author Mario Herb
 *
 * @see AggregateRoot
 * @see Identity
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Hotel implements AggregateRoot<Hotel.HotelId> {

    /**
     * Eindeutige Identität eines Hotels.
     *
     * @see Identity
     */
    public record HotelId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final HotelId id;

    private String name;
    private Adresse adresse;

    private long concurrencyVersion;

    /**
     * Konstruktor eines Hotels.
     *
     * @param id Eindeutige Identität eines Hotels.
     * @param name Name des Hotels.
     * @param adresse Adresse des Hotels.
     *
     * @throws IllegalArgumentException wenn id null ist.
     * @throws NullPointerException wenn name oder adresse null sind.
     */
    @Builder
    public Hotel(HotelId id, String name, Adresse adresse) {
        if (id == null) throw new IllegalArgumentException("Id darf nicht null sein");

        validateName(name);
        validateAdresse(adresse);

        this.id = id;
        this.name = name;
        this.adresse = adresse;
    }

    /**
     * Validiert, dass der Name nicht null ist.
     *
     * @param name Name des Hotels.
     *
     * @throws NullPointerException wenn Name null ist.
     */
    private void validateName(String name) {
        Objects.requireNonNull(name, "Der Name darf nicht null sein.");
    }

    /**
     * Validiert, dass die Adresse nicht null ist.
     *
     * @param adresse Adresse des Hotels.
     *
     * @throws NullPointerException wenn Adresse null ist.
     */
    private void validateAdresse(Adresse adresse) {
        Objects.requireNonNull(adresse, "Die Adresse darf nicht null sein.");
    }

    /**
     * Setzt den Namen eines Hotels.
     *
     * @param name Neuer Name des Hotels.
     *
     * @throws NullPointerException wenn Name null ist.
     */
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    /**
     * Setzt die Adresse eines Hotels.
     *
     * @param adresse Neue Adresse des Hotels.
     *
     * @throws NullPointerException wenn Adresse null ist.
     */
    public void setAdresse(Adresse adresse) {
        validateAdresse(adresse);
        this.adresse = adresse;
    }

    /**
     * Gibt die eindeutige Identität des Hotels zurück.
     *
     * @return Eindeutige Identität des Hotels.
     */
    @Override
    public HotelId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}