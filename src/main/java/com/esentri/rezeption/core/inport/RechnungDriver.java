package com.esentri.rezeption.core.inport;

import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerReservierung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleServiceRechnung;
import com.esentri.rezeption.core.domain.rechnung.MarkiereRechnungBezahlt;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import nitrox.dlc.domain.types.Driver;

/**
 * Das Interface RechnungDriver stellt eine Schnittstelle für die Bearbeitung von Rechnungen zur Verfügung.
 * Es definiert Methoden zur Erstellung von Rechnungsaufträgen und zur Markierung von bereits bezahlten Rechnungen.
 *
 * @author Mario Herb
 * @see Rechnung
 * @see ErstelleRechnungFuerReservierung
 * @see ErstelleServiceRechnung
 * @see MarkiereRechnungBezahlt
 */
public interface RechnungDriver extends Driver {

    /**
     * Nimmt einen Command zur Markierung einer Rechnung als bezahlt entgegen und gibt die ID der bearbeiteten Rechnung zurück.
     *
     * @param markiereRechnungBezahlt ein Command, der die zu zahlende Rechnung angibt
     * @return die ID der bezahlten Rechnung
     */
    Rechnung.RechnungId handle(MarkiereRechnungBezahlt markiereRechnungBezahlt);

    /**
     * Nimmt einen Command zur Erstellung einer Rechnung für eine Reservierung entgegen und gibt die ID der erstellten Rechnung zurück.
     *
     * @param erstelleRechnungFuerReservierung ein Command, der die Details für die zu erstellende Rechnung angibt
     * @return die ID der erstellten Rechnung
     */
    Rechnung.RechnungId handle(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung);

    /**
     * Nimmt einen Command zur Erstellung einer Service-Rechnung entgegen und gibt die ID der erstellten Rechnung zurück.
     *
     * @param erstelleServiceRechnung ein Command, der die Details für die zu erstellende Rechnung angibt
     * @return die ID der erstellten Rechnung
     */
    Rechnung.RechnungId handle(ErstelleServiceRechnung erstelleServiceRechnung);
}