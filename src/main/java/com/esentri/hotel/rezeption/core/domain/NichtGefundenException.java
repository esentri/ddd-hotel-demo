package com.esentri.hotel.rezeption.core.domain;

public class NichtGefundenException extends RuntimeException {

    public NichtGefundenException(String message) {
        super(message);
    }
}
