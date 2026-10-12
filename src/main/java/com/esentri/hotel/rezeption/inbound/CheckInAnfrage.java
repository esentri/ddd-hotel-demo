package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.Gastdaten;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;

/**
 * Eingabe der Rezeptionistin beim CheckIn; die Buchung kommt aus dem Pfad.
 */
public record CheckInAnfrage(Zimmernummer zimmernummer, Gastdaten gastdaten) {

    CheckeEin fuer(Buchung.BuchungsId buchungsId) {
        return new CheckeEin(buchungsId, zimmernummer, gastdaten);
    }
}
