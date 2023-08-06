package uk.yetanother.compact.device.adaptor.business.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.business.ScheduleService;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.domain.tasks.ConfigurationChangeTask;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapperImpl;
import uk.yetanother.compact.device.adaptor.test.utils.ConfigurationPackTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(SpringExtension.class)
class ConfigurationPackActionControllerTest {

    @Mock
    private IConfigurationPackHandler configurationPackHandler;

    @Mock
    private ConfigurationPackCRUDController configurationPackCRUDController;


    private ConfigurationPackMapper configurationPackMapper = new ConfigurationPackMapperImpl();

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private ExecutorService executor;

    @Captor
    private ArgumentCaptor<Set<LocalDateTime>> datesCaptor;

    @Captor
    private ArgumentCaptor<ConfigurationChangeType> changeTypeCaptor;

    @Captor
    private ArgumentCaptor<ConfigurationChangeTask> configurationChangeTaskCaptor;

    private ConfigurationPackActionController classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ConfigurationPackActionController(configurationPackHandler, configurationPackCRUDController, configurationPackMapper, scheduleService);
        ReflectionTestUtils.setField(classUnderTest, "executor", executor);
    }

    @Test
    void buildSchedule() {
        LocalDateTime baseDate = LocalDateTime.now();
        ConfigurationPack pack1 = ConfigurationPackTestUtils.createTestPack();
        pack1.setValidFrom(baseDate.plusHours(1));
        pack1.setValidTo(baseDate.plusHours(2));

        ConfigurationPack pack2 = ConfigurationPackTestUtils.createTestPack();
        pack2.setValidFrom(baseDate.plusHours(2));
        pack2.setValidTo(baseDate.plusHours(5));

        ConfigurationPack pack3 = ConfigurationPackTestUtils.createTestPack();
        pack3.setValidFrom(baseDate.plusHours(1));
        pack3.setValidTo(baseDate.plusHours(5));

        when(configurationPackCRUDController.getNonExpiredConfigurationPacks()).thenReturn(Arrays.asList(pack1, pack2, pack3));
        classUnderTest.buildSchedule();
        verify(scheduleService, times(2)).scheduleConfigurationPackChanges(datesCaptor.capture(), changeTypeCaptor.capture());

        Set<LocalDateTime> dateResults = datesCaptor.getAllValues().get(0);
        assertEquals(ConfigurationChangeType.START, changeTypeCaptor.getAllValues().get(0));
        assertEquals(2, dateResults.size());
        assertTrue(dateResults.contains(pack1.getValidFrom()));
        assertTrue(dateResults.contains(pack2.getValidFrom()));

        dateResults = datesCaptor.getAllValues().get(1);
        assertEquals(ConfigurationChangeType.STOP, changeTypeCaptor.getAllValues().get(1));
        assertEquals(1, dateResults.size());
        assertTrue(dateResults.contains(pack3.getValidTo()));
    }

    @Test
    void scheduledChange() {
        LocalDateTime baseDate = LocalDateTime.now();
        ConfigurationPack pack1 = ConfigurationPackTestUtils.createTestPack();
        pack1.setValidFrom(baseDate.plusHours(1));

        ConfigurationPack pack2 = ConfigurationPackTestUtils.createTestPack();
        pack2.setValidFrom(baseDate.plusHours(2));

        classUnderTest.scheduledChange(Arrays.asList(pack1, pack2), ConfigurationChangeType.START);
        verify(executor).submit(configurationChangeTaskCaptor.capture());

        ConfigurationChangeTask result = configurationChangeTaskCaptor.getValue();
        assertEquals(2, result.getConfigurationPacks().size());
        assertEquals(pack1.getId(), result.getConfigurationPacks().get(0).getId());
        assertEquals(pack2.getId(), result.getConfigurationPacks().get(1).getId());
        assertEquals(configurationPackHandler, result.getConfigurationPackHandler());
        assertTrue(result.isScheduledChange());

    }

    @Test
    void unScheduledChange() {
        ConfigurationPack pack1 = ConfigurationPackTestUtils.createTestPack();
        when(configurationPackCRUDController.getById(pack1.getId())).thenReturn(pack1);
        classUnderTest.unScheduledChange(pack1.getId(), ConfigurationChangeType.START);
        verify(executor).submit(configurationChangeTaskCaptor.capture());

        ConfigurationChangeTask result = configurationChangeTaskCaptor.getValue();
        assertEquals(1, result.getConfigurationPacks().size());
        assertEquals(pack1.getId(), result.getConfigurationPacks().get(0).getId());
        assertEquals(configurationPackHandler, result.getConfigurationPackHandler());
        assertFalse(result.isScheduledChange());
    }

    @Test
    void unScheduledChangeError() {
        when(configurationPackCRUDController.getById(any())).thenReturn(null);

        UUID id = UUID.randomUUID();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            classUnderTest.unScheduledChange(id, ConfigurationChangeType.START);
        });

        assertEquals(BAD_REQUEST, exception.getStatusCode());
    }
}