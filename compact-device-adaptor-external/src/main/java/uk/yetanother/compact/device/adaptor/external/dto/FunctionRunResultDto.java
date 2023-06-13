package uk.yetanother.compact.device.adaptor.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionRunResultDto {

    private UUID id;
    private boolean completed;
    private String result;

}
