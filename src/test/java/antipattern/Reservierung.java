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

package antipattern;

import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservierung {
    private Long reservierungsNummer;
    private Long hotelId;
    private LocalDate geplanteAnkunftAm;
    private LocalDateTime checkInAm;
    private LocalDateTime checkOutAm;
    private LocalDateTime storniertAm;
    private BigDecimal angebotenerZimmerpreis;
    private int geplanteAnzahlNaechte;
    private ZimmerKategorie gewuenschteZimmerKategorie;
    private int gewuenschteKapazitaet;
    private Long zimmerNummer;
    private String vorname;
    private String nachname;
    private LocalDate geburtsDatum;
    private String telefonNummer;
    private String emailAdresse;
    private String strasse;
    private String hausnummer;
    private String postleitzahl;
    private String ort;
    private long concurrencyVersion;

    public Reservierung(){
    }
    public Long getReservierungsNummer() {
        return reservierungsNummer;
    }

    public void setReservierungsNummer(Long reservierungsNummer) {
        this.reservierungsNummer = reservierungsNummer;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getGeplanteAnkunftAm() {
        return geplanteAnkunftAm;
    }

    public void setGeplanteAnkunftAm(LocalDate geplanteAnkunftAm) {
        this.geplanteAnkunftAm = geplanteAnkunftAm;
    }

    public LocalDateTime getCheckInAm() {
        return checkInAm;
    }

    public void setCheckInAm(LocalDateTime checkInAm) {
        this.checkInAm = checkInAm;
    }

    public LocalDateTime getCheckOutAm() {
        return checkOutAm;
    }

    public void setCheckOutAm(LocalDateTime checkOutAm) {
        this.checkOutAm = checkOutAm;
    }

    public LocalDateTime getStorniertAm() {
        return storniertAm;
    }

    public void setStorniertAm(LocalDateTime storniertAm) {
        this.storniertAm = storniertAm;
    }

    public BigDecimal getAngebotenerZimmerpreis() {
        return angebotenerZimmerpreis;
    }

    public void setAngebotenerZimmerpreis(BigDecimal angebotenerZimmerpreis) {
        this.angebotenerZimmerpreis = angebotenerZimmerpreis;
    }

    public int getGeplanteAnzahlNaechte() {
        return geplanteAnzahlNaechte;
    }

    public void setGeplanteAnzahlNaechte(int geplanteAnzahlNaechte) {
        this.geplanteAnzahlNaechte = geplanteAnzahlNaechte;
    }

    public ZimmerKategorie getGewuenschteZimmerKategorie() {
        return gewuenschteZimmerKategorie;
    }

    public void setGewuenschteZimmerKategorie(ZimmerKategorie gewuenschteZimmerKategorie) {
        this.gewuenschteZimmerKategorie = gewuenschteZimmerKategorie;
    }

    public int getGewuenschteKapazitaet() {
        return gewuenschteKapazitaet;
    }

    public void setGewuenschteKapazitaet(int gewuenschteKapazitaet) {
        this.gewuenschteKapazitaet = gewuenschteKapazitaet;
    }

    public Long getZimmerNummer() {
        return zimmerNummer;
    }

    public void setZimmerNummer(Long zimmerNummer) {
        this.zimmerNummer = zimmerNummer;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public LocalDate getGeburtsDatum() {
        return geburtsDatum;
    }

    public void setGeburtsDatum(LocalDate geburtsDatum) {
        this.geburtsDatum = geburtsDatum;
    }

    public String getTelefonNummer() {
        return telefonNummer;
    }

    public void setTelefonNummer(String telefonNummer) {
        this.telefonNummer = telefonNummer;
    }

    public String getEmailAdresse() {
        return emailAdresse;
    }

    public void setEmailAdresse(String emailAdresse) {
        this.emailAdresse = emailAdresse;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public long getConcurrencyVersion() {
        return concurrencyVersion;
    }

    public void setConcurrencyVersion(long concurrencyVersion) {
        this.concurrencyVersion = concurrencyVersion;
    }
}