package com.esentri.rezeption.infrastructure.persistence.zimmer;

import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.jdbc.util.JdbcRecordMapper;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ZimmerRepositoryJdbc extends JdbcAggregateRepository<Zimmer, ZimmerId> implements ZimmerRepository {

    private final JdbcDomainPersistenceProvider domainPersistenceProvider;

    public ZimmerRepositoryJdbc(JdbcDomainPersistenceProvider domainPersistenceProvider,
                                PersistenceEventPublisher persistenceEventPublisher) {
        super(Zimmer.class, domainPersistenceProvider, persistenceEventPublisher);
        this.domainPersistenceProvider = domainPersistenceProvider;
    }

    @Override
    public Optional<Zimmer> findById(ZimmerId id) {
        return super.findById(id);
    }

    @Override
    public Zimmer insert(Zimmer zimmer) {
        return super.insert(zimmer);
    }

    @Override
    public Zimmer update(Zimmer zimmer) {
        return super.update(zimmer);
    }

    @Override
    public List<Zimmer> findByKategorie(Zimmerkategorie kategorie) {
        return JdbcRecordMapper.selectByColumn(
                domainPersistenceProvider.connectionProvider,
                domainPersistenceProvider.dialect,
                domainPersistenceProvider.schemaMetadata.table("ZIMMER"),
                "KATEGORIE",
                kategorie.name())
            .stream()
            .map(r -> getFetcher().fetchDeep(r).resultValue().orElseThrow())
            .toList();
    }

    @Override
    public Optional<Zimmer> deleteById(ZimmerId id) {
        var deleted = findById(id);
        super.deleteById(id);
        return deleted;
    }
}
