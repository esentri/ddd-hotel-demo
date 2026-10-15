package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Zentrales Konzept der Rezeption: die Buchung eines Gastes fuer einen Buchungszeitraum
 * mit gewuenschter Zimmerkategorie. Eine Buchung wird hoechstens einmal eingecheckt, und nur wenn
 * die Adresse des Gastes bekannt ist und er das Mindestalter erreicht hat; eine eingecheckte Buchung
 * kann nicht mehr storniert werden.
 */
@Getter
public final class Buchung extends AggregateRootBase<Buchung.BuchungId> {

    public static final int MINDESTALTER_CHECKIN = 16;

    public record BuchungId(@NotNull UUID value) implements Identity<UUID> {
    }

    private final BuchungId id;

    @NotNull
    private final Gast gast;

    @NotNull
    private final Buchungszeitraum buchungszeitraum;

    @NotNull
    private final Zimmerkategorie gewuenschteZimmerkategorie;

    @NotNull
    private Buchungsstatus status;

    @Builder
    public Buchung(long concurrencyVersion,
                   BuchungId id,
                   Gast gast,
                   Buchungszeitraum buchungszeitraum,
                   Zimmerkategorie gewuenschteZimmerkategorie,
                   Buchungsstatus status) {
        super(concurrencyVersion);
        this.id = id;
        this.gast = gast;
        this.buchungszeitraum = buchungszeitraum;
        this.gewuenschteZimmerkategorie = gewuenschteZimmerkategorie;
        this.status = status;
    }

    /**
     * Walk-In: ein Gast kommt ohne vorherige Buchung an den Tresen. Die Buchung entsteht
     * vor Ort und wird am Tag der Anreise sofort eingecheckt.
     */
    @FactoryMethod
    public static Buchung walkIn(Gast gast, Buchungszeitraum buchungszeitraum, Zimmerkategorie gewuenschteZimmerkategorie) {
        var buchung = new Buchung(
            0L,
            new BuchungId(UUID.randomUUID()),
            gast,
            buchungszeitraum,
            gewuenschteZimmerkategorie,
            Buchungsstatus.RESERVIERT);
        buchung.checkeEin(buchungszeitraum.anreise());
        return buchung;
    }

    public void checkeEin(LocalDate checkinDatum) {
        DomainAssertions.isFalse(status == Buchungsstatus.EINGECHECKT,
            "Die Buchung ist bereits eingecheckt.");
        DomainAssertions.isFalse(status == Buchungsstatus.STORNIERT,
            "Eine stornierte Buchung kann nicht eingecheckt werden.");
        DomainAssertions.isNotNull(checkinDatum, "Das Check-in-Datum ist erforderlich.");
        DomainAssertions.isNotNull(gast.getAdresse(), "Fuer den Check-in wird die Adresse des Gastes benoetigt.");
        DomainAssertions.isTrue(gast.alterAm(checkinDatum) >= MINDESTALTER_CHECKIN,
            "Der Gast muss zum Check-in mindestens " + MINDESTALTER_CHECKIN + " Jahre alt sein.");
        status = Buchungsstatus.EINGECHECKT;
    }

    public void storniere() {
        DomainAssertions.isFalse(status == Buchungsstatus.EINGECHECKT,
            "Eine eingecheckte Buchung kann nicht storniert werden.");
        DomainAssertions.isFalse(status == Buchungsstatus.STORNIERT,
            "Die Buchung ist bereits storniert.");
        status = Buchungsstatus.STORNIERT;
    }
}
