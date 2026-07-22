package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import io.domainlifecycles.domain.types.AggregateRoot;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

public class Buchung implements AggregateRoot<BuchungsId> {

    @NotNull(message = "Die BuchungsId darf nicht null sein")
    private final BuchungsId id;
    @NotNull(message = "Der HauptGast darf nicht null sein")
    private final HauptGast hauptGast;
    @NotNull(message = "Der Belegungszeitraum darf nicht null sein")
    private final Zeitraum belegungszeitraum;
    private ZimmerId zimmerId;
    @NotNull(message = "Der Status darf nicht null sein")
    private BuchungsStatus status;
    private long concurrencyVersion;

    @Builder
    private Buchung(
            BuchungsId id,
            HauptGast hauptGast,
            Zeitraum belegungszeitraum,
            ZimmerId zimmerId,
            BuchungsStatus status,
            long concurrencyVersion) {
        this.id = id;
        this.hauptGast = hauptGast;
        this.belegungszeitraum = belegungszeitraum;
        this.zimmerId = zimmerId;
        this.status = status;
        this.concurrencyVersion = concurrencyVersion;
    }

    public static Buchung neueBuchung(BuchungsId id, HauptGast hauptGast, Zeitraum belegungszeitraum) {
        return new Buchung(id, hauptGast, belegungszeitraum, null, BuchungsStatus.RESERVIERT, 0);
    }

    public void checkeEin(@NotNull(message = "Beim Check-in muss eine ZimmerId vorhanden sein") ZimmerId zimmerId) {
        if (this.status == BuchungsStatus.STORNIERT) {
            throw new IllegalStateException("Eine stornierte Buchung kann nicht eingecheckt werden");
        }
        if (this.status != BuchungsStatus.RESERVIERT) {
            throw new IllegalStateException("Check-in ist nur aus dem Status RESERVIERT erlaubt");
        }

        // Invariante: HauptGast mindestens 16 Jahre alt zum checkInDatum
        if (hauptGast.getGeburtsdatum() == null) {
            throw new IllegalStateException("Geburtsdatum des Hauptgastes muss zum Check-in bekannt sein");
        }

        if (Period.between(hauptGast.getGeburtsdatum(), belegungszeitraum.start()).getYears() < 16) {
             throw new IllegalStateException("Der Hauptgast muss zum Check-in mindestens 16 Jahre alt sein");
        }

        this.zimmerId = zimmerId;
        this.status = BuchungsStatus.EINGECHECKT;
    }

    public void storniere() {
        if (this.status == BuchungsStatus.EINGECHECKT || this.status == BuchungsStatus.AUSGECHECKT) {
            throw new IllegalStateException("Buchung kann nicht mehr storniert werden");
        }
        this.status = BuchungsStatus.STORNIERT;
    }

    @Publishes(domainEventTypes = {GastAusgecheckt.class})
    public void checkeAus() {
        if (this.status != BuchungsStatus.EINGECHECKT) {
            throw new IllegalStateException("Check-out ist nur fuer eingecheckte Buchungen moeglich");
        }
        if (this.zimmerId == null) {
            throw new IllegalStateException("Check-out nicht moeglich: Keine ZimmerId an der Buchung hinterlegt");
        }
        this.status = BuchungsStatus.AUSGECHECKT;
        DomainEvents.publish(new GastAusgecheckt(
            this.id,
            this.zimmerId,
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
        return this.concurrencyVersion;
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
