package com.esentri.rezeption.core.inport;
import com.esentri.rezeption.core.domain.reservierung.CheckeAus;
import com.esentri.rezeption.core.domain.reservierung.CheckeEin;
import com.esentri.rezeption.core.domain.reservierung.ErstelleNeueReservierung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.reservierung.StorniereReservierung;
import com.esentri.rezeption.core.domain.reservierung.VervollstaendigeGastDaten;
import nitrox.dlc.domain.types.Driver;

/**
 * Dieses Interface bietet eine Methode an, um verschiedene Commands für Reservierungen zu handhaben. Diese Commands umfassen die Erstellung
 * einer neuen Reservierung, das Aktualisieren von Gastdaten, das Ein- und Auschecken und das Stornieren einer Reservierung.
 *
 * @author Mario Herb
 */
public interface ReservierungDriver extends Driver {

    /**
     * Behandelt den Command zur Erstellung einer neuen Reservierung.
     * @param erstelleNeueReservierung beinhaltet alle notwenigen Informationen zur Erstellung einer neuen Reservierung.
     * @return Gibt die Nummer der erstellten Reservierung zurück.
     */
    Reservierung.ReservierungsNummer handle(ErstelleNeueReservierung erstelleNeueReservierung);

    /**
     * Behandelt Command zur der Aktualisierung von Gastdaten.
     * @param vervollstaendigeGastDaten beinhaltet alle notwenigen Informationen zur Aktualisierung der Gastdaten.
     * @return Gibt die Nummer der Reservierung zurück, für die die Gastdaten aktualisiert wurden.
     */
    Reservierung.ReservierungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten);

    /**
     * Behandelt den Check-In Command.
     * @param checkeEin beinhaltet alle notwenigen Informationen für den Check-In eines Gastes.
     * @return Gibt die Nummer der Reservierung zurück, für die der Check-In durchgeführt wurde.
     */
    Reservierung.ReservierungsNummer handle(CheckeEin checkeEin);

    /**
     * Behandelt den Check-Out Command.
     * @param checkeAus beinhaltet alle notwenigen Informationen für den Check-Out eines Gastes.
     * @return Gibt die Nummer der Reservierung zurück, für die der Check-Out durchgeführt wurde.
     */
    Reservierung.ReservierungsNummer handle(CheckeAus checkeAus);

    /**
     * Behandelt das Stornieren einer Reservierung.
     * @param storniereReservierung beinhaltet alle notwenigen Informationen um eine Reservierung zu stornieren.
     * @return Gibt die Nummer der Reservierung zurück, die storniert wurde.
     */
    Reservierung.ReservierungsNummer handle(StorniereReservierung storniereReservierung);

}
