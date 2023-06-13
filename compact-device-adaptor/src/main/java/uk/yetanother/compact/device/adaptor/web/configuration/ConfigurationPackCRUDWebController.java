package uk.yetanother.compact.device.adaptor.web.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/cda/configuration")
@RequiredArgsConstructor
public class ConfigurationPackCRUDWebController {

    private final ConfigurationPackCRUDController configurationPackCRUDController;
    private final ConfigurationPackMapper configurationPackMapper;

    @PostMapping(produces = "application/json")
    public ConfigurationPackDto createConfigurationPack(@RequestBody ConfigurationPackDto configurationPack) {
        return configurationPackCRUDController.handleNewPackReceived(configurationPack);
    }

    @GetMapping(produces = "application/json")
    public Page<ConfigurationPackDto> getAll(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(defaultValue = "createdOn") String sortBy) {
        return configurationPackCRUDController.getAll(PageRequest.of(page, pageSize, Sort.by(sortBy)))
                .map(configurationPackMapper::toDto);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ConfigurationPackDto getById(@PathVariable("id") UUID id) {
        return configurationPackMapper.toDto(configurationPackCRUDController.getById(id));
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    public void deleteById(@PathVariable("id") UUID id) {
        configurationPackCRUDController.handlePackDeletion(id);
    }

}
