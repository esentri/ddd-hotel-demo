package com.esentri.hotel.rezeption.core.domain;

import io.domainlifecycles.mirror.api.Domain;
import io.domainlifecycles.mirror.reflect.ReflectiveDomainMirrorFactory;
import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;

/**
 * Domain-Mirror und Validation Extender fuer Unit-Tests ohne Spring-Kontext.
 */
public final class DomainTestSupport {

    private DomainTestSupport() {
    }

    public static void initDomain() {
        if (!Domain.isInitialized()) {
            Domain.initialize(new ReflectiveDomainMirrorFactory("com.esentri.hotel"));
        }
        ValidationDomainClassExtender.extend("com.esentri.hotel");
    }
}
