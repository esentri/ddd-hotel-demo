package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;

import java.time.LocalDate;
import java.util.Objects;

public record AktualisiereGastdaten(
    BuchungsId buchungsId,
    String vorname,
    String nachname,
    LocalDate geburtsdatum
) implements DomainCommand {

    public AktualisiereGastdaten {
        Objects.requireNonNull(buchungsId, "Die BuchungsId darf nicht null sein");
        Objects.requireNonNull(vorname, "Der Vorname darf nicht null sein");
        Objects.requireNonNull(nachname, "Der Nachname darf nicht null sein");
        Objects.requireNonNull(geburtsdatum, "Das Geburtsdatum darf nicht null sein");
    }
}
