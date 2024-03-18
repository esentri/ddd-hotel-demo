package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.Repository;

import java.util.List;

/**
 * Repository für ServiceLeistungen.
 *
 * @author Mario Herb
 */
public interface ServiceLeistungRepository extends Repository<ServiceLeistung.LeistungsId, ServiceLeistung> {

    /**
     * Sucht eine Liste von ServiceLeistungen basierend auf ihrer Reservierungsnummer.
     *
     * @param reservierungsNummer Die Reservierungsnummer der gesuchten ServiceLeistungen.
     * @return Eine Liste von ServiceLeistungen, welche die gegebene Reservierungsnummer haben. Eine leere Liste, falls keine passenden ServiceLeistungen gefunden werden könnten.
     */
    List<ServiceLeistung> find(Reservierung.ReservierungsNummer reservierungsNummer);
}
