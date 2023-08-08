package uk.yetanother.primefaces.jsf.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.yetanother.cda.examples.modem.dtos.ChartData;
import uk.yetanother.cda.examples.modem.dtos.UiSummary;
import uk.yetanother.compact.device.adaptor.external.dto.FunctionRunResultDto;
import uk.yetanother.primefaces.jsf.demo.domain.ResultsNotReadyException;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModemDataService {

    private static final String FUNCTION_API = "http://localhost:8080/api/cda/function/";

    private static final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
            .handle(ResultsNotReadyException.class)
            .withDelay(Duration.ofSeconds(2L))
            .withMaxRetries(3).build();

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public UiSummary getUiSummary() {
        try {
            return objectMapper.readValue(executeFunction("ui-summary", "").getResult(), UiSummary.class);
        } catch (JsonProcessingException e) {
            log.error("Error getting UI Summary", e);
            return new UiSummary();
        }
    }

    public ChartData getDataRateSample() {
        try {
            return objectMapper.readValue(executeFunction("data-rate", "").getResult(), ChartData.class);
        } catch (JsonProcessingException e) {
            log.error("Error getting Data Rate Sample", e);
            return new ChartData();
        }
    }

    public ChartData getBerSample() {
        try {
            return objectMapper.readValue(executeFunction("ber", "").getResult(), ChartData.class);
        } catch (JsonProcessingException e) {
            log.error("Error getting BER Sample", e);
            return new ChartData();
        }
    }

    public void stopModem() {
        this.executeFunction("deactivate", "");
    }

    private FunctionRunResultDto executeFunction(String functionName, String attributes) {
        ResponseEntity<UUID> response = restTemplate.postForEntity(FUNCTION_API + functionName, attributes, UUID.class);
        return Failsafe.with(retryPolicy).get(() -> getFunctionResult(response.getBody()));
    }

    private FunctionRunResultDto getFunctionResult(UUID id) throws ResultsNotReadyException {
        FunctionRunResultDto resultDto = restTemplate.getForObject(FUNCTION_API + id.toString(), FunctionRunResultDto.class);
        if (resultDto != null && resultDto.isCompleted()) {
            return resultDto;
        } else {
            throw new ResultsNotReadyException();
        }
    }
}
