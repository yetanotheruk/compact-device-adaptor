package uk.yetanother.compact.device.adaptor.test.utils;

import uk.yetanother.compact.device.adaptor.domain.ConfigurationPack;

import java.util.UUID;

public class ConfigurationPackTestUtils {

    private ConfigurationPackTestUtils() {
        // hiding public constructor
    }

    public static ConfigurationPack createTestPack() {
        ConfigurationPack pack = new ConfigurationPack();
        pack.setId(UUID.randomUUID());
        return pack;
    }
}
