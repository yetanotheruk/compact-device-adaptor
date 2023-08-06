package uk.yetanother.compact.device.adaptor.web.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ConfigurationPackActionWebControllerTest {

    @Mock
    private ConfigurationPackActionController configurationPackActionController;
    private ConfigurationPackActionWebController classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ConfigurationPackActionWebController(configurationPackActionController);
    }

    @Test
    void configurationChange() {
        UUID id = UUID.randomUUID();
        classUnderTest.configurationChange(id, ConfigurationChangeType.START.name());
        verify(configurationPackActionController).unScheduledChange(id, ConfigurationChangeType.START);
    }

    @Test
    void configurationChangeInvalidChangeType() {
        UUID id = UUID.randomUUID();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> classUnderTest.configurationChange(id, "INVALID"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(configurationPackActionController, never()).unScheduledChange(any(), any());
    }
}