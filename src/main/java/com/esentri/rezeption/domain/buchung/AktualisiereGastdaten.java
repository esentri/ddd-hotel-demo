package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AktualisiereGastdaten(
    @NotNull(message = "Die BuchungsId darf nicht null sein.") BuchungsId buchungsId,
    @NotNull(message = "Der Vorname darf nicht null sein.") String vorname,
    @NotNull(message = "Der Nachname darf nicht null sein.") String nachname,
    @NotNull(message = "Das Geburtsdatum darf nicht null sein.") LocalDate geburtsdatum
) implements DomainCommand {}
