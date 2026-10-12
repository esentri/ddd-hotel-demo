package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;

/**
 * Antwort beim Anlegen einer Buchung. Als Feld eines Objekts wird die ID als einfacher JSON-Wert geschrieben.
 */
public record BuchungAngelegt(Buchung.BuchungsId buchungsId) {
}
