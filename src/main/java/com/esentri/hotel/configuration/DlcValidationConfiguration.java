package com.esentri.hotel.configuration;

import io.domainlifecycles.mirror.api.DomainMirror;
import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Activates the DLC validation extender: Bean Validation constraints and {@code validate()} are checked
 * automatically after every constructor and every non-getter method of the domain classes.
 * Validation is not part of the DLC Spring autoconfig, so it is enabled here explicitly.
 */
@Configuration
public class DlcValidationConfiguration {

    /**
     * The domain mirror is injected only to run the extension after the mirror initialization: the extender
     * reliably instruments domain classes that are already loaded at that point.
     */
    public DlcValidationConfiguration(DomainMirror domainMirror) {
    }

    @PostConstruct
    public void extendDomainClasses() {
        ValidationDomainClassExtender.extend("com.esentri.hotel");
    }
}
