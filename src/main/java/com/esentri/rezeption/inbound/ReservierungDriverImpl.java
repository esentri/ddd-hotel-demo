package com.esentri.rezeption.inbound;

import com.esentri.rezeption.core.domain.reservierung.CheckInService;
import com.esentri.rezeption.core.domain.reservierung.CheckOutService;
import com.esentri.rezeption.core.domain.reservierung.CheckeAus;
import com.esentri.rezeption.core.domain.reservierung.CheckeEin;
import com.esentri.rezeption.core.domain.reservierung.ErstelleNeueReservierung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.reservierung.ReservierungsEingangService;
import com.esentri.rezeption.core.domain.reservierung.StorniereReservierung;
import com.esentri.rezeption.core.domain.reservierung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.inport.ReservierungDriver;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung des ReservierungDriver. Verantwortlich für die Verarbeitung von Reservierung-bezogenen Commands.
 *
 * @author Mario Herb
 */
@Service
@AllArgsConstructor
public class ReservierungDriverImpl implements ReservierungDriver {

    private final ReservierungRepository reservierungRepository;

    private final ReservierungsEingangService reservierungsEingangService;

    private final CheckInService checkInService;

    private final CheckOutService checkOutService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(ErstelleNeueReservierung erstelleNeueReservierung) {
        return reservierungsEingangService.handle(erstelleNeueReservierung);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten) {
        return reservierungRepository.findById(vervollstaendigeGastDaten.reservierungsNummer())
                .map(r ->
                        reservierungRepository.update(r.handle(vervollstaendigeGastDaten)))
                .map(Reservierung::id)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeEin checkeEin) {
        return checkInService.handle(checkeEin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(CheckeAus checkeAus) {
        return checkOutService.handle(checkeAus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Reservierung.ReservierungsNummer handle(StorniereReservierung storniereReservierung) {
        return reservierungRepository.findById(storniereReservierung.reservierungsNummer())
                .map(r ->
                        reservierungRepository.update(r.storniere()))
                .map(Reservierung::id)
                .orElseThrow();
    }


}
