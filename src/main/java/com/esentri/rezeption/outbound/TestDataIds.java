package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.Gast;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import nitrox.dlc.domain.types.Identity;

import java.util.UUID;

/**
 * Diese Enum-Klasse dient zur Bereitstellung von Testdaten-IDs, die in den verschiedenen Teilen des Systems zur Demonstration verwendet werden.
 * Hierbei handelt es sich hauptsächlich um Identifikatoren für Hotel, Zimmer, Reservierung, Gast und ServiceLeistung.
 *
 * Jeder Enum-Wert besitzt eine Identität, welche in der jeweiligen Domaininstanz gespeichert wird.
 * So ermöglichen wir beispielsweise die Cache-Kohärenz bei komplexen Abfragen und garantieren die Datenintegrität auf Systemebene.
 *
 * Für Details zu den einzelnen Identitäten siehe die einzelnen Enum-Werte und die Methode {@link #id()}.
 *
 * @author Mario Herb
 */
public enum TestDataIds {

    HOTEL_ID(new Hotel.HotelId(UUID.randomUUID())),
    ZIMMER_ID_EINZELZIMMER(new Zimmer.ZimmerNummer("1")),
    ZIMMER_ID_BUSINESS_SUITE(new Zimmer.ZimmerNummer("2")),
    ZIMMER_ID_PRESIDENTIAL_SUITE(new Zimmer.ZimmerNummer("3")),
    RESERVIERUNG_ID_OFFEN(new Reservierung.ReservierungsNummer(UUID.randomUUID())),
    GAST_RESERVIERUNG_OFFEN_ID(new Gast.GastId(UUID.randomUUID())),
    RESERVIERUNG_ID_EINGECHECKT(new Reservierung.ReservierungsNummer(UUID.randomUUID())),
    GAST_RESERVIERUNG_EINGECHECKT_ID(new Gast.GastId(UUID.randomUUID())),
    SERVICE_LEISTUNG_ID(new ServiceLeistung.LeistungsId(UUID.randomUUID()))
    ;

    private Identity<?> id;

    TestDataIds(Identity<?> id) {
        this.id = id;
    }

    public <I extends Identity<?>> I id(){
        return (I) id;
    }

}
