package uk.yetanother.primefaces.jsf.demo.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.LazyDataModel;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.primefaces.jsf.demo.service.ConfigurationPackService;

import java.util.UUID;

@Named
@ViewScoped
@RequiredArgsConstructor
@Slf4j
public class ConfigurationsBean {

    private final ConfigurationPackService configurationPackService;

    @Getter
    private LazyDataModel<ConfigurationPackDto> model;

    @Getter
    @Setter
    private UUID selectedConfiguration;

    @PostConstruct
    public void setup() {
        model = new ConfigurationPackLazyDataModel(configurationPackService);
    }

    public void applyConfiguration(String id) {
        configurationPackService.apply(UUID.fromString(id));
    }

}
