package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.BuchungApplicationService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Checkout an der Rezeption: der Gast wird ueber Hotel und Zimmernummer ausgecheckt.
 */
@RestController
public class CheckOutController {

    private final BuchungApplicationService buchungApplicationService;

    public CheckOutController(BuchungApplicationService buchungApplicationService) {
        this.buchungApplicationService = buchungApplicationService;
    }

    @PostMapping("/api/hotels/{hotelId}/zimmer/{zimmernummer}/checkout")
    public ResponseEntity<Void> checkeAus(@PathVariable HotelId hotelId, @PathVariable Zimmernummer zimmernummer) {
        buchungApplicationService.checkeAus(new CheckeAus(hotelId, zimmernummer));
        return ResponseEntity.noContent().build();
    }
}
