package com.esentri.hotel.setupcheck.outbound;

import com.esentri.hotel.setupcheck.core.domain.SetupCheck;
import com.esentri.hotel.setupcheck.core.domain.SetupCheckRepository;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;

public class JdbcSetupCheckRepository extends JdbcAggregateRepository<SetupCheck, SetupCheck.SetupCheckId>
        implements SetupCheckRepository {

    public JdbcSetupCheckRepository(JdbcDomainPersistenceProvider domainPersistenceProvider,
                                     PersistenceEventPublisher persistenceEventPublisher) {
        super(SetupCheck.class, domainPersistenceProvider, persistenceEventPublisher);
    }
}
