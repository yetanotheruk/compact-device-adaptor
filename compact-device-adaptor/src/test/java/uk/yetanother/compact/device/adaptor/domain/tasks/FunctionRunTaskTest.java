package uk.yetanother.compact.device.adaptor.domain.tasks;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.external.services.fuction.IFunctionHandler;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FunctionRunTaskTest {

    @Mock
    private IFunctionHandler functionHandler;

    private FunctionRunTask classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new FunctionRunTask(UUID.randomUUID(), "function", "attributes", new FutureWrapper(), functionHandler);
    }

    @SneakyThrows
    @Test
    void call() {
        when(functionHandler.execute(anyString(), anyString())).thenReturn("result");
        assertEquals("result", classUnderTest.call());
        verify(functionHandler).execute("function", "attributes");

        assertNotNull(classUnderTest.getWrapper().getCompletedOn());
        assertTrue(classUnderTest.getWrapper().isSuccess());
    }

    @Test
    void callWithError() {
        when(functionHandler.execute(anyString(), anyString())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> {
            classUnderTest.call();
        });
        verify(functionHandler).execute("function", "attributes");
        assertNotNull(classUnderTest.getWrapper().getCompletedOn());
        assertFalse(classUnderTest.getWrapper().isSuccess());

    }
}