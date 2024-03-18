package com.esentri.rezeption.core.domain.zimmer;

import com.esentri.rezeption.core.domain.WartungsPlanungId;
import nitrox.dlc.domain.types.DomainCommand;

import java.time.LocalDate;

/**
 * Ein DomainCommand zur Beantragung einer Zimmerwartung.
 *
 * @author Mario Herb
 */
public record BeantrageZimmerWartung(WartungsPlanungId wartungsPlanungId,
                                     LocalDate von,
                                     LocalDate bis,
                                     Zimmer.ZimmerNummer zimmerNummer) implements DomainCommand {
}
