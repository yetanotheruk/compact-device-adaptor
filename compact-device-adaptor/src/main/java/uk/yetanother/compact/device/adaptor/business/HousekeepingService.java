package uk.yetanother.compact.device.adaptor.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.business.function.FunctionController;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;

@Service
@RequiredArgsConstructor
@Slf4j
public class HousekeepingService {

    private final ConfigurationPackCRUDController configurationPackCRUDController;
    private final FunctionController functionController;

    @Scheduled(fixedDelayString = "${housekeeping.delay.ms}")
    public void housekeep() {
        log.info("Starting Housekeeping");
        for (ConfigurationPack expiredPack : configurationPackCRUDController.getExpiredConfigurationPacks()) {
            log.info(String.format("The Configuration Pack %s (%s) has been deleted as it expired on %s.", expiredPack.getExternalReference(), expiredPack.getId(), expiredPack.getValidTo()));
            configurationPackCRUDController.delete(expiredPack.getId());
        }
        functionController.housekeeping();
        log.info("Completed Housekeeping");
    }
}
