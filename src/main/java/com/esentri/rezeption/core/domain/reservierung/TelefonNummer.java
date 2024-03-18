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

package com.esentri.rezeption.core.domain.reservierung;

import nitrox.dlc.domain.types.ValueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Dies ist eine Hilfsklasse zur Speicherung und Validierung von Telefonnummern.
 * Sie stellt sicher, dass nur gültige Nummern zugelassen sind, die den folgenden Kriterien entsprechen:
 * Nur Nummern, Leerzeichen, Bindestriche und Pluszeichen sind erlaubt.
 *
 * @author Mario Herb
 */
public record TelefonNummer(String wert) implements ValueObject {
    private static final Pattern TELEFONNUMMER_PATTERN = Pattern.compile("^[+]?[0-9-\\s]+$");

    /**
     * Der Konstruktor für die TelefonNummer Klasse.
     * Er validiert die Telefonnummer und wirft eine IllegalArgumentException, wenn sie ungültig ist.
     * @param wert Die zu validierende Telefonnummer.
     * @throws NullPointerException Wenn der Wert null ist.
     * @throws IllegalArgumentException Wenn der Wert andere Zeichen als Zahlen, Leerzeichen, Bindestriche oder Pluszeichen enthält.
     */
    public TelefonNummer(String wert) {
        Objects.requireNonNull(wert, "Die Telefonnummer darf nicht leer sein.");
        if (!TELEFONNUMMER_PATTERN.matcher(wert).matches()) {
            throw new IllegalArgumentException("Die Telefonnummer enthält ungültige Zeichen.");
        }
        this.wert = wert;
    }
}