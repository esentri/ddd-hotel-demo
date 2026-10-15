package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
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
 * die Adresse des Gastes bekannt ist und er das Mindestalter erreicht hat; beim Check-in wird ihr ein Zimmer
 * zugewiesen. Nur eine eingecheckte Buchung kann ausgecheckt werden. Storniert werden kann nur eine
 * reservierte Buchung.
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

    private Zimmer.ZimmerId zugewiesenesZimmer;

    @Builder
    public Buchung(long concurrencyVersion,
                   BuchungId id,
                   Gast gast,
                   Buchungszeitraum buchungszeitraum,
                   Zimmerkategorie gewuenschteZimmerkategorie,
                   Buchungsstatus status,
                   Zimmer.ZimmerId zugewiesenesZimmer) {
        super(concurrencyVersion);
        this.id = id;
        this.gast = gast;
        this.buchungszeitraum = buchungszeitraum;
        this.gewuenschteZimmerkategorie = gewuenschteZimmerkategorie;
        this.status = status;
        this.zugewiesenesZimmer = zugewiesenesZimmer;
    }

    /**
     * Walk-In: ein Gast kommt ohne vorherige Buchung an den Tresen. Die Buchung entsteht vor Ort und wird
     * direkt im Anschluss eingecheckt (siehe {@link CheckinService}).
     */
    @FactoryMethod
    public static Buchung walkIn(Gast gast, Buchungszeitraum buchungszeitraum, Zimmerkategorie gewuenschteZimmerkategorie) {
        return new Buchung(
            0L,
            new BuchungId(UUID.randomUUID()),
            gast,
            buchungszeitraum,
            gewuenschteZimmerkategorie,
            Buchungsstatus.RESERVIERT,
            null);
    }

    /**
     * Checkt die Buchung ein und haelt das zugewiesene Zimmer fest. Dass das Zimmer bezugsfertig ist und zur
     * gewuenschten Zimmerkategorie passt, prueft das Zimmer selbst ({@link Zimmer#belege}).
     */
    public void checkeEin(LocalDate checkinDatum, Zimmer.ZimmerId zimmer) {
        DomainAssertions.isFalse(status == Buchungsstatus.EINGECHECKT || status == Buchungsstatus.AUSGECHECKT,
            "Die Buchung ist bereits eingecheckt.");
        DomainAssertions.isFalse(status == Buchungsstatus.STORNIERT,
            "Eine stornierte Buchung kann nicht eingecheckt werden.");
        DomainAssertions.isNotNull(checkinDatum, "Das Check-in-Datum ist erforderlich.");
        DomainAssertions.isNotNull(zimmer, "Beim Check-in muss ein Zimmer zugewiesen werden.");
        DomainAssertions.isNotNull(gast.getAdresse(), "Fuer den Check-in wird die Adresse des Gastes benoetigt.");
        DomainAssertions.isTrue(gast.alterAm(checkinDatum) >= MINDESTALTER_CHECKIN,
            "Der Gast muss zum Check-in mindestens " + MINDESTALTER_CHECKIN + " Jahre alt sein.");
        status = Buchungsstatus.EINGECHECKT;
        zugewiesenesZimmer = zimmer;
    }

    /**
     * Checkt die Buchung aus. Das zugewiesene Zimmer bleibt an der Buchung vermerkt; das Zimmer selbst wird
     * geraeumt ({@link Zimmer#raeume}).
     */
    public void checkeAus() {
        DomainAssertions.equals(status, Buchungsstatus.EINGECHECKT,
            "Nur eine eingecheckte Buchung kann ausgecheckt werden.");
        status = Buchungsstatus.AUSGECHECKT;
    }

    public void storniere() {
        DomainAssertions.isFalse(status == Buchungsstatus.EINGECHECKT || status == Buchungsstatus.AUSGECHECKT,
            "Eine eingecheckte Buchung kann nicht storniert werden.");
        DomainAssertions.isFalse(status == Buchungsstatus.STORNIERT,
            "Die Buchung ist bereits storniert.");
        status = Buchungsstatus.STORNIERT;
    }

    @Override
    public void validate() {
        if (status != null) {
            var warEingecheckt = status == Buchungsstatus.EINGECHECKT || status == Buchungsstatus.AUSGECHECKT;
            DomainAssertions.isTrue(warEingecheckt == (zugewiesenesZimmer != null),
                "Eine Buchung hat genau dann ein zugewiesenes Zimmer, wenn sie eingecheckt ist oder war.");
        }
    }
}
