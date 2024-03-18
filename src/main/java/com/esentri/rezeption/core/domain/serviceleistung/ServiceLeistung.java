package com.esentri.rezeption.core.domain.serviceleistung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nitrox.dlc.domain.types.AggregateRoot;
import nitrox.dlc.domain.types.Identity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Das ServiceLeistung, als AggregatRoot implementiert,
 * mit einer Beschreibung, einem Servicetyp und einem Nettopreis.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class ServiceLeistung implements AggregateRoot<ServiceLeistung.LeistungsId> {

    public record LeistungsId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final LeistungsId id;

    private final Reservierung.ReservierungsNummer reservierungsNummer;

    private Rechnung.RechnungId abgerechnetPer;

    private final String beschreibung;
    private final ServiceTyp serviceTyp;
    private final LocalDateTime erhaltenAm;
    private final Preis nettoPreis;

    private long concurrencyVersion;

    public ServiceLeistung(LeistungsId id,
                           Reservierung.ReservierungsNummer reservierungsNummer,
                           String beschreibung,
                           LocalDateTime erhaltenAm,
                           ServiceTyp serviceTyp,
                           Preis nettoPreis
    ) {
        Objects.requireNonNull(id, "Die LeistungsId darf nicht null sein.");
        Objects.requireNonNull(reservierungsNummer, "Die referenzierte ReservierungsNummer darf nicht null sein.");
        Objects.requireNonNull(serviceTyp, "Der ServiceTyp darf nicht null sein.");
        Objects.requireNonNull(nettoPreis, "Der NettoPreis darf nicht null sein.");
        this.id = id;
        this.reservierungsNummer = reservierungsNummer;
        this.validateBeschreibung(beschreibung);
        this.beschreibung = beschreibung;
        this.serviceTyp = serviceTyp;
        this.nettoPreis = nettoPreis;
        this.erhaltenAm = erhaltenAm;
    }

    private void validateBeschreibung(String beschreibung) {
        if (beschreibung != null && beschreibung.length() > 1000) {
            throw new IllegalArgumentException("Die Beschreibung sollte maximal 1000 Zeichen lang sein.");
        }
    }

    @Override
    public LeistungsId id() {
        return id;
    }

    public ServiceLeistung abgerechnet(Rechnung.RechnungId rechnungId){
        Objects.requireNonNull(rechnungId, "Die RechnungId darf nicht null sein.");
        this.abgerechnetPer = rechnungId;
        return this;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}
