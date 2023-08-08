package uk.yetanother.cda.examples.modem.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.yetanother.cda.examples.modem.dtos.ModemConfiguration;
import uk.yetanother.cda.examples.modem.simulator.ModemSimulator;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ModemConfigurationPackHandler implements IConfigurationPackHandler {

    private final ModemSimulator simulator;
    private final ObjectMapper objectMapper;

    @Override
    public void handleNewPackReceived(ConfigurationPackDto configurationPack) {
        log.info(String.format("Informed of new configuration pack %s has been received.", configurationPack.getExternalReference()));
    }

    @Override
    public void handlePackDeletion(ConfigurationPackDto configurationPack) {
        log.info(String.format("Informed of configuration pack %s (%s) is being deleted.", configurationPack.getExternalReference(), configurationPack.getId()));
    }

    @Override
    public void scheduledChange(List<ConfigurationPackDto> configurationPacks, ConfigurationChangeType changeType) {
        log.info("Informed of scheduled change.");
        if (configurationPacks != null && !configurationPacks.isEmpty()) {
            configureModem(configurationPacks.get(0));
        }
    }

    @Override
    public void unScheduledChange(ConfigurationPackDto configurationPack, ConfigurationChangeType changeType) {
        log.info("Informed of unScheduled change.");
        this.configureModem(configurationPack);
    }

    private void configureModem(ConfigurationPackDto configurationPackDto) {
        try {
            ModemConfiguration modemConfiguration = objectMapper.readValue(configurationPackDto.getConfiguration(), ModemConfiguration.class);
            simulator.setActive(true);
            simulator.setKeysLoaded(true);
            simulator.setKeysValidUntil(modemConfiguration.getKeyValidUntil());
            simulator.setSatellite(modemConfiguration.getSatellite());
            simulator.setPower(modemConfiguration.getPower());
            simulator.setConfigurationAppliedOn(LocalDateTime.now());
            simulator.setConfigurationName(configurationPackDto.getExternalReference());
        } catch (JsonProcessingException e) {
            log.error("Error configuring modem", e);
        }
    }
}
