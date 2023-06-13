package uk.yetanother.compact.device.adaptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.yetanother.compact.device.adaptor.module.CompactDeviceAdaptorModule;

@SpringBootApplication
@Import(CompactDeviceAdaptorModule.class)
public class CompactDeviceAdaptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompactDeviceAdaptorApplication.class, args);
    }

}
