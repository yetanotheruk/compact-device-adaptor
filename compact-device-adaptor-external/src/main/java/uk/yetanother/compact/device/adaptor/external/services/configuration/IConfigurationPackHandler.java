package uk.yetanother.compact.device.adaptor.external.services.configuration;

import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;

import java.util.List;

public interface IConfigurationPackHandler {

    void handleNewPackReceived(ConfigurationPackDto configurationPack);

    void handlePackDeletion(ConfigurationPackDto configurationPack);

    void scheduledChange(List<ConfigurationPackDto> configurationPacks);

    void unScheduledChange(ConfigurationPackDto configurationPack);

    void scheduleCompleted();

}
