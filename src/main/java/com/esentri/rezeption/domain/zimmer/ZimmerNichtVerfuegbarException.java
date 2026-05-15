package com.esentri.rezeption.domain.zimmer;

public class ZimmerNichtVerfuegbarException extends RuntimeException {
    public ZimmerNichtVerfuegbarException(String message) {
        super(message);
    }
}
