package uk.yetanother.compact.device.adaptor.web.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;

import java.util.UUID;

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
        classUnderTest.configurationChange(id);
        verify(configurationPackActionController).unScheduledChange(id);
    }
}