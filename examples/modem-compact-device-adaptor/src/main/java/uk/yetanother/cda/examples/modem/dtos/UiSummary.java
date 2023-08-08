package uk.yetanother.cda.examples.modem.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UiSummary {

    private int modemId;
    private String softwareVersion;
    private String firmwareVersion;
    private boolean active;
    private double power;
    private boolean keysLoaded;
    private LocalDateTime keysValidUntil;
    private String configurationName;
    private String satellite;
    private LocalDateTime configurationAppliedOn;
    private String gps = "Locked";
    private String onepps = "Present";
    private String ntp = "synchronised";

}
