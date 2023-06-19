package uk.yetanother.compact.device.adaptor.test.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.external.dto.SupportFileDto;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;
import static uk.yetanother.compact.device.adaptor.test.constants.EndpointUrlConstants.CONFIGURATION_ENDPOINT;

@RequiredArgsConstructor
public class ConfigurationPackRestUtils {

    private final TestRestTemplate restTemplate;

    public void requestConfigurationDeletion(UUID configId) {
        ResponseEntity<String> result = restTemplate.exchange(CONFIGURATION_ENDPOINT + String.format("/%s", configId), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(OK, result.getStatusCode());
    }

    public void requestConfigurationChange(UUID configId) {
        ResponseEntity<String> result = restTemplate.exchange(CONFIGURATION_ENDPOINT + String.format("/%s/apply", configId), HttpMethod.POST, HttpEntity.EMPTY, String.class);
        assertEquals(OK, result.getStatusCode());
    }

    public UUID createConfigurationPack() {
        LocalDateTime date = LocalDateTime.now();
        ConfigurationPackDto pack = new ConfigurationPackDto();
        pack.setExternalReference("Config1");
        pack.setValidFrom(date.minusHours(1));
        pack.setValidTo(date.plusHours(1));
        pack.setClassifier("Standard");
        pack.setConfiguration("power=5");

        SupportFileDto sf = new SupportFileDto();
        sf.setFilename("config.xml");
        try (InputStream file = this.getClass().getClassLoader().getResourceAsStream("config.xml")) {
            sf.setData(file.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pack.getSupportFiles().add(sf);

        ResponseEntity<ConfigurationPackDto> result = restTemplate.postForEntity(CONFIGURATION_ENDPOINT, pack, ConfigurationPackDto.class);
        ConfigurationPackDto resultDto = result.getBody();
        assertEquals(OK, result.getStatusCode());
        assertNotNull(resultDto.getId());
        assertNotNull(resultDto.getCreatedOn());
        assertEquals(pack.getExternalReference(), resultDto.getExternalReference());
        assertEquals(pack.getValidFrom(), resultDto.getValidFrom());
        assertEquals(pack.getValidTo(), resultDto.getValidTo());
        assertEquals(pack.getClassifier(), resultDto.getClassifier());
        assertEquals(pack.getConfiguration(), resultDto.getConfiguration());
        assertEquals(1, resultDto.getSupportFiles().size());

        SupportFileDto resultSf = resultDto.getSupportFiles().get(0);
        assertNotNull(resultSf.getId());
        assertEquals(sf.getFilename(), resultSf.getFilename());
        assertArrayEquals(sf.getData(), resultSf.getData());

        return resultDto.getId();
    }

}
