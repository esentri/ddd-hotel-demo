package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.ZimmerApplicationService;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerUebersicht;
import com.esentri.hotel.rezeption.core.domain.zimmer.BeendeWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegung;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.PlaneWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ZimmerController {

    private final ZimmerApplicationService zimmerApplicationService;

    public ZimmerController(ZimmerApplicationService zimmerApplicationService) {
        this.zimmerApplicationService = zimmerApplicationService;
    }

    @PostMapping("/api/zimmer")
    public ResponseEntity<ZimmerAngelegt> legeZimmerAn(@RequestBody LegeZimmerAn legeZimmerAn) {
        var zimmerId = zimmerApplicationService.legeZimmerAn(legeZimmerAn);
        return ResponseEntity.created(URI.create("/api/zimmer/" + zimmerId.value())).body(new ZimmerAngelegt(zimmerId));
    }

    @GetMapping("/api/hotels/{hotelId}/freie-zimmer")
    public FreieZimmerUebersicht freieZimmer(@PathVariable HotelId hotelId) {
        return zimmerApplicationService.freieZimmerDerNaechstenZweiWochen(hotelId);
    }

    /**
     * Zimmer werden - wie an der Rezeption - ueber Hotel und Zimmernummer angesprochen, nicht ueber ihre ID.
     */
    @PostMapping("/api/hotels/{hotelId}/zimmer/{zimmernummer}/wartungen")
    public ResponseEntity<WartungGeplant> planeWartung(@PathVariable HotelId hotelId,
                                                       @PathVariable Zimmernummer zimmernummer,
                                                       @RequestBody WartungsAnfrage anfrage) {
        var belegungId = zimmerApplicationService.planeWartung(
            new PlaneWartung(hotelId, zimmernummer, anfrage.wartungsgrund(), anfrage.zeitraum()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new WartungGeplant(belegungId));
    }

    /**
     * Eine laufende Wartung endet heute, eine noch nicht begonnene entfaellt.
     */
    @PostMapping("/api/hotels/{hotelId}/zimmer/{zimmernummer}/wartungen/{belegungId}/beenden")
    public ResponseEntity<Void> beendeWartung(@PathVariable HotelId hotelId,
                                              @PathVariable Zimmernummer zimmernummer,
                                              @PathVariable Belegung.BelegungId belegungId) {
        zimmerApplicationService.beendeWartung(new BeendeWartung(hotelId, zimmernummer, belegungId));
        return ResponseEntity.noContent().build();
    }
}
