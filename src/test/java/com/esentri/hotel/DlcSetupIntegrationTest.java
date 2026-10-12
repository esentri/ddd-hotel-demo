package com.esentri.hotel;

import com.esentri.hotel.setupcheck.core.domain.SetupCheck;
import com.esentri.hotel.setupcheck.core.domain.SetupCheckRepository;
import io.domainlifecycles.assertion.DomainAssertionException;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.mirror.api.AggregateRootMirror;
import io.domainlifecycles.mirror.api.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class DlcSetupIntegrationTest {

    @Autowired
    SetupCheckRepository repository;

    @Autowired
    JdbcDomainPersistenceProvider persistenceProvider;

    @Test
    void mirrorKnowsTheAggregate() {
        AggregateRootMirror mirror = Domain.aggregateRootMirrorFor(SetupCheck.class.getName());
        assertThat(mirror.getTypeName()).isEqualTo(SetupCheck.class.getName());
    }

    @Test
    void jdbcBackendIsActive() {
        assertThat(persistenceProvider).isNotNull();
    }

    @Test
    void jdbcRepositoryRoundTrip() {
        var inserted = repository.insert(SetupCheck.create("first"));

        var found = repository.findById(inserted.id()).orElseThrow();
        assertThat(found.getName()).isEqualTo("first");
        assertThat(found.concurrencyVersion()).isEqualTo(inserted.concurrencyVersion());

        var versionBeforeUpdate = found.concurrencyVersion();
        var updated = repository.update(found.rename("second"));
        assertThat(updated.concurrencyVersion()).isEqualTo(versionBeforeUpdate + 1);
        assertThat(repository.findById(inserted.id()).orElseThrow().getName()).isEqualTo("second");

        assertThat(repository.deleteById(inserted.id())).isPresent();
        assertThat(repository.findById(inserted.id())).isEmpty();
    }

    @Test
    void validationExtenderRejectsInvalidAggregate() {
        assertThatThrownBy(() -> SetupCheck.create(" "))
            .isInstanceOf(DomainAssertionException.class);
    }
}
