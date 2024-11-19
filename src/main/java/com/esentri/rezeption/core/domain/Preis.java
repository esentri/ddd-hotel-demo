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

import io.domainlifecycles.domain.types.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Dies ist die Preis-Klasse, die zur Darstellung und Manipulation von Preisinformationen dient.
 *
 * @author Mario Herb
 */
public record Preis(BigDecimal betrag) implements ValueObject {

    /**
     * Null-Preis-Konstante.
     */
    public static Preis NULL = new Preis(BigDecimal.ZERO);

    /**
     * Konstruktor zur Initialisierung des Preises.
     * Wirft eine IllegalArgumentException, wenn der übergebene Betrag kleiner Null ist
     * oder mehr als zwei Nachkommastellen hat.
     *
     * @param betrag der Preis-Betrag.
     */
    public Preis(BigDecimal betrag) {
        if (betrag.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preis muss größer 0 sein!");
        }
        if (betrag.scale() > 2) {
            throw new IllegalArgumentException("Preis darf nur 2 Nachkommastellen besitzen!");
        }
        this.betrag = betrag.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Methode zur Addition dieses Preises mit einem anderen Preis.
     * Gibt ein neues Preis-Objekt mit der Summe der beiden Beträge zurück.
     *
     * @param anderer der andere Preis.
     * @return ein neues Preis-Objekt mit dem addierten Betrag.
     */
    public Preis addiere(Preis anderer) {
        if(anderer!=null) {
            BigDecimal newBetrag = this.betrag.add(anderer.betrag);
            return new Preis(newBetrag);
        }
        return this;
    }
}