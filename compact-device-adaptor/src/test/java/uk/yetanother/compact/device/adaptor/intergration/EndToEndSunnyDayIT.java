package uk.yetanother.compact.device.adaptor.intergration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.yetanother.compact.device.adaptor.test.utils.ConfigurationPackRestUtils;
import uk.yetanother.compact.device.adaptor.test.utils.FunctionRestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndToEndSunnyDayIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private ConfigurationPackRestUtils configurationPackRestUtils;
    private FunctionRestUtils functionRestUtils;


    @BeforeEach
    public void setup() {
        configurationPackRestUtils = new ConfigurationPackRestUtils(restTemplate);
        functionRestUtils = new FunctionRestUtils(restTemplate);
    }

    @Test
    void sunnyDayRun() {
        UUID configId = configurationPackRestUtils.createConfigurationPack();
        configurationPackRestUtils.requestConfigurationChange(configId);
        UUID functionId = functionRestUtils.runFunction("function", "{key:value}");
        assertEquals("function", functionRestUtils.getFunctionResult(functionId));
        configurationPackRestUtils.requestConfigurationDeletion(configId);
    }

}
