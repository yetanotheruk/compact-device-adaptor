package uk.yetanother.compact.device.adaptor.module;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import static uk.yetanother.compact.device.adaptor.module.CompactDeviceAdaptorModule.MODULE_PACKAGE;

@EnableScheduling
@ComponentScan(MODULE_PACKAGE)
public class CompactDeviceAdaptorModule {
    public static final String MODULE_PACKAGE = "uk.yetanother.compact.device.adaptor";

    private CompactDeviceAdaptorModule() {
        // Removing public constructor
    }

}
