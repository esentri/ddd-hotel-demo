package com.esentri.rezeption.domain.model.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Gast implements Entity<GastId> {

    @EqualsAndHashCode.Include
    @NotNull
    private GastId id;

    @NotBlank
    private String vorname;
    @NotBlank
    private String nachname;
    @NotNull
    private LocalDate geburtsdatum;

    public Gast(GastId id, String vorname, String nachname, LocalDate geburtsdatum) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public GastId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }

    public boolean istVolljährig() {
        return Period.between(geburtsdatum, LocalDate.now()).getYears() >= 16;
    }

    public void validiereVolljährigkeit() {
        DomainAssertions.isTrue(istVolljährig(), "Der Gast muss mindestens 16 Jahre alt sein.");
    }
}
