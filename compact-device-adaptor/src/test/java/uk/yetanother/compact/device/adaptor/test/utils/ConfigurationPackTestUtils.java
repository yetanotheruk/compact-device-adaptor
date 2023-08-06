package uk.yetanother.compact.device.adaptor.test.utils;

import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfigurationPackTestUtils {

    private ConfigurationPackTestUtils() {
        // hiding public constructor
    }

    public static ConfigurationPack createTestPack() {
        ConfigurationPack pack = new ConfigurationPack();
        pack.setId(UUID.randomUUID());
        pack.setValidFrom(LocalDateTime.now().plusHours(1));
        pack.setValidTo(LocalDateTime.now().plusHours(2));
        return pack;
    }
}
