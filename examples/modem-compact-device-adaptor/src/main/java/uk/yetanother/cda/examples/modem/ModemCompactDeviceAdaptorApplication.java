package uk.yetanother.cda.examples.modem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.yetanother.compact.device.adaptor.module.CompactDeviceAdaptorModule;

@SpringBootApplication
@Import({CompactDeviceAdaptorModule.class})
public class ModemCompactDeviceAdaptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModemCompactDeviceAdaptorApplication.class, args);
    }
}
