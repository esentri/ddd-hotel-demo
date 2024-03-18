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