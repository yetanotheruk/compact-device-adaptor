package uk.yetanother.compact.device.adaptor.web.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.yetanother.compact.device.adaptor.business.function.FunctionController;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
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
        verify(functionController).execute(eq("foo"), eq("bar"));
    }

    @Test
    void getFunctionResults() {
        UUID id = UUID.randomUUID();
        classUnderTest.getFunctionResults(id);
        verify(functionController).result(id);
    }
}