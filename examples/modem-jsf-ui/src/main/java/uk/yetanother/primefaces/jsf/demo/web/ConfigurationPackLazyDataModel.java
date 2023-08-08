package uk.yetanother.primefaces.jsf.demo.web;

import lombok.RequiredArgsConstructor;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.primefaces.jsf.demo.service.ConfigurationPackService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfigurationPackLazyDataModel extends LazyDataModel<ConfigurationPackDto> {

    private final transient ConfigurationPackService configurationPackService;

    public int count(Map<String, FilterMeta> map) {
        return configurationPackService.count();
    }

    public List<ConfigurationPackDto> load(int page, int pageSize, Map<String, SortMeta> sorts, Map<String, FilterMeta> filters) {
        return configurationPackService.getFiltered(page, pageSize);
    }

    @Override
    public String getRowKey(ConfigurationPackDto object) {
        return object.getId().toString();
    }
}
