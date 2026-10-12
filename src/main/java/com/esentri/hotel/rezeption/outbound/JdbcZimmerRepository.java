package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.jdbc.imp.JdbcAggregateRepository;
import io.domainlifecycles.jdbc.imp.provider.JdbcDomainPersistenceProvider;
import io.domainlifecycles.jdbc.util.JdbcRecordMapper;
import io.domainlifecycles.persistence.repository.PersistenceEventPublisher;

import java.util.Optional;

public class JdbcZimmerRepository extends JdbcAggregateRepository<Zimmer, Zimmer.ZimmerId>
        implements ZimmerRepository {

    private final JdbcDomainPersistenceProvider provider;

    public JdbcZimmerRepository(JdbcDomainPersistenceProvider domainPersistenceProvider,
                                PersistenceEventPublisher persistenceEventPublisher) {
        super(Zimmer.class, domainPersistenceProvider, persistenceEventPublisher);
        this.provider = domainPersistenceProvider;
    }

    @Override
    public Optional<Zimmer> findByHotelUndZimmernummer(HotelId hotelId, Zimmernummer zimmernummer) {
        var zimmer = provider.schemaMetadata.table(provider.entityRecordType(Zimmer.class.getName()));
        var zimmerRecord = JdbcRecordMapper.selectOne(provider.connectionProvider, zimmer,
            "SELECT * FROM %s WHERE hotel_id = ? AND zimmernummer_value = ?".formatted(zimmer.qualifiedName()),
            hotelId.value(), zimmernummer.value());
        return Optional.ofNullable(zimmerRecord).flatMap(r -> getFetcher().fetchDeep(r).resultValue());
    }
}
