package uk.yetanother.compact.device.adaptor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigurationPackRepository extends JpaRepository<ConfigurationPack, UUID> {

    List<ConfigurationPack> findByExternalReference(String externalReference);

    List<ConfigurationPack> findAllByValidToIsBefore(LocalDateTime date);

    List<ConfigurationPack> findAllByValidToIsAfter(LocalDateTime date);

    List<ConfigurationPack> findAllByValidFromIs(LocalDateTime date);

    List<ConfigurationPack> findAllByValidFromIsGreaterThanEqualAndValidToIsBefore(LocalDateTime validFrom, LocalDateTime validTo);

}
