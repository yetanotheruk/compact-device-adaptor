package uk.yetanother.primefaces.jsf.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.primefaces.jsf.demo.domain.RestPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigurationPackService {

    private static final String API = "http://localhost:8080/api/cda/configuration";
    private final RestTemplate restTemplate;

    public int count() {
        RestPageImpl<ConfigurationPackDto> resultDto = restTemplate.getForObject(API + buildPageParameters(0, 1), RestPageImpl.class);
        return resultDto == null ? 5 : resultDto.getNumberOfElements();
    }

    public List<ConfigurationPackDto> getFiltered(int page, int pageSize) {
        RestPageImpl<ConfigurationPackDto> resultDto = restTemplate.getForObject(API + buildPageParameters(page, pageSize), RestPageImpl.class);
        return resultDto == null ? new ArrayList<>() : resultDto.stream().toList();
    }

    public void apply(UUID id) {
        restTemplate.postForEntity(API + "/" + id.toString() + "/apply", "", String.class);
    }

    private String buildPageParameters(int page, int pageSize) {
        return String.format("?page=%s&pageSize=%s", page, pageSize);
    }
}
