package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.EntityBase;
import io.domainlifecycles.validation.extend.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Der Gast einer Buchung. Buchungen kommen ueber verschiedene Kanaele und bringen nicht immer
 * vollstaendige Gastdaten mit - Pflicht ist nur der Nachname. Adresse und Geburtsdatum koennen fehlen,
 * werden aber spaetestens beim Check-in gebraucht.
 */
@Getter
public final class Gast extends EntityBase<Gast.GastId> {

    public record GastId(@NotNull UUID value) implements Identity<UUID> {
    }

    private final GastId id;

    @Size(max = 100)
    private final String vorname;

    @NotBlank
    @Size(max = 100)
    private final String nachname;

    private final Adresse adresse;

    @PastOrPresent
    private final LocalDate geburtsdatum;

    @Builder
    public Gast(long concurrencyVersion, GastId id, String vorname, String nachname, Adresse adresse, LocalDate geburtsdatum) {
        super(concurrencyVersion);
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
        this.geburtsdatum = geburtsdatum;
    }

    @FactoryMethod
    public static Gast neu(String vorname, String nachname, Adresse adresse, LocalDate geburtsdatum) {
        return new Gast(0L, new GastId(UUID.randomUUID()), vorname, nachname, adresse, geburtsdatum);
    }

    /**
     * Alter in vollendeten Lebensjahren am angegebenen Tag.
     */
    @Query
    public int alterAm(LocalDate stichtag) {
        DomainAssertions.isNotNull(geburtsdatum, "Das Geburtsdatum des Gastes ist nicht bekannt, das Alter kann nicht ermittelt werden.");
        return Period.between(geburtsdatum, stichtag).getYears();
    }
}
