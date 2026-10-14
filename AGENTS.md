# ddd-hotel-demo

Build: `./gradlew build` (Java 21, Spring Boot 4.1, Gradle wrapper).
Diagram Viewer upload (project `DDD_Hotel_Demo_Skills`, local viewer on http://localhost:8090):
`DLC_VIEWER_API_KEY=<key> ./gradlew domainModelUpload` - never store the key in files.

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
- `DlcValidationConfiguration` injects the `DomainMirror` on purpose: the validation extender only instruments
  domain classes already loaded when it runs, so it must run after the mirror initialization.
  `BuchungControllerIntegrationTest#ungueltigeEmailWirdAbgelehnt` guards this.
- Own finder methods in JDBC repositories load the root row via `JdbcRecordMapper` (`selectOneByColumn` /
  `selectOne`, table name from `provider.entityRecordType(...)`) and build the aggregate with
  `getFetcher().fetchDeep(record)` - no ID lookup followed by `findById`.
- REST responses never return a bare `Identity` as JSON root: DLC Jackson writes it as `{"value": ...}` there.
  Wrap it in a small response record (e.g. `BuchungAngelegt`), where it is written as a plain value.
- Domain events: `@DomainEventListener` methods of services run after commit, in their own transaction and
  asynchronously (`@EnableAsync` in `DlcEventsConfiguration`). Events must be published inside a transaction
  (`@Transactional` application service), otherwise they are dropped. Tests wait for listener effects with
  Awaitility. Example: checkout changes only the `Buchung`; the `Zimmer` is released by
  `ZimmerApplicationService.onGastAusgecheckt`.
- Spring tests use a fixed `Clock` (`TestClockConfiguration`, 2026-11-02 = arrival day of the test data).
- Tables live in schema `PUBLIC` (`dlc.features.persistence.jdbc.schema-pattern`).
- `springdoc.use-fqn=true` is required by the DLC OpenAPI integration.
- `src/test/.../setupcheck` is a test-only bounded context (table from `src/test/resources/db/testmigration`)
  that verifies mirror, JDBC round trip and validation extender (`DlcSetupIntegrationTest`).
