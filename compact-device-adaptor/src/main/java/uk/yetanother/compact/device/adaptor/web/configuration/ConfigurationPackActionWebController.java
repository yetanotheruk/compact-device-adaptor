package uk.yetanother.compact.device.adaptor.web.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackActionController;
import uk.yetanother.compact.device.adaptor.external.enums.ConfigurationChangeType;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/cda/configuration")
@RequiredArgsConstructor
public class ConfigurationPackActionWebController {

    private final ConfigurationPackActionController configurationPackActionController;

    @PostMapping(path = "/{id}/apply", produces = "application/json")
    public void configurationChange(@PathVariable("id") UUID id, @RequestParam(defaultValue = "START") String changeType) {
        try {
            configurationPackActionController.unScheduledChange(id, ConfigurationChangeType.valueOf(changeType));
        } catch (IllegalArgumentException ignored) {
            throw new ResponseStatusException(BAD_REQUEST, String.format("Invalid value provided for ConfigurationChangeType (%s).", changeType));
        }
    }

}
