package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.WartungsPlanungId;
import nitrox.dlc.domain.types.DomainEvent;

/**
 * ZimmerWartungAbgelehnt repräsentiert das Ereignis, wenn eine Wartung abgelehnt wird.
 * Er enthält die Referenz auf den Wartungsplan und die Zimmernummer, für die die Wartung abgelehnt wurde.
 *
 * @author Mario Herb
 */
public record ZimmerWartungAbgelehnt(WartungsPlanungId planungsReferenz, Zimmer.ZimmerNummer zimmerNummer) implements DomainEvent {
}
