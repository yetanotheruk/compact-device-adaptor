package uk.yetanother.compact.device.adaptor.external.services.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultAdaptorManager implements IAdaptorManager {

    @Override
    public void systemStarted() {
        log.info("Informed of application startup");
    }

}
