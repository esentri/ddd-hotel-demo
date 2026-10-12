package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.BuchungApplicationService;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeSpontanEin;
import com.esentri.hotel.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.hotel.rezeption.core.domain.buchung.UebernimmReservierung;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/buchungen")
public class BuchungController {

    private final BuchungApplicationService buchungApplicationService;

    public BuchungController(BuchungApplicationService buchungApplicationService) {
        this.buchungApplicationService = buchungApplicationService;
    }

    /**
     * Einstiegspunkt fuer das externe Reservierungssystem.
     */
    @PostMapping
    public ResponseEntity<BuchungAngelegt> uebernimmReservierung(@RequestBody UebernimmReservierung reservierung) {
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierung);
        return ResponseEntity.created(URI.create("/api/buchungen/" + buchungsId.value())).body(new BuchungAngelegt(buchungsId));
    }

    @PostMapping("/{buchungsId}/checkin")
    public ResponseEntity<Void> checkeEin(@PathVariable Buchung.BuchungsId buchungsId,
                                          @RequestBody CheckInAnfrage anfrage) {
        buchungApplicationService.checkeEin(anfrage.fuer(buchungsId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{buchungsId}/stornierung")
    public ResponseEntity<Void> storniere(@PathVariable Buchung.BuchungsId buchungsId) {
        buchungApplicationService.storniere(new StorniereBuchung(buchungsId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/spontan-checkin")
    public ResponseEntity<BuchungAngelegt> checkeSpontanEin(@RequestBody CheckeSpontanEin checkIn) {
        var buchungsId = buchungApplicationService.checkeSpontanEin(checkIn);
        return ResponseEntity.created(URI.create("/api/buchungen/" + buchungsId.value())).body(new BuchungAngelegt(buchungsId));
    }
}
