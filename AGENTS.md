# Hotelverwaltung

Demo-Projekt fuer taktisches Domain-Driven Design mit DomainLifeCycles (DLC).

## Build

- Java 21, Gradle (Wrapper), Spring Boot 4.1, DLC 3.5.0 (`dlcVersion` in `gradle.properties`)
- Bauen und testen: `./gradlew build`
- Starten: `./gradlew bootRun` (H2 in-memory, Konsole unter `/h2-console`, OpenAPI unter `/swagger-ui.html`)
- Domaenendiagramm: `./gradlew createDiagram` (Ausgabe in `build/`)

## DomainLifeCycles

For any work on domain code, load the skills `ddd-tactical-java` and
`domainlifecycles` first.
Mode: C (runtime, Spring Boot starter, JDBC persistence)
Domain base package: com.esentri.hotel
Persistence: JDBC (jOOQ excluded in `build.gradle`), H2, Flyway migrations in `src/main/resources/db/migration`
Validation style: BeanValidation+DomainAssertions (validation extender, activated in `com.esentri.hotel.configuration.ValidationConfiguration`)
Event publishing: DomainEvents
Package layout: default (ddd-tactical-java) - one root package per bounded context `com.esentri.hotel.<bc>` with `package-info.java` + `@BoundedContext`; application-wide Spring configuration in `com.esentri.hotel.configuration`
Legacy contexts: none
Code language: domain terms in German (no umlauts/ss in identifiers: ae, oe, ue, ss), technical terms in English (e.g. Repository, Service, Configuration, Id, Command, Event, Handler suffixes and infrastructure classes)
Glossary: docs/ubiquitous-language.md
Deviations from the skills: none
