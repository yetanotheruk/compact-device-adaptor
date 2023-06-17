package uk.yetanother.compact.device.adaptor.business.function;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.domain.tasks.FunctionRunTask;
import uk.yetanother.compact.device.adaptor.domain.tasks.FutureWrapper;
import uk.yetanother.compact.device.adaptor.external.dto.FunctionRunResultDto;
import uk.yetanother.compact.device.adaptor.external.services.fuction.IFunctionHandler;
import uk.yetanother.compact.device.adaptor.test.utils.MockedLocalDateUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(SpringExtension.class)
class FunctionControllerTest {

    FunctionController classUnderTest;
    @Captor
    ArgumentCaptor<FunctionRunTask> taskCaptor;
    @Mock
    private IFunctionHandler functionHandler;
    @Mock
    private ExecutorService executor;

    @Mock
    private Future<String> future;

    @BeforeEach
    void setUp() {
        classUnderTest = new FunctionController(functionHandler);
        ReflectionTestUtils.setField(classUnderTest, "executor", executor);
    }

    @Test
    void execute() {
        LocalDateTime creationDate = LocalDateTime.now();

        MockedLocalDateUtils.runWithCurrentDateTime(creationDate, () -> {
            classUnderTest.execute("function", "attributes");
        });
        verify(executor).submit(taskCaptor.capture());
        FunctionRunTask capturedTask = taskCaptor.getValue();
        assertNotNull(capturedTask.getId());
        assertNotNull(capturedTask.getWrapper());
        assertEquals("function", capturedTask.getFunction());
        assertEquals("attributes", capturedTask.getAttributes());
        assertEquals(functionHandler, capturedTask.getFunctionHandler());

        Map<UUID, FutureWrapper> taskDetails = ((Map<UUID, FutureWrapper>) ReflectionTestUtils.getField(classUnderTest, "taskDetails"));
        assertTrue(taskDetails.containsKey(capturedTask.getId()));
        assertEquals(creationDate, taskDetails.get(capturedTask.getId()).getCreationDate());
    }

    @SneakyThrows
    @Test
    void result() {
        UUID id = UUID.randomUUID();
        Map<UUID, FutureWrapper> taskDetails = ((Map<UUID, FutureWrapper>) ReflectionTestUtils.getField(classUnderTest, "taskDetails"));
        taskDetails.put(id, new FutureWrapper(LocalDateTime.now(), LocalDateTime.now(), true, future));

        when(future.isDone()).thenReturn(false);
        FunctionRunResultDto resultDto = classUnderTest.result(id);
        assertFalse(resultDto.isCompleted());
        assertEquals(id, resultDto.getId());
        assertEquals("", resultDto.getResult());

        when(future.isDone()).thenReturn(true);
        when(future.get()).thenReturn("result");
        resultDto = classUnderTest.result(id);
        assertTrue(resultDto.isCompleted());
        assertEquals(id, resultDto.getId());
        assertEquals("result", resultDto.getResult());
    }

    @SneakyThrows
    @Test
    void missingResult() {
        UUID tempId = UUID.randomUUID();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            classUnderTest.result(tempId);
        });
        assertEquals(NOT_FOUND, exception.getStatusCode());

        UUID id = UUID.randomUUID();
        Map<UUID, FutureWrapper> taskDetails = ((Map<UUID, FutureWrapper>) ReflectionTestUtils.getField(classUnderTest, "taskDetails"));
        taskDetails.put(id, new FutureWrapper(LocalDateTime.now(), LocalDateTime.now(), false, future));
        when(future.isDone()).thenReturn(true);
        when(future.get()).thenThrow(ExecutionException.class);

        exception = assertThrows(ResponseStatusException.class, () -> {
            classUnderTest.result(id);
        });
        assertEquals(INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void housekeeping() {
        Map<UUID, FutureWrapper> taskDetails = ((Map<UUID, FutureWrapper>) ReflectionTestUtils.getField(classUnderTest, "taskDetails"));
        classUnderTest.housekeeping();
        assertTrue(taskDetails.isEmpty());

        UUID expiredId = UUID.randomUUID();
        taskDetails.put(expiredId, new FutureWrapper(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1), true, future));

        UUID validId = UUID.randomUUID();
        taskDetails.put(validId, new FutureWrapper(LocalDateTime.now().minusMinutes(5), LocalDateTime.now().minusMinutes(5), true, future));

        classUnderTest.housekeeping();
        assertEquals(1, taskDetails.size());
        assertTrue(taskDetails.containsKey(validId));
        assertFalse(taskDetails.containsKey(expiredId));
    }
}