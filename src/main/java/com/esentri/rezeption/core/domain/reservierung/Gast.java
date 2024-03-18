package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.Adresse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nitrox.dlc.domain.types.Entity;
import nitrox.dlc.domain.types.Identity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

/**
 * Diese Klasse repräsentiert einen Gast innerhalb einer Reservierung.
 * Jeder Gast hat eine eindeutige ID, Vorname, Nachname, Geburtsdatum, Telefonnummer,
 * Emailadresse und eine Heimadresse.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Gast implements Entity<Gast.GastId> {

    public record GastId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final GastId id;

    private String vorname;

    private String nachname;

    private LocalDate geburtsDatum;

    private TelefonNummer telefonNummer;

    private EmailAdresse emailAdresse;

    private Adresse heimAdresse;

    private long concurrencyVersion;

    @Builder
    public Gast(GastId id, String vorname, String nachname, LocalDate geburtsDatum,
                TelefonNummer telefonNummer, EmailAdresse emailAdresse, Adresse heimAdresse) {
        Objects.requireNonNull(id, "Die GastId darf nicht null sein.");
        validateName(vorname, "Vorname");
        validateName(nachname, "Nachname");
        validateAlter(geburtsDatum);
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsDatum = geburtsDatum;
        this.telefonNummer = telefonNummer;
        this.emailAdresse = emailAdresse;
        this.heimAdresse = heimAdresse;
    }

    Gast setVorname(String vorname) {
        validateName(vorname, "Vorname");
        this.vorname = vorname;
        return this;
    }

    Gast setNachname(String nachname) {
        validateName(nachname, "Nachname");
        this.nachname = nachname;
        return this;
    }

    Gast setGeburtsDatum(LocalDate geburtsDatum) {
        validateAlter(geburtsDatum);
        this.geburtsDatum = geburtsDatum;
        return this;
    }

    Gast setTelefonNummer(TelefonNummer telefonNummer) {
        this.telefonNummer = telefonNummer;
        return this;
    }

    Gast setEmailAdresse(EmailAdresse emailAdresse) {
        this.emailAdresse = emailAdresse;
        return this;
    }

    Gast setHeimAdresse(Adresse heimAdresse) {
        this.heimAdresse = heimAdresse;
        return this;
    }

    private void validateName(String name, String fieldName) {
        Objects.requireNonNull(name, String.format("%s muss angegeben werden", fieldName));

        if (name.length() > 200) {
            throw new IllegalArgumentException(String.format("%s muss weniger als 200 Zeichen haben!", fieldName));
        }
    }

    private void validateAlter(LocalDate geburtsDatum) {
        if (geburtsDatum != null) {
            LocalDate today = LocalDate.now();
            int age = Period.between(geburtsDatum, today).getYears();

            if (age < 16) {
                throw new IllegalArgumentException("Gast muss mindestens 16 Jahre alt sein");
            }
        }
    }

    @Override
    public GastId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}
