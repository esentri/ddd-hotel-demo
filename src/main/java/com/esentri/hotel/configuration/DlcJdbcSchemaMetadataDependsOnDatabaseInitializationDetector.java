package com.esentri.hotel.configuration;

import io.domainlifecycles.jdbc.schema.JdbcSchemaMetadata;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;

import java.util.Set;

/**
 * DLC's JDBC backend reads the database schema once at startup ({@link JdbcSchemaMetadata}). This detector
 * makes Spring Boot create that bean only after the database initialization (Flyway) has run, so the
 * snapshot contains the fully migrated schema. Registered in {@code META-INF/spring.factories}.
 */
public class DlcJdbcSchemaMetadataDependsOnDatabaseInitializationDetector
        extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

    @Override
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Set.of(JdbcSchemaMetadata.class);
    }
}
