package uk.yetanother.compact.device.adaptor.web.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.yetanother.compact.device.adaptor.business.configuration.ConfigurationPackCRUDController;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapper;
import uk.yetanother.compact.device.adaptor.mapping.ConfigurationPackMapperImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.yetanother.compact.device.adaptor.test.utils.ConfigurationPackTestUtils.createTestPack;

@ExtendWith(SpringExtension.class)
class ConfigurationPackCRUDWebControllerTest {

    private final ConfigurationPackMapper configurationPackMapper = new ConfigurationPackMapperImpl();
    @Mock
    private ConfigurationPackCRUDController configurationPackCRUDController;
    private ConfigurationPackCRUDWebController classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ConfigurationPackCRUDWebController(configurationPackCRUDController, configurationPackMapper);
    }

    @Test
    void createConfigurationPack() {
        ConfigurationPack pack = createTestPack();
        classUnderTest.createConfigurationPack(configurationPackMapper.toDto(pack));
        verify(configurationPackCRUDController).handleNewPackReceived(pack);
    }

    @Test
    void getAll() {
        List<ConfigurationPack> entities = List.of(createTestPack());
        Page<ConfigurationPack> page = new PageImpl<>(entities);
        when(configurationPackCRUDController.getAll(any())).thenReturn(page);

        Page<ConfigurationPackDto> results = classUnderTest.getAll(0, 1, "createdOn");
        verify(configurationPackCRUDController).getAll(PageRequest.of(0, 1, Sort.by("createdOn")));
        assertEquals(1, results.get().count());
    }

    @Test
    void getById() {
        UUID id = UUID.randomUUID();
        classUnderTest.getById(id);
        verify(configurationPackCRUDController).getById(id);
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();
        classUnderTest.deleteById(id);
        verify(configurationPackCRUDController).handlePackDeletion(id);
    }

}