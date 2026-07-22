package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.buchung.GastAusgecheckt;
import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.DomainEventListener;

import java.util.Objects;

/**
 * Listener, der auf das GastAusgecheckt Domain Event reagiert
 * und die Freigabe des Zimmers veranlasst.
 */
public class ZimmerFreigabeListener implements ApplicationService {

    private final ZimmerRepository zimmerRepository;

    public ZimmerFreigabeListener(ZimmerRepository zimmerRepository) {
        this.zimmerRepository = Objects.requireNonNull(zimmerRepository, "ZimmerRepository darf nicht null sein");
    }

    /**
     * Reagiert auf das Event GastAusgecheckt.
     * Öffnet eine eigene Transaktion, um die Zimmerfreigabe entkoppelt vom Checkout durchzuführen.
     *
     * @param event Das Domain Event
     */
    @DomainEventListener
    public void aufGastAusgecheckt(GastAusgecheckt event) {
        Objects.requireNonNull(event, "Event darf nicht null sein");

        Zimmer zimmer = zimmerRepository.findById(event.zimmerId())
            .orElseThrow(() -> new IllegalArgumentException("Zimmer mit ID " + event.zimmerId() + " nicht gefunden"));

        zimmer.gibFrei(event.buchungsId());

        zimmerRepository.update(zimmer);
    }
}
