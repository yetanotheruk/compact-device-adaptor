package uk.yetanother.compact.device.adaptor.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.external.services.management.IAdaptorManager;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ManagementControllerTest {

    @Mock
    private IAdaptorManager iAdaptorManager;

    private ManagementController classUnderTest;

    @BeforeEach
    void setup() {
        classUnderTest = new ManagementController(iAdaptorManager);
    }

    @Test
    void systemStarted() {
        classUnderTest.systemStarted();
        verify(iAdaptorManager).systemStarted();
    }
}