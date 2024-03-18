package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.WartungsPlanungId;
import nitrox.dlc.domain.types.DomainEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * Diese Klasse repräsentiert das Event, dass eine Zimmerwartung eingeplant wurde (aus einem anderen Bounded Context).
 * Sie beinhaltet die Referenz zur Wartungsplanung, den Zeitraum der Wartung und die betroffenen Zimmer.
 *
 * @author Mario Herb
 */
public record ZimmerWartungEingeplant(
        WartungsPlanungId planungReferenz,
        LocalDate von,
        LocalDate bis,
        List<Zimmer.ZimmerNummer> zimmerNummern
) implements DomainEvent {}
