package uk.yetanother.compact.device.adaptor.test.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.yetanother.compact.device.adaptor.external.dto.FunctionRunResultDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static uk.yetanother.compact.device.adaptor.test.constants.EndpointUrlConstants.FUNCTION_ENDPOINT;

@RequiredArgsConstructor
public class FunctionRestUtils {

    private final TestRestTemplate restTemplate;

    public String getFunctionResult(UUID id) {
        ResponseEntity<FunctionRunResultDto> result = restTemplate.exchange(FUNCTION_ENDPOINT + String.format("/%s", id), HttpMethod.GET, HttpEntity.EMPTY, FunctionRunResultDto.class);
        assertEquals(OK, result.getStatusCode());
        return result.getBody().getResult();
    }

    public UUID runFunction(String functionName, String attributes) {
        ResponseEntity<UUID> result = restTemplate.exchange(FUNCTION_ENDPOINT + String.format("/%s", functionName), HttpMethod.POST, new HttpEntity<>(attributes), UUID.class);
        assertEquals(OK, result.getStatusCode());
        return result.getBody();
    }
}
