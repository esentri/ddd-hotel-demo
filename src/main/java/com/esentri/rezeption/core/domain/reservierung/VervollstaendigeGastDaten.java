package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.Adresse;
import nitrox.dlc.domain.types.DomainCommand;

import java.time.LocalDate;

/**
 * Dieser Record repräsentiert die Aktion, die Gastdaten einer bestimmten Reservierung zu vervollständigen.
 * Die Aktion kann durchgeführt werden, indem alle erforderlichen Daten des Gastes bereitgestellt werden,
 * wie Vorname, Nachname, Geburtsdatum, Email-Adresse, Telefonnummer und Heimadresse.
 * Dieses Command wird zur Modifikation des zugehörigen Aggregates, Reservierung, verwendet.
 *
 * @author Mario Herb
 */
public record VervollstaendigeGastDaten(
        Reservierung.ReservierungsNummer reservierungsNummer,
        String vorname,
        String nachname,
        LocalDate geburtsdatum,
        EmailAdresse emailAdresse,
        TelefonNummer telefonNummer,
        Adresse heimAdresse
) implements DomainCommand {}