package com.esentri.rezeption.inbound;

import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerReservierung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleServiceRechnung;
import com.esentri.rezeption.core.domain.rechnung.MarkiereRechnungBezahlt;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.rechnung.RechnungsErstellungsService;
import com.esentri.rezeption.core.inport.RechnungDriver;
import com.esentri.rezeption.core.outport.RechnungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung des RechnungDrivers. Verantwortlich für die Verarbeitung von Rechnungs-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@RequiredArgsConstructor
public class RechnungDriverImpl implements RechnungDriver {

    private final RechnungRepository rechnungRepository;

    private final RechnungsErstellungsService rechnungsErstellungsService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.RechnungId handle(MarkiereRechnungBezahlt markiereRechnungBezahlt) {
        return rechnungRepository.findById(markiereRechnungBezahlt.rechnungId())
            .map(r -> rechnungRepository.update(r.markiereBezahlt()).getId())
            .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.RechnungId handle(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung) {
        return rechnungsErstellungsService.handle(erstelleRechnungFuerReservierung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Rechnung.RechnungId handle(ErstelleServiceRechnung erstelleServiceRechnung) {
        return rechnungsErstellungsService.handle(erstelleServiceRechnung);
    }

}
