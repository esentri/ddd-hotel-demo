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
import io.domainlifecycles.domain.types.ValueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Repräsentiert eine E-Mail-Adresse in einem gängigen Format.
 * Stellt sicher, dass nur gültige E-Mail-Adressen gespeichert werden.
 *
 * @author Mario Herb
 */
public record EmailAdresse(String adresse) implements ValueObject {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Erstellt eine neue Instanz der EmailAdresse.
     * Überprüft die Eingabe auf Null und das Muster einer E-Mail-Adresse.
     *
     * @param adresse die zu validierende E-Mail-Adresse. Darf nicht null sein und muss dem gängigen E-Mail-Format entsprechen.
     * @throws NullPointerException wenn die E-Mail-Adresse null ist.
     * @throws IllegalArgumentException wenn die E-Mail-Adresse nicht dem gängigen E-Mail-Format entspricht.
     */
    public EmailAdresse(String adresse) {
        Objects.requireNonNull(adresse, "Die E-Mail-Adresse darf nicht leer sein.");
        if (!EMAIL_PATTERN.matcher(adresse).matches()) {
            throw new IllegalArgumentException("Die E-Mail-Adresse hat ein ungültiges Format.");
        }
        this.adresse = adresse;
    }
}
