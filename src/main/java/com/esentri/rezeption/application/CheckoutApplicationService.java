package com.esentri.rezeption.application;

import com.esentri.rezeption.domain.model.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.model.buchung.CheckoutService;
import io.domainlifecycles.domain.types.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService implements ApplicationService {

    private final CheckoutService checkoutService;

    @Transactional
    public void checkout(CheckeGastAus command) {
        checkoutService.checkout(command);
    }
}
