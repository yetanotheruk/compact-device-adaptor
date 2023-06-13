package uk.yetanother.compact.device.adaptor.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.external.services.management.IAdaptorManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagementController {

    private final IAdaptorManager adaptorManager;

    @EventListener(ApplicationReadyEvent.class)
    public void systemStarted() {
        log.trace("System Started calling Adaptor Manager");
        adaptorManager.systemStarted();
    }

}
