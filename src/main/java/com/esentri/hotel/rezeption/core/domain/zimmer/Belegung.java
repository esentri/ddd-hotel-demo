package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.EntityBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Belegung eines Zimmers fuer einen Zeitraum: durch einen eingecheckten Gast oder durch Wartungsarbeiten.
 * Nur ueber das {@link Zimmer} veraenderbar.
 */
public final class Belegung extends EntityBase<Belegung.BelegungId> {

    public record BelegungId(@NotNull UUID value) implements Identity<UUID> {
    }

    @NotNull
    private final BelegungId id;

    @NotNull
    private final Belegungsart art;

    @NotNull
    private Belegungszeitraum zeitraum;

    private final Buchung.BuchungsId buchungsId;

    private final Wartungsgrund wartungsgrund;

    @Builder
    public Belegung(long concurrencyVersion, BelegungId id, Belegungsart art, Belegungszeitraum zeitraum,
                    Buchung.BuchungsId buchungsId, Wartungsgrund wartungsgrund) {
        super(concurrencyVersion);
        this.id = id;
        this.art = art;
        this.zeitraum = zeitraum;
        this.buchungsId = buchungsId;
        this.wartungsgrund = wartungsgrund;
    }

    @FactoryMethod
    static Belegung durchGast(Buchung.BuchungsId buchungsId, Belegungszeitraum zeitraum) {
        return new Belegung(0, neueId(), Belegungsart.GAST, zeitraum, buchungsId, null);
    }

    @FactoryMethod
    static Belegung durchWartung(Wartungsgrund wartungsgrund, Belegungszeitraum zeitraum) {
        return new Belegung(0, neueId(), Belegungsart.WARTUNG, zeitraum, null, wartungsgrund);
    }

    private static BelegungId neueId() {
        return new BelegungId(UUID.randomUUID());
    }

    @Override
    public void validate() {
        DomainAssertions.isTrue((art == Belegungsart.GAST) == (buchungsId != null),
            "Eine Belegung durch einen Gast verweist genau dann auf eine Buchung.");
        DomainAssertions.isTrue((art == Belegungsart.WARTUNG) == (wartungsgrund != null),
            "Eine Belegung durch Wartung hat genau dann einen Wartungsgrund.");
    }

    void endetAm(LocalDate tag) {
        this.zeitraum = new Belegungszeitraum(zeitraum.von(), tag);
    }

    public Belegungsart getArt() {
        return art;
    }

    public Belegungszeitraum getZeitraum() {
        return zeitraum;
    }

    public Optional<Buchung.BuchungsId> getBuchungsId() {
        return Optional.ofNullable(buchungsId);
    }

    public Optional<Wartungsgrund> getWartungsgrund() {
        return Optional.ofNullable(wartungsgrund);
    }
}
