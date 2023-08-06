package uk.yetanother.compact.device.adaptor.business.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.business.ScheduleService;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.domain.tasks.ConfigurationChangeTask;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationPackActionController {

    private final IConfigurationPackHandler configurationPackHandler;
    private final ConfigurationPackCRUDController configurationPackCRUDController;
    private final ConfigurationPackMapper configurationPackMapper;
    private final ScheduleService scheduleService;

    private ExecutorService executor;

    @PostConstruct
    public void setup() {
        executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void tearDown() {
        executor.shutdownNow();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void buildSchedule() {
        log.info("Building Configuration Pack Change Schedule");
        Set<LocalDateTime> uniqueStartDates = new HashSet<>();
        Set<LocalDateTime> uniqueEndDates = new HashSet<>();
        for (ConfigurationPack configurationPack : configurationPackCRUDController.getNonExpiredConfigurationPacks()) {
            log.trace(String.format("Scheduling Configuration %s (%s) with the change date of %s to %s.", configurationPack.getExternalReference(), configurationPack.getId(), configurationPack.getValidFrom(), configurationPack.getValidTo()));
            uniqueStartDates.add(configurationPack.getValidFrom());
            uniqueEndDates.add(configurationPack.getValidTo());
        }
        log.info("Generated schedule for the given change start dates " + uniqueStartDates);

        scheduleService.scheduleConfigurationPackChanges(uniqueStartDates, ConfigurationChangeType.START);
        // Remove any end dates where another configuration starts
        uniqueEndDates.removeAll(uniqueStartDates);
        log.info("Generated schedule for the given change end dates " + uniqueEndDates);
        scheduleService.scheduleConfigurationPackChanges(uniqueEndDates, ConfigurationChangeType.STOP);
    }

    public void scheduledChange(List<ConfigurationPack> configurationPacks, ConfigurationChangeType changeType) {
        executor.submit(new ConfigurationChangeTask(configurationPackMapper.allToDto(configurationPacks), true, changeType, configurationPackHandler));
    }

    public void unScheduledChange(UUID id, ConfigurationChangeType changeType) {
        ConfigurationPack configurationPack = configurationPackCRUDController.getById(id);
        if (configurationPack != null) {
            log.info(String.format("Unscheduled change for configuration %s (%s).", configurationPack.getExternalReference(), configurationPack.getId()));
            executor.submit(new ConfigurationChangeTask(Collections.singletonList(configurationPackMapper.toDto(configurationPack)), false, changeType, configurationPackHandler));
        } else {
            log.trace(String.format("No configuration pack found for id %s, to activate.", id));
            throw new ResponseStatusException(BAD_REQUEST, String.format("No configuration pack found for id %s, to activate.", id));
        }
    }
}
