package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.WartungsPlanungId;
import nitrox.dlc.domain.types.DomainEvent;

/**
 * ZimmerWartungBestaetigt repräsentiert das Ereignis, wenn eine Wartung bestätigt wird.
 * Er enthält die Referenz auf den Wartungsplan und die Zimmernummer, für die die Wartung bestätigt wurde.
 *
 * @author Mario Herb
 */
public record ZimmerWartungBestaetigt(WartungsPlanungId planungsReferenz, Zimmer.ZimmerNummer zimmerNummer) implements DomainEvent {
}
