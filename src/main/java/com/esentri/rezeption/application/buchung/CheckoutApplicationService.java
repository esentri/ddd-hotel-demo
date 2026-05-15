package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Application Service fuer den Checkout-Prozess.
 * Er koordiniert den Ablauf des Auscheckens eines Gastes.
 */
public class CheckoutApplicationService implements ApplicationService {

    private final BuchungRepository buchungRepository;

    public CheckoutApplicationService(BuchungRepository buchungRepository) {
        this.buchungRepository = Objects.requireNonNull(buchungRepository, "BuchungRepository darf nicht null sein");
    }

    /**
     * Fuehrt den Checkout fuer einen Gast aus.
     *
     * @param command Der Domain Command mit der BuchungsId
     * @return Die BuchungsId der ausgecheckten Buchung
     */
    @Transactional
    public BuchungsId checkeGastAus(CheckeGastAus command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");

        Buchung buchung = buchungRepository.findById(command.buchungsId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung mit ID " + command.buchungsId() + " wurde nicht gefunden"));

        buchung.checkeAus();

        buchungRepository.update(buchung);

        return buchung.id();
    }
}
