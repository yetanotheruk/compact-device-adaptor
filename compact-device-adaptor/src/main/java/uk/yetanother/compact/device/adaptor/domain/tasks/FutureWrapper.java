package uk.yetanother.compact.device.adaptor.domain.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.Future;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FutureWrapper {

    private LocalDateTime creationDate;
    private LocalDateTime completedOn;
    private boolean success;
    private Future<String> future;
}
