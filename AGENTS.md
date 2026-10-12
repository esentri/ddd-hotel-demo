# ddd-hotel-demo

Build: `./gradlew build` (Java 21, Spring Boot 4.1, Gradle wrapper).

## DomainLifeCycles

For any work on domain code, load the skills `ddd-tactical-java` and
`domainlifecycles` first; for tables/migrations also `dlc-persistence-schema`.
Mode: C (runtime, Spring Boot starter `io.domainlifecycles:spring-boot-starter:3.5.0`)
Domain base package: com.esentri.hotel
Persistence: JDBC (DLC `jdbc-integration`, no jOOQ, no codegen), H2, Flyway migrations in `src/main/resources/db/migration`
Validation style: BeanValidation+DomainAssertions (validation extender, activated in `configuration.DlcValidationConfiguration`)
Event publishing: DomainEvents
Package layout: default (one root package per bounded context with `@BoundedContext` in `package-info.java`)
Legacy contexts: none
Code language: German domain terms, no umlauts/ss in identifiers (ae, oe, ue, ss)
Deviations from the skills: none

### Project-specific notes
- Aggregates and entities need a Lombok `@Builder` on the full constructor - DLC's
  `InnerClassDomainObjectBuilderProvider` uses it to rebuild domain objects (persistence, JSON).
- The JDBC schema snapshot is read once at startup; `DlcJdbcSchemaMetadataDependsOnDatabaseInitializationDetector`
  (registered in `META-INF/spring.factories`) makes sure this happens after Flyway has migrated.
- Tables live in schema `PUBLIC` (`dlc.features.persistence.jdbc.schema-pattern`).
- `springdoc.use-fqn=true` is required by the DLC OpenAPI integration.
- `src/test/.../setupcheck` is a test-only bounded context (table from `src/test/resources/db/testmigration`)
  that verifies mirror, JDBC round trip and validation extender (`DlcSetupIntegrationTest`).
