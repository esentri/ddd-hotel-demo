package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.ReservierungBereitsUebernommenException;
import com.esentri.hotel.rezeption.core.application.ZimmerBereitsVorhandenException;
import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = RezeptionExceptionHandler.class)
public class RezeptionExceptionHandler {

    @ExceptionHandler(DomainAssertionException.class)
    public ProblemDetail fachlicherFehler(DomainAssertionException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NichtGefundenException.class)
    public ProblemDetail nichtGefunden(NichtGefundenException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ReservierungBereitsUebernommenException.class, ZimmerBereitsVorhandenException.class})
    public ProblemDetail bereitsVorhanden(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
