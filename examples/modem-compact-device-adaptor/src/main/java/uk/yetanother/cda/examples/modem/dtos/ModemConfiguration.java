package uk.yetanother.cda.examples.modem.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModemConfiguration {

    private double power;
    private String satellite;
    private LocalDateTime keyValidUntil;

}
