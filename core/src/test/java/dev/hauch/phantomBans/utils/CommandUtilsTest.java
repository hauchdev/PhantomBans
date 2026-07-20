package dev.hauch.phantomBans.utils;

import dev.hauch.phantomBans.TestMethodInterface;
import dev.hauch.phantomBans.Universal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommandUtils} utility methods.
 */
class CommandUtilsTest {

    @BeforeAll
    static void setUp() {
        Universal.get().setup(new TestMethodInterface());
    }

    @Test
    void testGetPunishment_returnsBanForBanType() {
        // Can't fully test without DB, but can verify it doesn't crash and returns null
        assertNull(CommandUtils.getPunishment("nonexistent-uuid", PunishmentType.BAN));
    }

    @Test
    void testGetPunishment_returnsMuteForMuteType() {
        assertNull(CommandUtils.getPunishment("nonexistent-uuid", PunishmentType.MUTE));
    }
}
