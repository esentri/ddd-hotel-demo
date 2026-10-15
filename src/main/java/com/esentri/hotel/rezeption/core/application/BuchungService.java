package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckoutService;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

public class BuchungService implements ApplicationService {

    private final CheckoutService checkoutService;

    public BuchungService(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Transactional
    public void checkeAus(CheckeAus command) {
        checkoutService.checkeAus(command.buchungId());
    }
}
