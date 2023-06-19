package uk.yetanother.compact.device.adaptor.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.business.function.FunctionController;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.test.utils.ConfigurationPackTestUtils;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HousekeepingServiceTest {

    @Mock
    private ConfigurationPackCRUDController configurationPackCRUDController;

    @Mock
    private FunctionController functionController;

    private HousekeepingService classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new HousekeepingService(configurationPackCRUDController, functionController);
    }

    @Test
    void housekeep() {
        ConfigurationPack pack1 = ConfigurationPackTestUtils.createTestPack();
        ConfigurationPack pack2 = ConfigurationPackTestUtils.createTestPack();
        when(configurationPackCRUDController.getExpiredConfigurationPacks()).thenReturn(Arrays.asList(pack1, pack2));

        classUnderTest.housekeep();
        verify(configurationPackCRUDController).delete(pack1.getId());
        verify(configurationPackCRUDController).delete(pack2.getId());
        verify(functionController).housekeeping();
    }
}