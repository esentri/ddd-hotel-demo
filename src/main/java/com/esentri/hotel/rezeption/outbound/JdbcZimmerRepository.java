package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.jdbc.util.JdbcRecordMapper;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;

import java.util.Optional;

class JdbcZimmerRepository extends JdbcAggregateRepository<Zimmer, Zimmer.ZimmerId> implements ZimmerRepository {

    private final JdbcDomainPersistenceProvider provider;

    JdbcZimmerRepository(JdbcDomainPersistenceProvider provider, PersistenceEventPublisher publisher) {
        super(Zimmer.class, provider, publisher);
        this.provider = provider;
    }

    @Override
    public Optional<Zimmer> findByZimmernummer(Zimmernummer zimmernummer) {
        var record = JdbcRecordMapper.selectOneByColumn(provider.connectionProvider, provider.dialect,
            provider.schemaMetadata.table("ZIMMER"), "ZIMMERNUMMER_VALUE", zimmernummer.value());
        return Optional.ofNullable(record).flatMap(r -> getFetcher().fetchDeep(r).resultValue());
    }
}
