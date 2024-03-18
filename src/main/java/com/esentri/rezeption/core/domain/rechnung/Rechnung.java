package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nitrox.dlc.domain.types.AggregateRoot;
import nitrox.dlc.domain.types.Identity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Rechnungsklasse zur Erstellung und Verwaltung von Rechnungen in unserem System, implementiert als DDD AggregateRoot.
 * Eine Rechnung bezieht sich immer auf eine Reservierung und kann verschiedene Rechnungspositionen enthalten.
 * Die Klasse verfolgt auch, wann sie erstellt und bezahlt wurde.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Rechnung implements AggregateRoot<Rechnung.RechnungId> {

    /**
     * Struktur zur Darstellung der eindeutigen Identität einer Rechnung.
     */
    public record RechnungId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final RechnungId id;

    private final Reservierung.ReservierungsNummer reservierungsNummer;

    private Preis gesamtNetto;

    private final Optional<Preis> zimmerPreis;

    private final LocalDateTime erstelltAm;

    private LocalDateTime bezahltAm;

    /** Rechnungsadresse, an die die Rechnung geschickt wird. */
    @Setter
    private Adresse rechnungsAdresse;

    /** Liste der Positionen, die auf der Rechnung aufgeführt sind,
     * diese beziehen sich immer auf ServiceLeistungen. (Es gibt keine dedizierte Position für das Zimmer).
     * Das abgerechnete Zimmer ist beinhaltet, wenn ein Zimmerpreis angegeben ist*/
    private List<RechnungsPosition> rechnungsPositionen;

    private long concurrencyVersion;

    /**
     * Erstellt eine neue Rechnung mit den gegebenen Informationen.
     *
     * @param id identifikation der Rechnung
     * @param reservierungsNummer Reservierungsnummer, auf die sich die Rechnung bezieht
     * @param zimmerPreis Optionaler Preis für ein Zimmer (wenn vorhanden)
     * @param erstelltAm Datum und Uhrzeit, an dem die Rechnung erstellt wurde
     * @param rechnungsAdresse Die Adresse, an die die Rechnung gesendet wird
     */
    @Builder
    public Rechnung(RechnungId id, Reservierung.ReservierungsNummer reservierungsNummer, Preis zimmerPreis, LocalDateTime erstelltAm, Adresse rechnungsAdresse) {
        Objects.requireNonNull(id, "Die LeistungsId darf nicht null sein.");
        Objects.requireNonNull(reservierungsNummer, "Die referenzierte ReservierungsNummer darf nicht null sein.");
        this.id = id;
        this.reservierungsNummer = reservierungsNummer;
        this.zimmerPreis = Optional.ofNullable(zimmerPreis);
        this.erstelltAm = erstelltAm;
        this.rechnungsPositionen = new ArrayList<>();
        this.rechnungsAdresse = rechnungsAdresse;
    }

    @Override
    public RechnungId id() {
        return id;
    }

    /**
     * Fügt eine neue Rechnungsposition zur Rechnung hinzu, berechnet das Gesamtnetto und gibt die aktualisierte Rechnung zurück.
     *
     * @param nettoPreis Der Nettopreis der hinzuzufügenden Position
     * @param beschreibung Eine Beschreibung der hinzuzufügenden Position
     * @param leistungsId Die Id der Serviceleistung, die hinzugefügt werden soll
     */
    public Rechnung addRechnungsPosition(Preis nettoPreis, String beschreibung, ServiceLeistung.LeistungsId leistungsId) {
        var neuePosition = new RechnungsPosition(
            new RechnungsPosition.RechnungsPositionId(UUID.randomUUID()),
            nettoPreis,
            beschreibung,
            leistungsId
        );
        this.rechnungsPositionen.add(neuePosition);
        berechneGesamtNetto();
        return this;
    }

    private void berechneGesamtNetto() {
        this.gesamtNetto = this.rechnungsPositionen.stream()
            .map(RechnungsPosition::getPreis)
            .reduce(Preis.NULL, Preis::addiere).addiere(zimmerPreis.orElse(null));

    }

    public Rechnung markiereBezahlt(){
        this.bezahltAm = LocalDateTime.now();
        return this;
    }

    public boolean beinhaltetZimmerAbrechnung(){
        return zimmerPreis.isPresent();
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }

}