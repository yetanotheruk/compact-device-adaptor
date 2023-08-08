package uk.yetanother.cda.examples.modem.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.external.services.management.IAdaptorManager;

@Service
@Primary
@Slf4j
public class ModemAdaptorManager implements IAdaptorManager {

    @Override
    public void systemStarted() {
        log.info("System Started");
    }
}
