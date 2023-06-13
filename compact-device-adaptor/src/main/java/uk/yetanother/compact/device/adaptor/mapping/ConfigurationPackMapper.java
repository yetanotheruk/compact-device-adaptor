package uk.yetanother.compact.device.adaptor.mapping;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;
import uk.yetanother.compact.device.adaptor.domain.SupportFile;
import uk.yetanother.compact.device.adaptor.external.dto.ConfigurationPackDto;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigurationPackMapper extends LazyAwareMapper {

    ConfigurationPackDto toDto(ConfigurationPack configurationPack);

    ConfigurationPack fromDto(ConfigurationPackDto configurationPackDto);

    List<ConfigurationPackDto> allToDto(List<ConfigurationPack> configurationPacks);

    List<ConfigurationPack> allFromDto(List<ConfigurationPackDto> configurationPackDtos);

    @Condition
    default boolean isSupportFilesInitialized(Collection<SupportFile> collection) {
        return isInitialized(collection);
    }

}
