package dev.hauch.phantomBans.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PunishmentType} enum.
 */
class PunishmentTypeTest {

    @ParameterizedTest
    @CsvSource({
            "ban, BAN",
            "tempban, TEMP_BAN",
            "ipban, IP_BAN",
            "banip, IP_BAN",
            "ban-ip, IP_BAN",
            "tempipban, TEMP_IP_BAN",
            "tipban, TEMP_IP_BAN",
            "mute, MUTE",
            "tempmute, TEMP_MUTE",
            "warn, WARNING",
            "tempwarn, TEMP_WARNING",
            "note, NOTE",
            "kick, KICK"
    })
    void testFromCommandName_validCommands(String cmd, PunishmentType expected) {
        assertEquals(expected, PunishmentType.fromCommandName(cmd));
    }

    @Test
    void testFromCommandName_returnsNullForInvalid() {
        assertNull(PunishmentType.fromCommandName("invalid"));
        assertNull(PunishmentType.fromCommandName(""));
        assertNull(PunishmentType.fromCommandName("fly"));
    }

    @Test
    void testGetBasic_returnsSelfForBaseTypes() {
        assertSame(PunishmentType.BAN, PunishmentType.BAN.getBasic());
        assertSame(PunishmentType.MUTE, PunishmentType.MUTE.getBasic());
        assertSame(PunishmentType.WARNING, PunishmentType.WARNING.getBasic());
        assertSame(PunishmentType.KICK, PunishmentType.KICK.getBasic());
        assertSame(PunishmentType.NOTE, PunishmentType.NOTE.getBasic());
    }

    @Test
    void testGetBasic_returnsParentForTempTypes() {
        assertSame(PunishmentType.BAN, PunishmentType.TEMP_BAN.getBasic());
        assertSame(PunishmentType.BAN, PunishmentType.IP_BAN.getBasic());
        assertSame(PunishmentType.BAN, PunishmentType.TEMP_IP_BAN.getBasic());
        assertSame(PunishmentType.MUTE, PunishmentType.TEMP_MUTE.getBasic());
        assertSame(PunishmentType.WARNING, PunishmentType.TEMP_WARNING.getBasic());
    }

    @Test
    void testGetPermanent_returnsCorrectType() {
        assertSame(PunishmentType.BAN, PunishmentType.BAN.getPermanent());
        assertSame(PunishmentType.BAN, PunishmentType.TEMP_BAN.getPermanent());
        assertSame(PunishmentType.IP_BAN, PunishmentType.IP_BAN.getPermanent());
        assertSame(PunishmentType.IP_BAN, PunishmentType.TEMP_IP_BAN.getPermanent());
        assertSame(PunishmentType.MUTE, PunishmentType.MUTE.getPermanent());
    }

    @Test
    void testIsTemp_returnsCorrectValue() {
        assertFalse(PunishmentType.BAN.isTemp());
        assertTrue(PunishmentType.TEMP_BAN.isTemp());
        assertFalse(PunishmentType.IP_BAN.isTemp());
        assertTrue(PunishmentType.TEMP_IP_BAN.isTemp());
        assertFalse(PunishmentType.MUTE.isTemp());
        assertTrue(PunishmentType.TEMP_MUTE.isTemp());
        assertFalse(PunishmentType.KICK.isTemp());
        assertFalse(PunishmentType.NOTE.isTemp());
    }

    @Test
    void testIsIpOrientated() {
        assertFalse(PunishmentType.BAN.isIpOrientated());
        assertTrue(PunishmentType.IP_BAN.isIpOrientated());
        assertTrue(PunishmentType.TEMP_IP_BAN.isIpOrientated());
        assertFalse(PunishmentType.MUTE.isIpOrientated());
    }

    @Test
    void testGetName() {
        assertEquals("Ban", PunishmentType.BAN.getName());
        assertEquals("Tempban", PunishmentType.TEMP_BAN.getName());
        assertEquals("Mute", PunishmentType.MUTE.getName());
        assertEquals("Kick", PunishmentType.KICK.getName());
    }

    @Test
    void testGetConfSection() {
        assertEquals("Ban.Usage", PunishmentType.BAN.getConfSection("Usage"));
        assertEquals("Mute.Done", PunishmentType.MUTE.getConfSection("Done"));
    }
}
