package uk.yetanother.compact.device.adaptor.business;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.quartz.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.domain.jobs.ConfigurationPackChangeJob;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.time.ZoneId.systemDefault;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.yetanother.compact.device.adaptor.domain.jobs.ConfigurationPackChangeJob.JOB_GROUP_NAME;

@ExtendWith(SpringExtension.class)
class ScheduleServiceTest {

    @Mock
    private Scheduler scheduler;

    @Captor
    private ArgumentCaptor<JobDetail> jobDetailCaptor;

    @Captor
    private ArgumentCaptor<Trigger> triggerCaptor;

    private ScheduleService classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ScheduleService(scheduler);
    }

    @SneakyThrows
    @Test
    void scheduleConfigurationPackChanges() {
        LocalDateTime job1 = LocalDateTime.now().plusDays(1);
        LocalDateTime job2 = LocalDateTime.now().plusDays(2);
        classUnderTest.scheduleConfigurationPackChanges(new HashSet<>(Arrays.asList(job1, job2)), ConfigurationChangeType.START);
        verify(scheduler, times(2)).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        List<JobDetail> jobDetails = jobDetailCaptor.getAllValues();
        List<Trigger> triggers = triggerCaptor.getAllValues();
        JobDetail jobDetail1;
        Trigger trigger1;
        JobDetail jobDetail2;
        Trigger trigger2;
        if (jobDetails.get(0).getKey().getName().equals(job1.toString())) {
            jobDetail1 = jobDetails.get(0);
            trigger1 = triggers.get(0);
            jobDetail2 = jobDetails.get(1);
            trigger2 = triggers.get(1);

        } else {
            jobDetail1 = jobDetails.get(1);
            trigger1 = triggers.get(1);
            jobDetail2 = jobDetails.get(0);
            trigger2 = triggers.get(0);
        }

        assertEquals(job1.toString(), jobDetail1.getKey().getName());
        assertEquals(JOB_GROUP_NAME, jobDetail1.getKey().getGroup());
        assertEquals(job1.toString(), jobDetail1.getJobDataMap().getString("date"));
        assertEquals(job1.atZone(systemDefault()).toInstant().truncatedTo(ChronoUnit.MILLIS), trigger1.getStartTime().toInstant());


        assertEquals(job2.toString(), jobDetail2.getKey().getName());
        assertEquals(JOB_GROUP_NAME, jobDetail2.getKey().getGroup());
        assertEquals(job2.toString(), jobDetail2.getJobDataMap().getString("date"));
        assertEquals(job2.atZone(systemDefault()).toInstant().truncatedTo(ChronoUnit.MILLIS), trigger2.getStartTime().toInstant());

    }

    @SneakyThrows
    @Test
    void scheduleConfigurationPackChange() {

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        verify(scheduler, never()).scheduleJob(any(), any());

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        LocalDateTime date = LocalDateTime.now();
        classUnderTest.scheduleConfigurationPackChange(date, ConfigurationChangeType.START);
        verify(scheduler).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        JobDetail jobDetail = jobDetailCaptor.getValue();
        assertEquals(date.toString(), jobDetail.getKey().getName());
        assertEquals(JOB_GROUP_NAME, jobDetail.getKey().getGroup());
        assertEquals(date.toString(), jobDetail.getJobDataMap().getString("date"));

        Trigger trigger = triggerCaptor.getValue();
        assertEquals(date.atZone(systemDefault()).toInstant().truncatedTo(ChronoUnit.MILLIS), trigger.getStartTime().toInstant());
    }

    @SneakyThrows
    @Test
    void scheduleConfigurationPackChangeFails() {
        when(scheduler.checkExists(any(JobKey.class))).thenThrow(SchedulerException.class);
        classUnderTest.scheduleConfigurationPackChange(LocalDateTime.now(), ConfigurationChangeType.START);
        verify(scheduler, never()).scheduleJob(any(), any());
    }

    @SneakyThrows
    @Test
    void scheduleJob() {
        JobDetail job = newJob(ConfigurationPackChangeJob.class).build();
        Trigger trigger = newTrigger().build();
        classUnderTest.scheduleJob(job, trigger);
        verify(scheduler).scheduleJob(job, trigger);

        when(scheduler.scheduleJob(any(), any())).thenThrow(SchedulerException.class);
        classUnderTest.scheduleJob(job, trigger);
    }
}