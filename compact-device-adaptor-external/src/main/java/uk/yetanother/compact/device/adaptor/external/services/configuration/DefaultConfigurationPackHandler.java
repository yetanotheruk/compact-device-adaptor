package uk.yetanother.compact.device.adaptor.external.services.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;

import java.util.List;

@Service
@Slf4j
public class DefaultConfigurationPackHandler implements IConfigurationPackHandler {
    @Override
    public void handleNewPackReceived(ConfigurationPackDto configurationPack) {
        log.info(String.format("Informed of new configuration pack %s has been received.", configurationPack.getExternalReference()));
    }

    @Override
    public void handlePackDeletion(ConfigurationPackDto configurationPack) {
        log.info(String.format("Informed of configuration pack %s (%s) is being deleted.", configurationPack.getExternalReference(), configurationPack.getId()));
    }

    @Override
    public void scheduledChange(List<ConfigurationPackDto> configurationPacks) {
        log.info("Informed of scheduled change.");
    }

    @Override
    public void unScheduledChange(ConfigurationPackDto configurationPack) {
        log.info("Informed of unScheduled change.");
    }

    @Override
    public void scheduleCompleted() {
        log.info("Informed of scheduled completed.");
    }
}
