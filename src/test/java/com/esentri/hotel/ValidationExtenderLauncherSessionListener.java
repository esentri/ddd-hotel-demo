package com.esentri.hotel;

import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * Aktiviert den Validation Extender einmal pro Test-JVM vor allen Tests (registriert in META-INF/services).
 * Reine Unit-Tests haben keinen Spring-Kontext, {@link HotelverwaltungApplication#main} laeuft in Tests nicht.
 */
public class ValidationExtenderLauncherSessionListener implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        ValidationDomainClassExtender.extend(HotelverwaltungApplication.DOMAIN_BASE_PACKAGE);
    }
}
