package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import io.domainlifecycles.domain.types.OutboundService;

/**
 * Service-Fassade für die Erstellung von PDF Dokumenten für eine Rechnung.
 *
 * @author Mario Herb
 */
public interface RechnungsPDFErstellung extends OutboundService {

    /**
     * Gibt einen byte-Array zurück, der das Rechnungs-PDF für die Rechnung mit der übergebenen Id repräsentiert.
     *
     * @param rechnungsId Id der Rechnung für welche ein PDF erzeugt werden soll.
     * @return byte Array für das Rechnungs PDF
     */
    byte[] erzeugePDF(Rechnung.Id rechnungsId);
}
