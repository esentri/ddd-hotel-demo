package com.esentri.rezeption.domain.model.buchung;

import com.esentri.rezeption.domain.model.Zeitraum;
import com.esentri.rezeption.domain.model.zimmer.ZimmerId;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.AggregateRoot;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Buchung implements AggregateRoot<BuchungId> {

    public enum Status {
        RESERVIERT, EINGECHECKT, AUSGECHECKT, STORNIERT
    }

    @EqualsAndHashCode.Include
    @NotNull
    private BuchungId id;

    @NotNull
    private Gast gast;

    @NotNull
    private Zeitraum zeitraum;

    @NotNull
    private Status status;

    private ZimmerId zugewiesenesZimmerId;

    public Buchung(BuchungId id, Gast gast, Zeitraum zeitraum) {
        this.id = id;
        this.gast = gast;
        this.zeitraum = zeitraum;
        this.status = Status.RESERVIERT;
    }

    public void checkIn(ZimmerId zimmerId) {
        DomainAssertions.isTrue(status == Status.RESERVIERT, "Check-In ist nur für reservierte Buchungen möglich.");
        gast.validiereVolljährigkeit();
        this.zugewiesenesZimmerId = zimmerId;
        this.status = Status.EINGECHECKT;
    }

    public void checkout() {
        DomainAssertions.isTrue(status == Status.EINGECHECKT, "Checkout ist nur für eingecheckte Buchungen möglich.");
        this.status = Status.AUSGECHECKT;
    }

    public void storniere() {
        DomainAssertions.isTrue(status == Status.RESERVIERT, "Nur reservierte Buchungen können storniert werden. Eingecheckte Buchungen sind endgültig.");
        this.status = Status.STORNIERT;
    }

    @Override
    public BuchungId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }

    public Optional<ZimmerId> getZugewiesenesZimmerId() {
        return Optional.ofNullable(zugewiesenesZimmerId);
    }
}
