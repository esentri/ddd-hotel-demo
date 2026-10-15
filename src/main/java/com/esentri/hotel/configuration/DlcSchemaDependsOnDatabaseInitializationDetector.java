package com.esentri.hotel.configuration;

import io.domainlifecycles.jdbc.schema.JdbcSchemaMetadata;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;

import java.util.Set;

/**
 * DLC liest die Schema-Metadaten einmalig beim Start. Damit das erst nach den Flyway-Migrationen passiert,
 * wird die Bean als abhaengig von der Datenbankinitialisierung markiert (registriert in META-INF/spring.factories).
 */
public class DlcSchemaDependsOnDatabaseInitializationDetector
    extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

    @Override
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Set.of(JdbcSchemaMetadata.class);
    }
}
