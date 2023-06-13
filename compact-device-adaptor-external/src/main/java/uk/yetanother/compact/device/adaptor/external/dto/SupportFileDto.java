package uk.yetanother.compact.device.adaptor.external.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SupportFileDto {

    private UUID id;
    private String filename;
    private byte[] data;

}
