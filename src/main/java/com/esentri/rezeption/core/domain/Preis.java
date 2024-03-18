package com.esentri.rezeption.core.domain;

import nitrox.dlc.domain.types.ValueObject;

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