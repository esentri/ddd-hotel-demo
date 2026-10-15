package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungszeitraum;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeWalkInEin;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckinService;
import com.esentri.hotel.rezeption.core.domain.buchung.Gast;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

public class WalkInService implements ApplicationService {

    private final CheckinService checkinService;
    private final Clock clock;

    public WalkInService(CheckinService checkinService, Clock clock) {
        this.checkinService = checkinService;
        this.clock = clock;
    }

    @Transactional
    public Buchung.BuchungId checkeWalkInEin(CheckeWalkInEin command) {
        return checkinService.checkeWalkInEin(
                Gast.neu(command.vorname(), command.nachname(), command.adresse(), command.geburtsdatum()),
                new Buchungszeitraum(LocalDate.now(clock), command.abreise()),
                command.gewuenschteZimmerkategorie(),
                command.zimmerId())
            .getId();
    }
}
