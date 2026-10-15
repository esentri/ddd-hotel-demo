package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Zentrales Konzept der Rezeption: die Buchung eines Gastes fuer einen Buchungszeitraum
 * mit gewuenschter Zimmerkategorie. Eine Buchung wird hoechstens einmal eingecheckt;
 * eine eingecheckte Buchung kann nicht mehr storniert werden.
 */
@Getter
public final class Buchung extends AggregateRootBase<Buchung.BuchungId> {

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
     * vor Ort und wird sofort eingecheckt.
     */
    @FactoryMethod
    public static Buchung walkIn(Gast gast, Buchungszeitraum buchungszeitraum, Zimmerkategorie gewuenschteZimmerkategorie) {
        var buchung = new Buchung(
            0L,
            new BuchungId(UUID.randomUUID()),
            gast,
            buchungszeitraum,
            gewuenschteZimmerkategorie,
            Buchungsstatus.ANGELEGT);
        buchung.checkeEin();
        return buchung;
    }

    public void checkeEin() {
        DomainAssertions.isFalse(status == Buchungsstatus.EINGECHECKT,
            "Die Buchung ist bereits eingecheckt.");
        DomainAssertions.isFalse(status == Buchungsstatus.STORNIERT,
            "Eine stornierte Buchung kann nicht eingecheckt werden.");
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
