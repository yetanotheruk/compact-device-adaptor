package uk.yetanother.compact.device.adaptor.domain.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ConfigurationChangeTaskTest {

    @Mock
    private IConfigurationPackHandler configurationPackHandler;

    @Test
    void callScheduled() {
        ConfigurationPackDto pack = new ConfigurationPackDto();
        pack.setId(UUID.randomUUID());
        List<ConfigurationPackDto> packs = new ArrayList<>(List.of(pack));
        ConfigurationChangeTask classUnderTest = new ConfigurationChangeTask(packs, true, configurationPackHandler);
        classUnderTest.call();
        verify(configurationPackHandler).scheduledChange(packs);
        verify(configurationPackHandler, never()).unScheduledChange(any());
    }

    @Test
    void callUnScheduled() {
        ConfigurationPackDto pack = new ConfigurationPackDto();
        pack.setId(UUID.randomUUID());
        List<ConfigurationPackDto> packs = new ArrayList<>();
        ConfigurationChangeTask classUnderTest = new ConfigurationChangeTask(packs, false, configurationPackHandler);
        classUnderTest.call();
        verify(configurationPackHandler, never()).scheduledChange(any());
        verify(configurationPackHandler, never()).unScheduledChange(any());

        packs.add(pack);
        classUnderTest.call();
        verify(configurationPackHandler, never()).scheduledChange(any());
        verify(configurationPackHandler).unScheduledChange(pack);

        doThrow(new RuntimeException()).when(configurationPackHandler).unScheduledChange(any());
        assertThrows(RuntimeException.class, classUnderTest::call);
    }
}