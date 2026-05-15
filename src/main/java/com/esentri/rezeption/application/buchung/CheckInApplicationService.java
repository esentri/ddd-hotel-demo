package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.CheckInService;
import com.esentri.rezeption.domain.buchung.CheckeGastEin;
import io.domainlifecycles.domain.types.ApplicationService;

import java.util.Objects;

public class CheckInApplicationService implements ApplicationService {

    private final CheckInService checkInService;

    public CheckInApplicationService(CheckInService checkInService) {
        this.checkInService = Objects.requireNonNull(checkInService);
    }

    public BuchungsId checkeGastEin(CheckeGastEin command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");

        checkInService.weiseZimmerZuUndCheckeEin(command.buchungsId(), command.zimmerId());

        return command.buchungsId();
    }
}
