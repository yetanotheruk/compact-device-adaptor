package uk.yetanother.compact.device.adaptor.domain.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigurationPackChangeJob extends QuartzJobBean {

    public static final String JOB_GROUP_NAME = "CONFIGURATION_CHANGE";
    public static final String DATE_DATA_MAP_KEY = "date";
    public static final String TYPE_DATA_MAP_KEY = "changeType";

    private final ConfigurationPackCRUDController configurationPackCRUDController;
    private final ConfigurationPackActionController configurationPackActionController;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime scheduledDate = LocalDateTime.parse(context.getJobDetail().getJobDataMap().get(DATE_DATA_MAP_KEY).toString());
        List<ConfigurationPack> configurationPacks = configurationPackCRUDController.getConfigurationPacksValidOnDate(scheduledDate);
        if (configurationPacks == null || configurationPacks.isEmpty()) {
            log.warn(String.format("Scheduled to start configuration change at %s, but no packs where identified by the Valid From Date", scheduledDate));
            return;
        }

        StringBuilder builder = new StringBuilder(String.format("Configuration Pack Change for %s has started, the following packs were identified.", scheduledDate));
        for (ConfigurationPack pack : configurationPacks) {
            builder.append(String.format("%s (%s)", pack.getExternalReference(), pack.getId()));
        }
        log.info(builder.toString());
        configurationPackActionController.scheduledChange(configurationPacks, ConfigurationChangeType.valueOf(context.getJobDetail().getJobDataMap().get(TYPE_DATA_MAP_KEY).toString()));
    }

}
