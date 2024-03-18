package com.esentri.rezeption.core.inport;

import com.esentri.rezeption.core.domain.rechnung.RechnungErstellt;
import com.esentri.rezeption.core.domain.serviceleistung.ErstelleServiceLeistung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.Driver;

/**
 * Interface für den ServiceLeistungDriver. Dieses Interface bietet Methoden zur Handhabung von Serviceleistungen.
 * Es enthält Methoden, um ServiceLeistungen zu erstellen und auf RechnungErstellt Ereignisse zu reagieren.
 *
 * @author Mario Herb
 *
 */
public interface ServiceLeistungDriver extends Driver {

    /**
     * Erstellt eine Serviceleistung und gibt eine LeistungsId zurück.
     *
     * @param erstelleServiceLeistung Command, das Informationen zum Erstellen einer Serviceleistung enthält.
     * @return LeistungsId der erstellten Serviceleistung.
     */
    ServiceLeistung.LeistungsId handle(ErstelleServiceLeistung erstelleServiceLeistung);

    /**
     * Diese Methode wird aufgerufen, wenn ein RechnungErstellt Ereignis eintritt.
     *
     * @param rechnungErstellt Das RechnungErstellt Ereignis, auf das die Methode reagiert.
     */
    void onEvent(RechnungErstellt rechnungErstellt);
}