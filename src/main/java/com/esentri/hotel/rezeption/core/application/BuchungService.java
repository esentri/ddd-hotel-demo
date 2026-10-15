package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

public class BuchungService implements ApplicationService {

    private final BuchungRepository buchungRepository;

    public BuchungService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    @Transactional
    public void checkeAus(CheckeAus command) {
        var buchung = buchungRepository.findById(command.buchungId());
        DomainAssertions.isTrue(buchung.isPresent(), "Die Buchung ist nicht bekannt.");
        buchung.get().checkeAus();
        buchungRepository.update(buchung.get());
    }
}
