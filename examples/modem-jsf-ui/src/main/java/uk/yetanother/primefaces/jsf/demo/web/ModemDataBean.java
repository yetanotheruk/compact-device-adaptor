package uk.yetanother.primefaces.jsf.demo.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import uk.yetanother.cda.examples.modem.dtos.ChartData;
import uk.yetanother.cda.examples.modem.dtos.UiSummary;
import uk.yetanother.primefaces.jsf.demo.service.ModemDataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
@RequiredArgsConstructor
public class ModemDataBean {

    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ModemDataService modemDataService;

    @Getter
    private UiSummary summary;

    @Getter
    private LineChartModel dataRateChart = new LineChartModel();

    @Getter
    private LineChartModel berChart = new LineChartModel();

    @Getter
    private String page = "HOME";

    @PostConstruct
    public void setup() {
        this.refreshSummary();
        this.buildChartMeta("Data Rate", this.dataRateChart);
        this.buildChartMeta("BER", this.berChart);
        this.refreshChartData();
    }

    private void buildChartMeta(String name, LineChartModel model) {
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText(name);
        options.setTitle(title);
        model.setOptions(options);
    }

    public void refreshSummary() {
        this.summary = this.modemDataService.getUiSummary();
    }

    public void changePage(String page) {
        this.page = page;
    }

    public void stopModem() {
        this.modemDataService.stopModem();
    }

    public void refreshChartData() {
        this.buildChartData(this.modemDataService.getDataRateSample(), this.dataRateChart, "Data Rate kbps");
        this.buildChartData(this.modemDataService.getBerSample(), this.berChart, "BER %");
    }

    private void buildChartData(ChartData sample, LineChartModel model, String label) {
        org.primefaces.model.charts.ChartData data = new org.primefaces.model.charts.ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        dataSet.setData(new ArrayList<>(sample.getValues()));
        dataSet.setFill(false);
        dataSet.setLabel(label);
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);
        data.setLabels(new ArrayList<>(convertDateToLabel(sample.getLabels())));
        model.setData(data);
    }

    private List<String> convertDateToLabel(List<LocalDateTime> dates) {
        List<String> labels = new ArrayList<>();

        for (LocalDateTime date : dates) {
            labels.add(date.format(CUSTOM_FORMATTER));
        }

        return labels;
    }

}
