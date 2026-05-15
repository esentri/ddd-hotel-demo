package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.ZimmerId;
import io.domainlifecycles.domain.types.AggregateRoot;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

public class Buchung implements AggregateRoot<BuchungsId> {

    private final BuchungsId id;
    private final HauptGast hauptGast;
    private final Zeitraum belegungszeitraum;
    private ZimmerId zimmerId;
    private BuchungsStatus status;

    private Buchung(BuchungsId id, HauptGast hauptGast, Zeitraum belegungszeitraum, BuchungsStatus status) {
        this.id = Objects.requireNonNull(id, "Die BuchungsId darf nicht null sein");
        this.hauptGast = Objects.requireNonNull(hauptGast, "Der HauptGast darf nicht null sein");
        this.belegungszeitraum = Objects.requireNonNull(belegungszeitraum, "Der Belegungszeitraum darf nicht null sein");
        this.status = Objects.requireNonNull(status, "Der Status darf nicht null sein");
    }

    public static Buchung neueBuchung(BuchungsId id, HauptGast hauptGast, Zeitraum belegungszeitraum) {
        return new Buchung(id, hauptGast, belegungszeitraum, BuchungsStatus.RESERVIERT);
    }

    public void checkeEin(ZimmerId zimmerId) {
        if (this.status == BuchungsStatus.STORNIERT) {
            throw new IllegalStateException("Eine stornierte Buchung kann nicht eingecheckt werden");
        }
        if (this.status != BuchungsStatus.RESERVIERT) {
            throw new IllegalStateException("Check-in ist nur aus dem Status RESERVIERT erlaubt");
        }
        Objects.requireNonNull(zimmerId, "Beim Check-in muss eine ZimmerId vorhanden sein");

        // Invariante: HauptGast mindestens 16 Jahre alt zum checkInDatum
        if (hauptGast.getGeburtsdatum() == null) {
            throw new IllegalStateException("Geburtsdatum des Hauptgastes muss zum Check-in bekannt sein");
        }

        if (Period.between(hauptGast.getGeburtsdatum(), belegungszeitraum.checkInDatum()).getYears() < 16) {
             throw new IllegalStateException("Der Hauptgast muss zum Check-in mindestens 16 Jahre alt sein");
        }

        this.zimmerId = zimmerId;
        this.status = BuchungsStatus.EINGECHECKT;
    }

    public void storniere() {
        if (this.status == BuchungsStatus.EINGECHECKT || this.status == BuchungsStatus.AUSGECHECKT) {
            throw new IllegalStateException("Eine eingecheckte Buchung kann nicht mehr storniert werden");
        }
        this.status = BuchungsStatus.STORNIERT;
    }

    @Publishes(domainEventTypes = {GastAusgecheckt.class})
    public void checkeAus() {
        if (this.status != BuchungsStatus.EINGECHECKT) {
            throw new IllegalStateException("Check-out ist nur fuer eingecheckte Buchungen moeglich");
        }
        this.status = BuchungsStatus.AUSGECHECKT;
        DomainEvents.publish(new GastAusgecheckt(
            this.id,
            Instant.now()
        ));
    }

    public void aktualisiereGastdaten(String vorname, String nachname, LocalDate geburtsdatum) {
        this.hauptGast.vervollstaendigeDaten(vorname, nachname, geburtsdatum);
    }

    @Override
    public BuchungsId id() {
        return id;
    }

    @Override
    public long concurrencyVersion() {
        return 0;
    }

    public HauptGast getHauptGast() {
        return hauptGast;
    }

    public Zeitraum getBelegungszeitraum() {
        return belegungszeitraum;
    }

    public Optional<ZimmerId> getZimmerId() {
        return Optional.ofNullable(zimmerId);
    }

    public BuchungsStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Buchung buchung = (Buchung) o;
        return Objects.equals(id, buchung.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
