package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.WalkInService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeWalkInEin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/rezeption/walk-ins")
class WalkInController {

    record WalkInResponse(UUID buchungId) {
    }

    private final WalkInService walkInService;

    WalkInController(WalkInService walkInService) {
        this.walkInService = walkInService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    WalkInResponse checkeWalkInEin(@RequestBody CheckeWalkInEin command) {
        return new WalkInResponse(walkInService.checkeWalkInEin(command).value());
    }
}
