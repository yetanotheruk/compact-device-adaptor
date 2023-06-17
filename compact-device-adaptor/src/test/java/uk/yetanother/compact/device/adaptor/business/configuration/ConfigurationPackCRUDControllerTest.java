package uk.yetanother.compact.device.adaptor.business.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.external.services.configuration.IConfigurationPackHandler;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapperImpl;
import uk.yetanother.compact.device.adaptor.repositories.ConfigurationPackRepository;
import uk.yetanother.compact.device.adaptor.test.utils.MockedLocalDateUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.yetanother.compact.device.adaptor.test.utils.ConfigurationPackTestUtils.createTestPack;

@ExtendWith(SpringExtension.class)
class ConfigurationPackCRUDControllerTest {

    private final ConfigurationPackMapper configurationPackMapper = new ConfigurationPackMapperImpl();

    @Mock
    private IConfigurationPackHandler configurationPackHandler;

    @Mock
    private ConfigurationPackRepository repository;
    private ConfigurationPackCRUDController classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ConfigurationPackCRUDController(configurationPackHandler, configurationPackMapper, repository);
    }

    @Test
    void handleNewPackReceived() {
        ConfigurationPack pack = createTestPack();
        classUnderTest.handleNewPackReceived(pack);
        verify(configurationPackHandler).handleNewPackReceived(configurationPackMapper.toDto(pack));
        verify(repository).save(pack);
    }

    @Test
    void handlePackDeletion() {
        ConfigurationPack pack = createTestPack();
        when(repository.findById(pack.getId())).thenReturn(Optional.of(pack));
        classUnderTest.handlePackDeletion(pack.getId());
        verify(configurationPackHandler).handlePackDeletion(configurationPackMapper.toDto(pack));
        verify(repository).deleteById(pack.getId());
    }

    @Test
    void save() {
        ConfigurationPack pack = createTestPack();
        classUnderTest.save(pack);
        verify(repository).save(pack);
    }

    @Test
    void getById() {
        UUID id = UUID.randomUUID();
        classUnderTest.getById(id);
        verify(repository).findById(id);
    }

    @Test
    void getAll() {
        PageRequest request = PageRequest.of(1, 1);
        classUnderTest.getAll(request);
        verify(repository).findAll(request);
    }

    @Test
    void getExpiredConfigurationPacks() {
        LocalDateTime date = LocalDateTime.now();
        MockedLocalDateUtils.runWithCurrentDateTime(date, () -> {
            classUnderTest.getExpiredConfigurationPacks();
        });
        verify(repository).findAllByValidToIsBefore(date);
    }

    @Test
    void getNonExpiredConfigurationPacks() {
        LocalDateTime date = LocalDateTime.now();
        MockedLocalDateUtils.runWithCurrentDateTime(date, () -> {
            classUnderTest.getExpiredConfigurationPacks();
        });
        verify(repository).findAllByValidToIsBefore(date);
    }

    @Test
    void getConfigurationPacksValidOnDate() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        classUnderTest.getConfigurationPacksValidOnDate(date);
        verify(repository).findAllByValidFromIs(date);
    }

    @Test
    void getConfigurationPacksValidForDate() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        classUnderTest.getConfigurationPacksValidForDate(date);
        verify(repository).findAllByValidFromIsGreaterThanEqualAndValidToIsBefore(date, date);

    }

    @Test
    void getByExternalReference() {
        String reference = "conf1";
        classUnderTest.getByExternalReference(reference);
        verify(repository).findByExternalReference(reference);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        classUnderTest.delete(id);
        verify(repository).deleteById(id);
    }
}