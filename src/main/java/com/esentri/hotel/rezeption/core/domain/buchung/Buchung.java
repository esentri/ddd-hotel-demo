package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.FactoryMethod;
import io.domainlifecycles.domain.types.Identity;
import io.domainlifecycles.domain.types.base.AggregateRootBase;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Zentrales Aggregat der Rezeption. Eine Buchung gehoert zu genau einem Hotel und bezieht sich immer auf genau einen
 * Hauptgast; das Zimmer wird erst beim CheckIn zugewiesen.
 */
public final class Buchung extends AggregateRootBase<Buchung.BuchungsId> {

    public record BuchungsId(@NotNull UUID value) implements Identity<UUID> {
    }

    @NotNull
    private final BuchungsId id;

    @NotNull
    private final HotelId hotelId;

    /** Kennung im externen Reservierungssystem; fehlt bei Buchungen, die erst beim spontanen CheckIn entstehen. */
    private final Reservierungsnummer reservierungsnummer;

    @NotNull
    private final Gast hauptgast;

    @NotNull
    private Aufenthaltszeitraum aufenthaltszeitraum;

    @NotNull
    private final Zimmerkategorie zimmerkategorie;

    /** Zugewiesenes Zimmer; bleibt nach dem Checkout als Historie erhalten. */
    private Zimmer.ZimmerId zimmerId;

    @NotNull
    private BuchungsStatus status;

    /** Tag des Checkouts; kann vor dem geplanten Abreisetag (vorzeitige Abreise) oder danach liegen. */
    private LocalDate ausgechecktAm;

    @Builder
    public Buchung(long concurrencyVersion, BuchungsId id, HotelId hotelId, Reservierungsnummer reservierungsnummer,
                   Gast hauptgast, Aufenthaltszeitraum aufenthaltszeitraum, Zimmerkategorie zimmerkategorie,
                   Zimmer.ZimmerId zimmerId, BuchungsStatus status, LocalDate ausgechecktAm) {
        super(concurrencyVersion);
        this.id = id;
        this.hotelId = hotelId;
        this.reservierungsnummer = reservierungsnummer;
        this.hauptgast = hauptgast;
        this.aufenthaltszeitraum = aufenthaltszeitraum;
        this.zimmerkategorie = zimmerkategorie;
        this.zimmerId = zimmerId;
        this.status = status;
        this.ausgechecktAm = ausgechecktAm;
    }

    @FactoryMethod
    public static Buchung uebernimmReservierung(UebernimmReservierung reservierung) {
        var hauptgast = Gast.erfasse(reservierung.name(), reservierung.email(), reservierung.telefonnummer(),
            reservierung.adresse());
        return new Buchung(0, new BuchungsId(UUID.randomUUID()), reservierung.hotelId(),
            reservierung.reservierungsnummer(), hauptgast, reservierung.aufenthaltszeitraum(),
            reservierung.zimmerkategorie(), null, BuchungsStatus.RESERVIERT, null);
    }

    /**
     * Ein Gast ohne vorherige Reservierung checkt direkt ein: es entsteht eine neue, bereits eingecheckte Buchung
     * fuer die Kategorie des gewaehlten Zimmers.
     */
    @FactoryMethod
    public static Buchung checkeSpontanEin(HotelId hotelId, Gastdaten gastdaten,
                                           Aufenthaltszeitraum aufenthaltszeitraum, Zimmer.ZimmerId zimmerId,
                                           Zimmerkategorie zimmerkategorie, LocalDate heute) {
        DomainAssertions.isTrue(aufenthaltszeitraum.anreise().equals(heute),
            "Bei einem spontanen CheckIn muss die Anreise heute sein.");
        return new Buchung(0, new BuchungsId(UUID.randomUUID()), hotelId, null, Gast.erfasse(gastdaten),
            aufenthaltszeitraum, zimmerkategorie, zimmerId, BuchungsStatus.EINGECHECKT, null);
    }

    /**
     * Checkt den Hauptgast ein: die geprueften Gastdaten werden uebernommen und das Zimmer zugewiesen. Das Zimmer muss
     * der gebuchten Kategorie entsprechen.
     */
    public Buchung checkeEin(Zimmer.ZimmerId zimmerId, Zimmerkategorie kategorieDesZimmers, Gastdaten gastdaten,
                             LocalDate heute) {
        DomainAssertions.equals(status, BuchungsStatus.RESERVIERT, "Nur reservierte Buchungen koennen einchecken.");
        DomainAssertions.isTrue(aufenthaltszeitraum.anreise().equals(heute),
            "Ein CheckIn ist nur am Anreisetag moeglich.");
        DomainAssertions.equals(kategorieDesZimmers, zimmerkategorie,
            "Gebucht ist ein %s, das Zimmer ist ein %s.".formatted(zimmerkategorie, kategorieDesZimmers));
        hauptgast.aktualisiere(gastdaten);
        this.zimmerId = zimmerId;
        this.status = BuchungsStatus.EINGECHECKT;
        return this;
    }

    /**
     * Storniert eine noch nicht eingecheckte Buchung.
     */
    public Buchung storniere() {
        DomainAssertions.equals(status, BuchungsStatus.RESERVIERT,
            "Nur reservierte Buchungen koennen storniert werden.");
        this.status = BuchungsStatus.STORNIERT;
        return this;
    }

    /**
     * Der Hauptgast reist ab. Das Zimmer bleibt als Historie an der Buchung.
     */
    public Buchung checkeAus(LocalDate heute) {
        DomainAssertions.equals(status, BuchungsStatus.EINGECHECKT, "Nur eingecheckte Buchungen koennen auschecken.");
        DomainAssertions.isFalse(heute.isBefore(aufenthaltszeitraum.anreise()),
            "Ein Checkout vor der Anreise ist nicht moeglich.");
        this.status = BuchungsStatus.AUSGECHECKT;
        this.ausgechecktAm = heute;
        return this;
    }

    @Override
    public void validate() {
        var warEingecheckt = status == BuchungsStatus.EINGECHECKT || status == BuchungsStatus.AUSGECHECKT;
        DomainAssertions.isTrue(warEingecheckt == (zimmerId != null),
            "Ein Zimmer ist genau dann zugewiesen, wenn die Buchung eingecheckt ist oder war.");
        DomainAssertions.isTrue(!warEingecheckt || hauptgast.getAdresse().isPresent(),
            "Fuer den CheckIn ist die Adresse des Hauptgasts erforderlich.");
        DomainAssertions.isTrue((status == BuchungsStatus.AUSGECHECKT) == (ausgechecktAm != null),
            "Genau ausgecheckte Buchungen haben einen Checkout-Tag.");
    }

    public HotelId getHotelId() {
        return hotelId;
    }

    public Optional<Reservierungsnummer> getReservierungsnummer() {
        return Optional.ofNullable(reservierungsnummer);
    }

    public Gast getHauptgast() {
        return hauptgast;
    }

    public Aufenthaltszeitraum getAufenthaltszeitraum() {
        return aufenthaltszeitraum;
    }

    public Zimmerkategorie getZimmerkategorie() {
        return zimmerkategorie;
    }

    public Optional<Zimmer.ZimmerId> getZimmerId() {
        return Optional.ofNullable(zimmerId);
    }

    public BuchungsStatus getStatus() {
        return status;
    }

    public Optional<LocalDate> getAusgechecktAm() {
        return Optional.ofNullable(ausgechecktAm);
    }
}
