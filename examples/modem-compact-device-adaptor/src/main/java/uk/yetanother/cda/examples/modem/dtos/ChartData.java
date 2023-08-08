package uk.yetanother.cda.examples.modem.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChartData {

    private List<LocalDateTime> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

}
