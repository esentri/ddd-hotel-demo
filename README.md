# DDD Hotel Demo - Hotelverwaltung

Hotelverwaltung als Demo fuer taktisches Domain-Driven Design mit
[DomainLifeCycles](https://github.com/domainlifecycles/domainlifecycles) (DLC 3.5.0).

- Java 21, Spring Boot 4.1, Gradle
- DLC Spring Boot Starter mit JDBC-Persistenz (H2 + Flyway)
- Validierung: Bean Validation + DomainAssertions ueber den DLC Validation Extender

```shell
./gradlew build      # bauen und testen
./gradlew bootRun    # Anwendung starten
./gradlew createDiagram # Domaenendiagramm nach build/ erzeugen
```

Projektentscheidungen und Konventionen: [AGENTS.md](AGENTS.md),
Fachbegriffe: [docs/ubiquitous-language.md](docs/ubiquitous-language.md).
