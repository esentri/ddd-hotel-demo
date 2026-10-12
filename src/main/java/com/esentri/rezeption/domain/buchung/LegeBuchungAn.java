package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Domain Command zum Anlegen einer Buchung fuer einen Hauptgast und einen Belegungszeitraum.
 */
public record LegeBuchungAn(
    @NotNull(message = "Der Vorname darf nicht null sein.") String vorname,
    @NotNull(message = "Der Nachname darf nicht null sein.") String nachname,
    LocalDate geburtsdatum,
    @NotNull(message = "Der Belegungszeitraum darf nicht null sein.") Zeitraum belegungszeitraum
) implements DomainCommand {}
