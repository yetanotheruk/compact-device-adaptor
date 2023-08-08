package uk.yetanother.cda.examples.modem;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.domain.SupportFile;
import uk.yetanother.compact.device.adaptor.repositories.ConfigurationPackRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TestData {

    private final ConfigurationPackRepository repository;

    @PostConstruct
    public void init() {
        this.createTestConfiguration("WGS-10", "2.4", "US-Nominal", "2023-06-13T23:55:32.343483");
        this.createTestConfiguration("Intelsat", "4.4", "Spain-Alt1", "2024-06-13T23:55:32.343483");
        this.createTestConfiguration("Inmarsat", "1.4", "Nominal", "2024-08-13T23:55:32.343483");
        this.createTestConfiguration("Viasat", "8.4", "Stressed", "2024-11-13T23:55:32.343483");
        this.createTestConfiguration("Eutelsat", "8.4", "Euro-Alt2", "2025-08-13T23:55:32.343483");
    }

    private void createTestConfiguration(String satName, String power, String type, String keyDate) {
        ConfigurationPack pack = new ConfigurationPack();
        pack.setExternalReference(String.format("%s Trial", satName));
        pack.setClassifier(type);
        pack.setValidFrom(LocalDateTime.now().plusDays(1L));
        pack.setValidTo(LocalDateTime.now().plusWeeks(1L));
        pack.setConfiguration(String.format("{\"power\": %s,\"keyValidUntil\": \"%s\",\"satellite\": \" %s\"}", power, keyDate, satName));
        pack.setSupportFiles(new ArrayList<>());
        SupportFile sf = new SupportFile();
        sf.setFilename("KeyPackage.xml");
        sf.setData("<xml></xml>".getBytes());
        pack.getSupportFiles().add(sf);
        repository.save(pack);
    }
}
