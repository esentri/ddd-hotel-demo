package com.esentri.hotel.configuration;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Verletzte fachliche Regeln aus dem Domaenenmodell werden als Client-Fehler beantwortet, nicht als Serverfehler.
 */
@RestControllerAdvice
class DomainAssertionExceptionHandler {

    @ExceptionHandler(DomainAssertionException.class)
    ProblemDetail handle(DomainAssertionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
