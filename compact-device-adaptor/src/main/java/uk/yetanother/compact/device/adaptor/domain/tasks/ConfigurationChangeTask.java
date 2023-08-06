package uk.yetanother.compact.device.adaptor.domain.tasks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Data
@Slf4j
public class ConfigurationChangeTask implements Callable<Boolean> {

    private final List<ConfigurationPackDto> configurationPacks;
    private final boolean scheduledChange;
    private final ConfigurationChangeType changeType;
    private final IConfigurationPackHandler configurationPackHandler;

    @Override
    public Boolean call() {
        try {
            log.trace("Running task for configuration change.");
            if (scheduledChange) {
                configurationPackHandler.scheduledChange(configurationPacks, changeType);
            } else if (configurationPacks != null && configurationPacks.size() == 1) {
                configurationPackHandler.unScheduledChange(configurationPacks.get(0), changeType);
            } else {
                log.error("Failed to handle unScheduled change as there were no, or too many configuration packs provided.");
            }
            log.trace("Finished task for configuration change");
            return true;
        } catch (Exception e) {
            log.error("Error running task for configuration change.", e);
            throw e;
        }
    }

}
