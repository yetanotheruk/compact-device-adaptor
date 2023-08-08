package uk.yetanother.cda.examples.modem.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.yetanother.cda.examples.modem.dtos.UiSummary;
import uk.yetanother.cda.examples.modem.simulator.ModemSimulator;
import uk.yetanother.compact.device.adaptor.external.services.fuction.IFunctionHandler;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ModemFunctionHandler implements IFunctionHandler {

    private final ModemSimulator simulator;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public String execute(String function, String attributes) {
        try {
            log.info(String.format("Asked Modem to run function %s with attributes %s.", function, attributes));
            switch (function) {
                case "ui-summary":
                    return this.objectMapper.writeValueAsString(buildUiSummary());
                case "set-power":
                    simulator.setPower(Double.parseDouble(attributes));
                    return String.valueOf(simulator.getPower());
                case "data-rate":
                    return this.objectMapper.writeValueAsString(simulator.getDataRateData(1000, 5000));
                case "ber":
                    return this.objectMapper.writeValueAsString(simulator.getDataRateData(0, 3));
                case "activate":
                    simulator.setActive(true);
                    return String.valueOf(simulator.isActive());
                case "deactivate":
                    simulator.setActive(false);
                    return String.valueOf(simulator.isActive());
                default:
                    log.warn(String.format("Unknown function %s", function));
                    return "";
            }
        } catch (JsonProcessingException e) {
            log.error("Error running function", e);
            return "";
        }
    }

    private UiSummary buildUiSummary() {
        UiSummary uiSummary = new UiSummary();
        uiSummary.setModemId(simulator.getModemId());
        uiSummary.setSoftwareVersion(simulator.getSoftwareVersion());
        uiSummary.setFirmwareVersion(simulator.getFirmwareVersion());
        uiSummary.setPower(simulator.getPower());
        uiSummary.setConfigurationName(simulator.getConfigurationName());
        uiSummary.setSatellite(simulator.getSatellite());
        uiSummary.setConfigurationAppliedOn(simulator.getConfigurationAppliedOn());
        uiSummary.setActive(simulator.isActive());
        uiSummary.setKeysLoaded(simulator.isKeysLoaded());
        uiSummary.setKeysValidUntil(simulator.getKeysValidUntil());
        uiSummary.setGps(simulator.getGps());
        uiSummary.setNtp(simulator.getNtp());
        uiSummary.setOnepps(simulator.getOnepps());
        return uiSummary;
    }
}
