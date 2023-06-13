package uk.yetanother.compact.device.adaptor.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ConfigurationPack extends StoredEntity {

    private String externalReference;
    private String classifier;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Lob
    private String configuration;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SupportFile> supportFiles;

}

