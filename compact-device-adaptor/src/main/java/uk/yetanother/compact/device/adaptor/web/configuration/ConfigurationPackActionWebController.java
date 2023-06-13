package uk.yetanother.compact.device.adaptor.web.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cda/configuration")
@RequiredArgsConstructor
public class ConfigurationPackActionWebController {

    private final ConfigurationPackActionController configurationPackActionController;

    @PostMapping(path = "/{id}/apply", produces = "application/json")
    public void configurationChange(@PathVariable("id") UUID id) {
        configurationPackActionController.unScheduledChange(id);
    }

}
