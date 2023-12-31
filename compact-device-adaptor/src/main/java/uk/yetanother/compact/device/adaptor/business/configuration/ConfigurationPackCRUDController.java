package uk.yetanother.compact.device.adaptor.business.configuration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.business.ScheduleService;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;
import uk.yetanother.compact.device.adaptor.repositories.ConfigurationPackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigurationPackCRUDController {

    private final IConfigurationPackHandler configurationPackHandler;
    private final ConfigurationPackMapper configurationPackMapper;
    private final ConfigurationPackRepository repository;
    private final ScheduleService scheduleService;

    public ConfigurationPack handleNewPackReceived(ConfigurationPack configurationPack) {
        ConfigurationPackDto newPack = configurationPackMapper.toDto(configurationPack);
        configurationPackHandler.handleNewPackReceived(newPack);
        ConfigurationPack savedPack = save(configurationPackMapper.fromDto(newPack));

        // If the Configuration Pack is due to be activated in the future schedule this.
        if (LocalDateTime.now().isBefore(savedPack.getValidFrom())) {
            scheduleService.scheduleConfigurationPackChange(savedPack.getValidFrom(), ConfigurationChangeType.START);
            // If no other pack is due to take over after this one, schedule the tear down too.
            if (getConfigurationPacksValidOnDate(savedPack.getValidTo()).isEmpty()) {
                scheduleService.scheduleConfigurationPackChange(savedPack.getValidTo(), ConfigurationChangeType.STOP);
            }
        }
        return savedPack;
    }

    public void handlePackDeletion(UUID id) {
        configurationPackHandler.handlePackDeletion(configurationPackMapper.toDto(getById(id)));
        delete(id);
    }

    public ConfigurationPack save(ConfigurationPack configurationPack) {
        return repository.save(configurationPack);
    }

    public ConfigurationPack getById(UUID id) {
        Optional<ConfigurationPack> searchResult = repository.findById(id);
        if (searchResult.isPresent()) {
            Hibernate.initialize(searchResult.get().getSupportFiles());
            return searchResult.get();
        }
        return null;
    }

    public Page<ConfigurationPack> getAll(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    public List<ConfigurationPack> getExpiredConfigurationPacks() {
        return repository.findAllByValidToIsBefore(LocalDateTime.now());
    }

    public List<ConfigurationPack> getNonExpiredConfigurationPacks() {
        return repository.findAllByValidToIsAfter(LocalDateTime.now());
    }

    public List<ConfigurationPack> getConfigurationPacksValidOnDate(LocalDateTime dateTime) {
        List<ConfigurationPack> configurationPacks = repository.findAllByValidFromIs(dateTime);
        for (ConfigurationPack configurationPack : configurationPacks) {
            Hibernate.initialize(configurationPack.getSupportFiles());
        }
        return configurationPacks;
    }

    public List<ConfigurationPack> getConfigurationPacksValidForDate(LocalDateTime dateTime) {
        return repository.findAllByValidFromIsGreaterThanEqualAndValidToIsBefore(dateTime, dateTime);
    }

    public List<ConfigurationPack> getByExternalReference(String externalReference) {
        return repository.findByExternalReference(externalReference);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

}
