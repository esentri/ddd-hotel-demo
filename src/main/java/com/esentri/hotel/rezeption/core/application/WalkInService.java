package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungszeitraum;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeWalkInEin;
import com.esentri.hotel.rezeption.core.domain.buchung.Gast;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

public class WalkInService implements ApplicationService {

    private final BuchungRepository buchungRepository;
    private final Clock clock;

    public WalkInService(BuchungRepository buchungRepository, Clock clock) {
        this.buchungRepository = buchungRepository;
        this.clock = clock;
    }

    @Transactional
    public Buchung.BuchungId checkeWalkInEin(CheckeWalkInEin command) {
        var buchung = Buchung.walkIn(
            Gast.neu(command.vorname(), command.nachname()),
            new Buchungszeitraum(LocalDate.now(clock), command.abreise()),
            command.gewuenschteZimmerkategorie());
        return buchungRepository.insert(buchung).getId();
    }
}
