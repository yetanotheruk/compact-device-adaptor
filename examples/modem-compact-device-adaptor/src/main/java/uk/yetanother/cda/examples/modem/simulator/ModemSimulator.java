package uk.yetanother.cda.examples.modem.simulator;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import uk.yetanother.cda.examples.modem.dtos.ChartData;

import java.time.LocalDateTime;

@Service
@Data
public class ModemSimulator {

    private int modemId;
    private String softwareVersion = "14.5.7";
    private String firmwareVersion = "High Speed Waveform 6.7";
    private boolean active;
    private double power;
    private boolean keysLoaded;
    private LocalDateTime keysValidUntil;
    private String configurationName = "";
    private String satellite = "";
    private LocalDateTime configurationAppliedOn;
    private String gps = "Locked";
    private String onepps = "Present";
    private String ntp = "Synchronised";

    @PostConstruct
    public void setup() {
        modemId = RandomUtils.nextInt(1, 25);
    }

    public ChartData getDataRateData(int from, int to) {
        ChartData sample = new ChartData();
        LocalDateTime timeReference = LocalDateTime.now();

        for (int i = 60; i >= 0; --i) {
            sample.getLabels().add(timeReference.minusSeconds(i));
            sample.getValues().add(active ? RandomUtils.nextDouble(from, to) : 0.0);
        }

        return sample;
    }

}
