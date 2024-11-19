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

package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import io.domainlifecycles.domain.types.AggregateRoot;
import io.domainlifecycles.domain.types.Identity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Diese Klasse repräsentiert eine Reservierung in einem Hotel. Sie enthält Informationen
 * wie die Reservierungsnummer, das Hotel, das Ankunftsdatum, Check-in und Check-out Daten,
 * usw. sowie Methoden zum Behandeln von Gastdaten, Check-In, Check-Out, usw.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class Reservierung implements AggregateRoot<Reservierung.ReservierungsNummer> {

    /**
     * Record-Klasse, die für die Darstellung der Reservierungsnummer dient. Identität der Reservierung.
     */
    public record ReservierungsNummer(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final ReservierungsNummer reservierungsNummer;

    private final Hotel.Id hotelId;

    private final LocalDate geplanteAnkunftAm;

    private LocalDateTime checkInAm;

    private LocalDateTime checkOutAm;

    private LocalDateTime storniertAm;

    private final Preis angebotenerZimmerpreis;

    private int geplanteAnzahlNaechte;

    private final ZimmerKategorie gewuenschteZimmerKategorie;

    private final int gewuenschteKapazitaet;

    private final Gast gast;

    private Zimmer.ZimmerNummer zimmerNummer;

    private long concurrencyVersion;

    public Reservierung(
            ReservierungsNummer reservierungsNummer,
            Hotel.Id hotelId,
            LocalDate geplanteAnkunftAm,
            LocalDateTime checkInAm,
            LocalDateTime checkOutAm,
            LocalDateTime storniertAm,
            Preis angebotenerZimmerpreis,
            int geplanteAnzahlNaechte,
            ZimmerKategorie gewuenschteZimmerKategorie,
            int gewuenschteKapazitaet,
            Gast gast,
            Zimmer.ZimmerNummer zimmerNummer,
            long concurrencyVersion) {
        this.reservierungsNummer = Objects.requireNonNull(reservierungsNummer, "Eine ReservierungsNummer muss vorhanden sein!");
        this.hotelId = Objects.requireNonNull(hotelId, "Jede Reservierung muss sich auf ein konkretes Hotel beziehen!");
        this.geplanteAnkunftAm = Objects.requireNonNull(geplanteAnkunftAm, "Das Datum der geplanten Ankunft muss angegeben sein!");
        this.checkInAm = checkInAm;
        this.checkOutAm = checkOutAm;
        this.storniertAm = storniertAm;
        this.angebotenerZimmerpreis = Objects.requireNonNull(angebotenerZimmerpreis, "Der angebotene Zimmerpreis muss angegeben sein!");
        this.geplanteAnzahlNaechte = geplanteAnzahlNaechte;
        this.gewuenschteZimmerKategorie = gewuenschteZimmerKategorie;
        this.gewuenschteKapazitaet = gewuenschteKapazitaet;
        if(gewuenschteKapazitaet<=0){
            throw new IllegalStateException("Die gewünschte Kapazität muss >0 sein!");
        }
        this.gast = Objects.requireNonNull(gast, "Ein Hauptgast muss von Beginn an angegeben sein!");
        this.zimmerNummer = zimmerNummer;
        this.concurrencyVersion = concurrencyVersion;
    }

    @Override
    public ReservierungsNummer id() {
        return reservierungsNummer;
    }

    /**
     * Methode zum Hinzufügen oder Aktualisieren der Gastdaten in einer Reservierung.
     *
     * @param vervollstaendigeGastDaten Gastdaten
     * @return Reservierung
     */
    public Reservierung handle(VervollstaendigeGastDaten vervollstaendigeGastDaten){
        gast.setEmailAdresse(vervollstaendigeGastDaten.emailAdresse())
            .setGeburtsDatum(vervollstaendigeGastDaten.geburtsdatum())
            .setVorname(vervollstaendigeGastDaten.vorname())
            .setNachname(vervollstaendigeGastDaten.nachname())
            .setTelefonNummer(vervollstaendigeGastDaten.telefonNummer())
            .setHeimAdresse(vervollstaendigeGastDaten.heimAdresse());
        return this;
    }

    /**
     * Methode zum Einchecken eines Gastes.
     *
     * @param checkeEin Daten fürs Einchecken
     * @return Reservierung
     * @throws IllegalStateException wenn der Gast bereits eingecheckt ist
     */
    public Reservierung handle(CheckeEin checkeEin){
        if(zimmerNummer == null && checkInAm == null){
            if(gast.getGeburtsDatum() == null){
                throw new IllegalStateException("Geburtsdatum des Gasts muss spätestens beim Einchecken angegeben werden!");
            }
            zimmerNummer = checkeEin.zimmerNummer();
            checkInAm = LocalDateTime.now();
            geplanteAnzahlNaechte = checkeEin.geplanteAnzahlNaechte();
            return this;
        }
        throw new IllegalStateException("Reservierung bereits eingecheckt!");
    }

    /**
     * Methode zum Auschecken eines Gastes von einer Reservierung.
     *
     * @return Reservierung
     * @throws IllegalStateException wenn der Gast bereits ausgecheckt hat oder wenn noch nicht eingecheckt.
     */
    public Reservierung checkeAus(){
        if(checkOutAm != null){
            throw new IllegalStateException("Reservierung war bereits ausgecheckt!");
        }
        if(checkInAm != null){
            checkOutAm = LocalDateTime.now();
            return this;
        }
        throw new IllegalStateException("Reservierung war nicht eingecheckt!");
    }

    /**
     * Methode zum Stornieren von Reservierungen. Wenn die Reservierung bereits
     * eingecheckt ist, kann diese nicht storniert werden.
     *
     * @return Reservierung
     * @throws IllegalStateException wenn die Reservierung bereits eingecheckt wurde.
     */
    public Reservierung storniere(){
        if(checkInAm != null){
            throw new IllegalStateException("Reservierung bereits eingecheckt!");
        }
        storniertAm = LocalDateTime.now();
        return this;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }

}