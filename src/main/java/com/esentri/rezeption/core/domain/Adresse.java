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

package com.esentri.rezeption.core.domain;

import lombok.Builder;
import nitrox.dlc.domain.types.ValueObject;

import java.util.Objects;

/**
 * Stellt eine Adresse dar und stellt sicher, dass keines der Felder null ist.
 * Straße und Ort dürfen jeweils weniger als 200 Zeichen enthalten.
 * Hausnummer und Postleitzahl dürfen jeweils weniger als 10 Zeichen enthalten.
 *
 * @author Mario Herb
 */
@Builder
public record Adresse(String strasse, String hausnummer, String postleitzahl, String ort) implements ValueObject {

    public Adresse {
        validateField(strasse, "Straße", 200);
        validateField(hausnummer, "Hausnummer", 10);
        validateField(postleitzahl, "Postleitzahl", 10);
        validateField(ort, "Ort", 200);
    }

    private void validateField(String field, String fieldName, int maxLength) {
        Objects.requireNonNull(field, fieldName + " darf nicht null sein.");
        if (field.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " muss weniger als " + maxLength + " Zeichen enthalten.");
        }
    }
}