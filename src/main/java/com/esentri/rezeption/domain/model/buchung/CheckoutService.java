package com.esentri.rezeption.domain.model.buchung;

import com.esentri.rezeption.domain.model.rechnung.Rechnung;
import com.esentri.rezeption.domain.model.rechnung.RechnungId;
import com.esentri.rezeption.domain.model.rechnung.RechnungRepository;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final RechnungRepository rechnungRepository;

    @Publishes(domainEventTypes = GastAusgecheckt.class)
    public void checkout(CheckeGastAus command) {
        Buchung buchung = buchungRepository.findById(command.buchungId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung nicht gefunden: " + command.buchungId()));

        buchung.checkout();

        // Einfache Rechnungslogik für das Demo
        BigDecimal netto = new BigDecimal("100.00");
        BigDecimal brutto = netto.multiply(new BigDecimal("1.19"));
        Rechnung rechnung = new Rechnung(
            new RechnungId(UUID.randomUUID()),
            buchung.id(),
            netto,
            brutto
        );

        // Speichere Rechnung und Buchungsstatus (Zimmerfreigabe wird hier noch nicht explizit behandelt)
        rechnungRepository.insert(rechnung);
        buchungRepository.update(buchung);

        buchung.getZugewiesenesZimmerId().ifPresent(zimmerId ->
            DomainEvents.publish(new GastAusgecheckt(buchung.id(), zimmerId))
        );
    }
}
