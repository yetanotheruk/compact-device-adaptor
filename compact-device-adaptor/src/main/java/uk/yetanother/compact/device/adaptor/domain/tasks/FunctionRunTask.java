package uk.yetanother.compact.device.adaptor.domain.tasks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.yetanother.compact.device.adaptor.external.services.fuction.IFunctionHandler;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Data
@Slf4j
public class FunctionRunTask implements Callable<String> {

    private final UUID uuid;
    private final String function;
    private final String attributes;
    private final FutureWrapper wrapper;
    private final IFunctionHandler functionHandler;

    @Override
    public String call() throws Exception {
        try {
            log.trace(String.format("Running task for function %s.", function));
            String result = functionHandler.execute(function, attributes);
            log.trace(String.format("Finished task for function %s.", function));
            wrapper.setCompletedOn(LocalDateTime.now());
            return result;
        } catch (Exception e) {
            log.error(String.format("Error running task for function %s.", function), e);
            throw e;
        }
    }

}
