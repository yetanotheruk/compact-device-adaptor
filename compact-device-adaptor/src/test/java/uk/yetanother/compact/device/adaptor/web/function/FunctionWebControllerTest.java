package uk.yetanother.compact.device.adaptor.web.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.business.function.FunctionController;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class FunctionWebControllerTest {

    @Mock
    private FunctionController functionController;

    private FunctionWebController classUnderTest;

    @BeforeEach
    void setup() {
        classUnderTest = new FunctionWebController(functionController);
    }

    @Test
    void executeFunction() {
        classUnderTest.executeFunction("foo", "bar");
        verify(functionController).execute("foo", "bar");
    }

    @Test
    void getFunctionResults() {
        UUID id = UUID.randomUUID();
        classUnderTest.getFunctionResults(id);
        verify(functionController).result(id);
    }
}