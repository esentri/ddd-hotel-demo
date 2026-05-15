package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hauptgast implements Entity<HauptgastId> {

    @EqualsAndHashCode.Include
    private final HauptgastId id;

    private String vorname;
    private String nachname;
    private LocalDate geburtsdatum;

    @Builder
    public Hauptgast(HauptgastId id, String vorname, String nachname, LocalDate geburtsdatum) {
        if (id == null) {
            throw new IllegalArgumentException("HauptgastId darf nicht null sein.");
        }
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
    }

    public void vervollstaendigeDaten(String vorname, String nachname, LocalDate geburtsdatum) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public HauptgastId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }
}
