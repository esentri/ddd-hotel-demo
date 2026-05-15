package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.AggregateRoot;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Buchung implements AggregateRoot<BuchungsId> {

    @EqualsAndHashCode.Include
    private final BuchungsId id;

    private final Hauptgast hauptgast;
    private final Zeitraum belegungszeitraum;
    private ZimmerId zimmerId;
    private BuchungsStatus status;

    @Builder
    public Buchung(BuchungsId id, Hauptgast hauptgast, Zeitraum belegungszeitraum, ZimmerId zimmerId, BuchungsStatus status) {
        if (id == null) {
            throw new IllegalArgumentException("BuchungsId darf nicht null sein.");
        }
        if (hauptgast == null) {
            throw new IllegalArgumentException("Hauptgast darf nicht null sein.");
        }
        if (belegungszeitraum == null) {
            throw new IllegalArgumentException("Belegungszeitraum darf nicht null sein.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status darf nicht null sein.");
        }
        this.id = id;
        this.hauptgast = hauptgast;
        this.belegungszeitraum = belegungszeitraum;
        this.zimmerId = zimmerId;
        this.status = status;
    }

    public static Buchung neueBuchung(BuchungsId id, Hauptgast hauptgast, Zeitraum belegungszeitraum) {
        return Buchung.builder()
                .id(id)
                .hauptgast(hauptgast)
                .belegungszeitraum(belegungszeitraum)
                .status(BuchungsStatus.RESERVIERT)
                .build();
    }

    public void checkeEin(ZimmerId zimmerId) {
        if (this.status != BuchungsStatus.RESERVIERT) {
            if (this.status == BuchungsStatus.STORNIERT) {
                throw new IllegalStateException("Eine stornierte Buchung kann nicht eingecheckt werden.");
            }
            throw new IllegalStateException("Check-in ist nur aus dem Status RESERVIERT heraus erlaubt.");
        }

        if (zimmerId == null) {
            throw new IllegalArgumentException("Beim Check-in muss eine ZimmerId vorhanden sein.");
        }

        if (hauptgast.getVorname() == null || hauptgast.getVorname().isBlank() || hauptgast.getGeburtsdatum() == null) {
            throw new IllegalStateException("Hauptgastdaten (Vorname und Geburtsdatum) muessen zum Check-in vervollstaendigt sein.");
        }

        if (hauptgast.getGeburtsdatum().isAfter(belegungszeitraum.checkInDatum().minusYears(16))) {
            throw new IllegalStateException("Der Hauptgast muss zum Check-in mindestens 16 Jahre alt sein.");
        }

        this.zimmerId = zimmerId;
        this.status = BuchungsStatus.EINGECHECKT;
    }

    public void storniere() {
        if (this.status == BuchungsStatus.EINGECHECKT || this.status == BuchungsStatus.AUSGECHECKT) {
            throw new IllegalStateException("Eine eingecheckte Buchung kann nicht mehr storniert werden.");
        }
        this.status = BuchungsStatus.STORNIERT;
    }

    public void checkeAus() {
        if (this.status != BuchungsStatus.EINGECHECKT) {
            throw new IllegalStateException("Check-out ist nur im Status EINGECHECKT moeglich.");
        }
        this.status = BuchungsStatus.AUSGECHECKT;
    }

    public Optional<ZimmerId> getZimmerId() {
        return Optional.ofNullable(zimmerId);
    }

    @Override
    public BuchungsId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }
}
