package uk.yetanother.compact.device.adaptor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class SupportFile extends StoredEntity {

    private String filename;

    @Lob
    private byte[] data;

}
