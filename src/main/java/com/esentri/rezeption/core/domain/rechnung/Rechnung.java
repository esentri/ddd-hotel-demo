/*
 *  Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import io.domainlifecycles.domain.types.AggregateRoot;
import io.domainlifecycles.domain.types.Identity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Rechnungsklasse zur Erstellung und Verwaltung von Rechnungen in unserem System, implementiert als DDD AggregateRoot.
 * Eine Rechnung bezieht sich immer auf eine Buchung und kann verschiedene Rechnungspositionen enthalten.
 * Die Klasse verfolgt auch, wann sie erstellt und bezahlt wurde.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Rechnung implements AggregateRoot<Rechnung.Id> {

    /**
     * Struktur zur Darstellung der eindeutigen Identität einer Rechnung.
     */
    public record Id(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final Id id;

    private final Buchung.BuchungsNummer buchungsNummer;

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
     * @param buchungsNummer BuchungsNummer, auf die sich die Rechnung bezieht
     * @param zimmerPreis Optionaler Preis für ein Zimmer (wenn vorhanden)
     * @param erstelltAm Datum und Uhrzeit, an dem die Rechnung erstellt wurde
     * @param rechnungsAdresse Die Adresse, an die die Rechnung gesendet wird
     */
    @Builder
    public Rechnung(Id id, Buchung.BuchungsNummer buchungsNummer, Preis zimmerPreis, LocalDateTime erstelltAm, Adresse rechnungsAdresse) {
        Objects.requireNonNull(id, "Die Id darf nicht null sein.");
        Objects.requireNonNull(buchungsNummer, "Die referenzierte BuchungsNummer darf nicht null sein.");
        this.id = id;
        this.buchungsNummer = buchungsNummer;
        this.zimmerPreis = Optional.ofNullable(zimmerPreis);
        this.erstelltAm = erstelltAm;
        this.rechnungsPositionen = new ArrayList<>();
        this.rechnungsAdresse = rechnungsAdresse;
    }

    @Override
    public Id id() {
        return id;
    }

    /**
     * Fügt eine neue Rechnungsposition zur Rechnung hinzu, berechnet das Gesamtnetto und gibt die aktualisierte Rechnung zurück.
     *
     * @param nettoPreis Der Nettopreis der hinzuzufügenden Position
     * @param beschreibung Eine Beschreibung der hinzuzufügenden Position
     * @param leistungsId Die Id der Serviceleistung, die hinzugefügt werden soll
     */
    public Rechnung addRechnungsPosition(Preis nettoPreis, String beschreibung, ServiceLeistung.Id leistungsId) {
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