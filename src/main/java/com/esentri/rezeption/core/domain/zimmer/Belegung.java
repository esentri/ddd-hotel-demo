package com.esentri.rezeption.core.domain.zimmer;

import nitrox.dlc.domain.types.ValueObject;

import java.time.LocalDate;

/**
 * Diese Klasse repräsentiert die Belegung eines Zimmers in einem bestimmten Zeitraum .
 * Eine Belegung besitzt ein Startdatum (von), ein Enddatum (bis) und einen Belegungstypen ({@link BelegungTyp}).
 * Bei der Erzeugung einer Instanz dieser Klasse wird sichergestellt, dass alle Attribute gültig sind (d.h. nicht null sind und das Startdatum liegt vor dem Enddatum).
 * <p>
 * Die Klasse verfügt über Methoden zur Überprüfung, ob ein bestimmter Zeitraum mit dem Belegungszeitraum überschneidet, sowie ob ein bestimmtes Datum innerhalb des Belegungszeitraums liegt.
 *
 * @author Mario Herb
 * @see ValueObject
 * @see BelegungTyp
 */
public record Belegung(LocalDate von, LocalDate bis, BelegungTyp belegungTyp) implements ValueObject {

    public Belegung {
        if (von == null) {
            throw new IllegalStateException("Der Belegungsbeginn darf nie null sein");
        }
        if (bis == null) {
            throw new IllegalStateException("Das Belegungsende darf nie null sein");
        }
        if (von.isAfter(bis)) {
            throw new IllegalStateException("Der Belegungsbeginn muss vor dem Belegungsende liegen");
        }
        if (belegungTyp == null) {
            throw new IllegalStateException("Der BelegungTyp darf nie null sein");
        }
    }

    /**
     * Überprüft, ob ein gegebener Zeitraum mit dem Belegungszeitraum dieses Objekts überschneidet.
     *
     * @param probeVon das Startdatum des Zeitraums, der geprüft werden soll
     * @param probeBis das Enddatum des Zeitraums, der geprüft werden soll
     * @return {@code true} falls eine Überschneidung besteht, {@code false} falls keine Überschneidung besteht
     */
    public boolean ueberschneidetZeitraum(LocalDate probeVon, LocalDate probeBis){
        return probeVon.isBefore(bis) && probeBis.isAfter(von);
    }

    /**
     * Überprüft, ob das gegebene Datum innerhalb des Belegungszeitraums dieses Objekts liegt.
     *
     * @param datum das Datum, das geprüft werden soll
     * @return {@code true} falls das Datum innerhalb des Belegungszeitraums liegt, {@code false} falls es außerhalb des Belegungszeitraums liegt
     */
    public boolean istBelegtAm(LocalDate datum){
        return ueberschneidetZeitraum(datum, datum) || von.isEqual(datum) || bis.isEqual(datum);
    }
}