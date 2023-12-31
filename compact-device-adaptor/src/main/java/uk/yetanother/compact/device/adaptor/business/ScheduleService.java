package uk.yetanother.compact.device.adaptor.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.domain.jobs.ConfigurationPackChangeJob;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.yetanother.compact.device.adaptor.domain.jobs.ConfigurationPackChangeJob.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final Scheduler scheduler;

    public void scheduleConfigurationPackChanges(Set<LocalDateTime> changeDates, ConfigurationChangeType changeType) {
        if (changeDates != null) {
            for (LocalDateTime changeDate : changeDates) {
                scheduleConfigurationPackChange(changeDate, changeType);
            }
        }
    }

    public void scheduleConfigurationPackChange(LocalDateTime changeDate, ConfigurationChangeType type) {
        try {
            if (scheduler.checkExists(new JobKey(changeDate.toString(), JOB_GROUP_NAME))) {
                return; // If there is already a scheduled change at the same time skip adding another job.
            }
        } catch (SchedulerException e) {
            log.error(String.format("Unable to look up Quartz Job by name %s, skipping job creation.", changeDate), e);
            return;
        }

        JobDetail job = newJob(ConfigurationPackChangeJob.class)
                .withIdentity(changeDate.toString(), JOB_GROUP_NAME)
                .usingJobData(DATE_DATA_MAP_KEY, changeDate.toString())
                .usingJobData(TYPE_DATA_MAP_KEY, type.name())
                .build();

        Trigger trigger = newTrigger()
                .startAt(Date.from(changeDate.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduleJob(job, trigger);
    }

    public void scheduleJob(JobDetail job, Trigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to schedule job with Quartz.", e);
        }
    }

}
