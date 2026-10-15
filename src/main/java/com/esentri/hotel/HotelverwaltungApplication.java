package com.esentri.hotel;

import io.domainlifecycles.autoconfig.annotation.EnableDlc;
import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDlc(dlcMirrorBasePackages = HotelverwaltungApplication.DOMAIN_BASE_PACKAGE)
@SpringBootApplication
public class HotelverwaltungApplication {

    public static final String DOMAIN_BASE_PACKAGE = "com.esentri.hotel";

    public static void main(String[] args) {
        // Bean Validation + validate() automatisch in Konstruktoren und Methoden der Domaenenklassen.
        // DLC 3.5.0: vor dem Laden der ersten Domaenenklasse aktivieren - Klassen, die erst waehrend der Aktivierung
        // geladen werden, bleiben sonst unerweitert. Ab DLC 3.5.1 genuegt @PostConstruct (Empfehlung der DLC-Doku).
        ValidationDomainClassExtender.extend(DOMAIN_BASE_PACKAGE);
        SpringApplication.run(HotelverwaltungApplication.class, args);
    }
}
