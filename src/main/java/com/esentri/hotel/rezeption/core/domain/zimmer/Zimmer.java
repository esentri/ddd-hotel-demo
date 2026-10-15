package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Ein Zimmer des Hotels mit seinen Merkmalen. Nur ein bezugsfertiges Zimmer kann belegt werden,
 * und nur fuer eine Buchung, deren gewuenschte Zimmerkategorie es mindestens erfuellt.
 */
@Getter
public final class Zimmer extends AggregateRootBase<Zimmer.ZimmerId> {

    public record ZimmerId(@NotNull UUID value) implements Identity<UUID> {
    }

    private final ZimmerId id;

    @NotNull
    private final Zimmernummer zimmernummer;

    @NotNull
    private final Zimmerkategorie zimmerkategorie;

    private final int etage;

    @NotNull
    private final Blickrichtung blickrichtung;

    private final boolean balkon;

    private final boolean barrierefrei;

    @NotNull
    private final Bettart bettart;

    private final boolean raucherzimmer;

    @NotNull
    private Zimmerstatus status;

    private Buchung.BuchungId belegtDurch;

    @Builder
    public Zimmer(long concurrencyVersion,
                  ZimmerId id,
                  Zimmernummer zimmernummer,
                  Zimmerkategorie zimmerkategorie,
                  int etage,
                  Blickrichtung blickrichtung,
                  boolean balkon,
                  boolean barrierefrei,
                  Bettart bettart,
                  boolean raucherzimmer,
                  Zimmerstatus status,
                  Buchung.BuchungId belegtDurch) {
        super(concurrencyVersion);
        this.id = id;
        this.zimmernummer = zimmernummer;
        this.zimmerkategorie = zimmerkategorie;
        this.etage = etage;
        this.blickrichtung = blickrichtung;
        this.balkon = balkon;
        this.barrierefrei = barrierefrei;
        this.bettart = bettart;
        this.raucherzimmer = raucherzimmer;
        this.status = status;
        this.belegtDurch = belegtDurch;
    }

    /**
     * Belegt das Zimmer im Rahmen des Check-ins mit einer Buchung.
     */
    public void belege(Buchung.BuchungId buchungId, Zimmerkategorie gewuenschteZimmerkategorie) {
        DomainAssertions.isNotNull(buchungId, "Ein Zimmer wird immer fuer eine Buchung belegt.");
        DomainAssertions.isNotNull(gewuenschteZimmerkategorie, "Die gewuenschte Zimmerkategorie ist erforderlich.");
        DomainAssertions.equals(status, Zimmerstatus.BEZUGSFERTIG,
            "Zimmer " + zimmernummer.value() + " ist nicht bezugsfertig.");
        DomainAssertions.isTrue(zimmerkategorie.istGleichOderHoeherAls(gewuenschteZimmerkategorie),
            "Zimmer " + zimmernummer.value() + " (" + zimmerkategorie + ") erfuellt die gewuenschte Zimmerkategorie "
                + gewuenschteZimmerkategorie + " nicht.");
        status = Zimmerstatus.BELEGT;
        belegtDurch = buchungId;
    }

    @Override
    public void validate() {
        if (status != null) {
            DomainAssertions.isTrue((status == Zimmerstatus.BELEGT) == (belegtDurch != null),
                "Ein Zimmer ist genau dann einer Buchung zugeordnet, wenn es belegt ist.");
        }
    }
}
