package uk.yetanother.compact.device.adaptor.domain.jobs;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.impl.JobDetailImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ConfigurationPackChangeJobTest {

    @Mock
    private ConfigurationPackCRUDController configurationPackCRUDController;

    @Mock
    private ConfigurationPackActionController configurationPackActionController;

    @Mock
    private JobExecutionContext jobExecutionContext;


    private ConfigurationPackChangeJob classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ConfigurationPackChangeJob(configurationPackCRUDController, configurationPackActionController);
    }

    @SneakyThrows
    @Test
    void executeInternal() {
        LocalDateTime date = LocalDateTime.now();
        List<ConfigurationPack> packs = new ArrayList<>(List.of(new ConfigurationPack()));
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobDataMap(new JobDataMap());
        jobDetail.getJobDataMap().put("date", date.toString());

        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
        when(configurationPackCRUDController.getConfigurationPacksValidOnDate(date)).thenReturn(new ArrayList<>());
        classUnderTest.executeInternal(jobExecutionContext);
        verify(configurationPackActionController, never()).scheduledChange(any());


        Mockito.clearInvocations(configurationPackCRUDController);
        when(configurationPackCRUDController.getConfigurationPacksValidOnDate(date)).thenReturn(packs);
        classUnderTest.executeInternal(jobExecutionContext);
        verify(configurationPackCRUDController).getConfigurationPacksValidOnDate(date);
        verify(configurationPackActionController).scheduledChange(packs);
    }
}