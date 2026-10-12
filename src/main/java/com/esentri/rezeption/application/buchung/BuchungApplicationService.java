package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.buchung.AktualisiereGastdaten;
import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsFactory;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.CheckInService;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.buchung.CheckeGastEin;
import com.esentri.rezeption.domain.buchung.LegeBuchungAn;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public class BuchungApplicationService implements ApplicationService {

    private final BuchungRepository buchungRepository;
    private final CheckInService checkInService;
    private final BuchungsFactory buchungsFactory;

    public BuchungApplicationService(BuchungRepository buchungRepository, CheckInService checkInService,
                                     BuchungsFactory buchungsFactory) {
        this.buchungRepository = Objects.requireNonNull(buchungRepository, "BuchungRepository darf nicht null sein");
        this.checkInService = Objects.requireNonNull(checkInService, "CheckInService darf nicht null sein");
        this.buchungsFactory = Objects.requireNonNull(buchungsFactory, "BuchungsFactory darf nicht null sein");
    }

    /**
     * Legt eine neue Buchung an.
     *
     * @param command Der Domain Command mit den Gastdaten und dem Belegungszeitraum
     * @return Die BuchungsId der neuen Buchung
     */
    @Transactional
    public BuchungsId legeBuchungAn(LegeBuchungAn command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");
        Buchung buchung = buchungsFactory.reserviere(
            command.vorname(),
            command.nachname(),
            command.geburtsdatum(),
            command.belegungszeitraum()
        );
        buchungRepository.insert(buchung);
        return buchung.id();
    }

    @Transactional
    public BuchungsId aktualisiereGastdaten(AktualisiereGastdaten command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");

        Buchung buchung = buchungRepository.findById(command.buchungsId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung mit ID " + command.buchungsId() + " nicht gefunden"));

        buchung.aktualisiereGastdaten(
            command.vorname(),
            command.nachname(),
            command.geburtsdatum()
        );

        buchungRepository.update(buchung);

        return buchung.id();
    }

    /**
     * Fuehrt den Check-In fuer einen Gast aus.
     *
     * @param command Der Domain Command mit der BuchungsId und ZimmerId
     * @return Die BuchungsId
     */
    @Transactional
    public BuchungsId checkeGastEin(CheckeGastEin command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");

        checkInService.weiseZimmerZuUndCheckeEin(command);

        return command.buchungsId();
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
