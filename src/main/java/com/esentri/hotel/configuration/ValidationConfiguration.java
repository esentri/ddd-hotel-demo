package com.esentri.hotel.configuration;

import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Aktiviert den DLC Validation Extender: Bean Validation und {@code validate()} laufen automatisch
 * am Ende jedes Konstruktors und jeder nicht lesenden Methode der Domaenenklassen.
 */
@Configuration
public class ValidationConfiguration {

    @PostConstruct
    public void extendDomainClassesWithValidation() {
        ValidationDomainClassExtender.extend("com.esentri.hotel");
    }
}
