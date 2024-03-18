package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nitrox.dlc.domain.types.Entity;
import nitrox.dlc.domain.types.Identity;

import java.util.UUID;

/**
 * Die Klasse RechnungsPosition repräsentiert eine Position auf einer Rechnung in der Domain.
 * Sie gehorcht den Eigenschaften einer Entity gemäß dem DDD-Ansatz (Domain-driven Design)
 * und beinhaltet Informationen wie Preis, ServiceLeistung und eine Beschreibung der Position.
 *
 * @author Mario Herb
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class RechnungsPosition implements Entity<RechnungsPosition.RechnungsPositionId> {

    /**
     * Der Identifier der Rechnungsposition, implementiert als record-Typ.
     */
    public record RechnungsPositionId(UUID value) implements Identity<UUID> {}

    @EqualsAndHashCode.Include
    private final RechnungsPositionId id;

    /**
     * Der Preis der Rechnungsposition.
     */
    private final Preis preis;

    /**
     * Der Identifier der ServiceLeistung, die mit dieser Rechnungsposition abgerechnet wird.
     */
    private final ServiceLeistung.LeistungsId leistungsId;

    /**
     * Eine Beschreibung der Rechnungsposition.
     */
    private final String beschreibung;

    private long concurrencyVersion;

    @Override
    public RechnungsPositionId id() {
        return id;
    }

    /**
     * Erstellt eine neue Rechnungsposition mit gegebenen Identifier, Preis, Beschreibung und ServiceLeistung.
     * Es wird überprüft, ob der Preis und die ServiceLeistung nicht null sind und ob die Beschreibung nicht länger
     * als 1000 Zeichen ist, wobei in diesem Fall eine IllegalArgumentException ausgelöst wird.
     *
     * @param id Der Identifier der Rechnungsposition.
     * @param preis Der Preis der Rechnungsposition.
     * @param beschreibung Eine Beschreibung der Rechnungsposition.
     * @param leistungsId Der Identifier der ServiceLeistung.
     */
    RechnungsPosition(RechnungsPositionId id,
                      Preis preis,
                      String beschreibung,
                      ServiceLeistung.LeistungsId leistungsId
    ) {
        if (preis == null) {
            throw new IllegalArgumentException("Preis darf nicht null sein");
        }
        if (leistungsId == null) {
            throw new IllegalArgumentException("LeistungsId darf nicht null sein");
        }
        if (beschreibung != null && beschreibung.length() > 1000) {
            throw new IllegalArgumentException("Beschreibung darf nicht mehr als 1000 Zeichen enthalten.");
        }
        this.id = id;
        this.preis = preis;
        this.leistungsId = leistungsId;
        this.beschreibung = beschreibung;
    }

    @Override
    public long concurrencyVersion() {
        return concurrencyVersion;
    }
}