package uk.yetanother.compact.device.adaptor.external.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ConfigurationPackDto {

    private UUID id;
    private String externalReference;
    private String classifier;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String configuration;
    private List<SupportFileDto> supportFiles = new ArrayList<>();
    private LocalDateTime createdOn;

}
