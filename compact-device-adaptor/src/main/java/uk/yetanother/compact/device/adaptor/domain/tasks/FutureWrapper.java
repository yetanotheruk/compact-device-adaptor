package uk.yetanother.compact.device.adaptor.domain.tasks;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.Future;

@Data
public class FutureWrapper {

    private LocalDateTime creationDate;
    private LocalDateTime completedOn;
    private Future<String> future;
}
