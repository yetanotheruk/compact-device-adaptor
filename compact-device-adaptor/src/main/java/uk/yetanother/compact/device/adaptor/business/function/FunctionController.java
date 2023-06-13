package uk.yetanother.compact.device.adaptor.business.function;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.domain.tasks.FunctionRunTask;
import uk.yetanother.compact.device.adaptor.domain.tasks.FutureWrapper;
import uk.yetanother.compact.device.adaptor.external.dto.FunctionRunResultDto;
import uk.yetanother.compact.device.adaptor.external.services.fuction.IFunctionHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionController {

    private final IFunctionHandler functionHandler;
    private final Map<UUID, FutureWrapper> taskDetails = new HashMap<>();
    private ExecutorService executor;

    @PostConstruct
    public void setup() {
        executor = Executors.newFixedThreadPool(10);
    }

    @PreDestroy
    public void tearDown() {
        executor.shutdownNow();
    }

    public UUID execute(String function, String attributes) {
        UUID taskId = UUID.randomUUID();
        FutureWrapper wrapper = new FutureWrapper();
        wrapper.setCreationDate(LocalDateTime.now());
        wrapper.setFuture(executor.submit(new FunctionRunTask(taskId, function, attributes, wrapper, functionHandler)));
        taskDetails.put(taskId, wrapper);
        return taskId;
    }

    public FunctionRunResultDto result(UUID id) {
        if (!taskDetails.containsKey(id)) {
            log.trace(String.format("No record of a functions results %s.", id));
            throw new ResponseStatusException(NOT_FOUND, String.format("No record of a functions results for id %s.", id));
        }

        if (taskDetails.get(id).getFuture().isDone()) {
            try {
                FunctionRunResultDto result = new FunctionRunResultDto(id, true, taskDetails.get(id).getFuture().get());
                taskDetails.remove(id);
                return result;
            } catch (InterruptedException | ExecutionException e) {
                log.error(String.format("Unable to get run results of function %s.", id), e);
                Thread.currentThread().interrupt(); // Restore interrupted state.
            }

        }
        return new FunctionRunResultDto(id, false, "");
    }

    public void housekeeping() {
        LocalDateTime housekeepingDate = LocalDateTime.now().minusMinutes(10);
        List<UUID> futuresToRemove = new ArrayList<>();
        for (Map.Entry<UUID, FutureWrapper> future : taskDetails.entrySet()) {
            if (future.getValue().getCompletedOn() != null && housekeepingDate.isAfter(future.getValue().getCompletedOn())) {
                futuresToRemove.add(future.getKey());
            }
        }
        log.trace(String.format("Found %s orphaned task results to remove", futuresToRemove.size()));
        for (UUID id : futuresToRemove) {
            taskDetails.remove(id);
        }
    }
}
