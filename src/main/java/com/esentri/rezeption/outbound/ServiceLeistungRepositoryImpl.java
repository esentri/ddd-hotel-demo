package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceTyp;
import com.esentri.rezeption.core.outport.ServiceLeistungRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse ServiceLeistungRepositoryImpl bietet eine Implementierung des
 * ServiceLeistungRepository-Interfaces. Sie erweitert BaseInMemoryRepository.
 *
 * @author Mario Herb
 */
@Repository
public class ServiceLeistungRepositoryImpl extends BaseInMemoryRepository<ServiceLeistung.LeistungsId, ServiceLeistung> implements ServiceLeistungRepository {

    /**
     * Der Standardkonstruktor erstellt ein neues Objekt dieses Typs und füllt das Repository
     * mit vordefinierten Daten.
     */
    public ServiceLeistungRepositoryImpl() {
        super(new ArrayList<>());
        allAggregates.addAll(List.of(
                new ServiceLeistung(
                        TestDataIds.SERVICE_LEISTUNG_ID.id(),
                        TestDataIds.RESERVIERUNG_ID_EINGECHECKT.id(),
                        "Flasche Dom Perignon 2013",
                        LocalDateTime.now().minusDays(1),
                        ServiceTyp.BAR,
                        new Preis(BigDecimal.valueOf(300))
                )
        ));
    }

    /**
     * Diese Methode findet ServiceLeistung-Instanzen,
     * die zu der übergebenen Reservierungsnummer gehören.
     *
     * @param reservierungsNummer Die Reservierungsnummer, nach der gesucht werden soll.
     * @return eine Liste von ServiceLeistung-Instanzen, die der angegebenen Reservierungsnummer entsprechen.
     */
    @Override
    public List<ServiceLeistung> find(Reservierung.ReservierungsNummer reservierungsNummer) {
        return allAggregates.stream().filter(sl -> reservierungsNummer.equals(sl.getReservierungsNummer())).toList();
    }
}