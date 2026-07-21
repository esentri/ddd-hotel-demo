package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;


public class HauptGast implements Entity<HauptGastId> {

    @NotNull(message = "HauptGastId darf nicht null sein")
    private final HauptGastId id;
    private String vorname;
    private String nachname;
    private LocalDate geburtsdatum;
    private long concurrencyVersion;

    @Builder
    public HauptGast(
            HauptGastId id, String vorname, String nachname, LocalDate geburtsdatum, long concurrencyVersion
    ) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
        this.concurrencyVersion = concurrencyVersion;
    }

    @Override
    public HauptGastId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void vervollstaendigeDaten(String vorname, String nachname, LocalDate geburtsdatum) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HauptGast hauptGast = (HauptGast) o;
        return Objects.equals(id, hauptGast.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
