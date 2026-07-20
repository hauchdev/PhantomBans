package dev.hauch.phantomBans.utils;

import dev.hauch.phantomBans.TestMethodInterface;
import dev.hauch.phantomBans.Universal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Punishment} class.
 */
class PunishmentTest {

    private static final long NOW = System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        Universal.get().setup(new TestMethodInterface());
    }

    @Test
    void testConstructor() {
        Punishment punishment = new Punishment(
                "TestPlayer", "uuid-123", "Griefing", "Admin",
                PunishmentType.BAN, NOW, -1, "", 1);

        assertEquals("TestPlayer", punishment.getName());
        assertEquals("uuid-123", punishment.getUuid());
        assertEquals("Griefing", punishment.getReason());
        assertEquals("Admin", punishment.getOperator());
        assertEquals(PunishmentType.BAN, punishment.getType());
        assertEquals(NOW, punishment.getStart());
        assertEquals(-1, punishment.getEnd());
        assertEquals(1, punishment.getId());
    }

    @Test
    void testIsExpired_returnsFalseForPermanent() {
        Punishment ban = new Punishment("P", "uuid", "R", "O",
                PunishmentType.BAN, NOW, -1, "", 1);
        assertFalse(ban.isExpired());
    }

    @Test
    void testIsExpired_returnsFalseForFutureEnd() {
        Punishment tempBan = new Punishment("P", "uuid", "R", "O",
                PunishmentType.TEMP_BAN, NOW, NOW + 86_400_000, "", 1);
        assertFalse(tempBan.isExpired());
    }

    @Test
    void testIsExpired_returnsTrueForPastEnd() {
        long past = NOW - 86_400_000;
        Punishment tempBan = new Punishment("P", "uuid", "R", "O",
                PunishmentType.TEMP_BAN, past, past, "", 1);
        assertTrue(tempBan.isExpired());
    }

    @Test
    void testCeilDiv_positiveDivision() {
        Punishment p = createPunishment();
        assertEquals(3, p.ceilDiv(10, 3));
        assertEquals(4, p.ceilDiv(11, 3));
        assertEquals(3, p.ceilDiv(9, 3));
    }

    @Test
    void testCeilDiv_exactDivision() {
        Punishment p = createPunishment();
        assertEquals(2, p.ceilDiv(6, 3));
        assertEquals(5, p.ceilDiv(10, 2));
    }

    @Test
    void testCeilDiv_negativeDividend() {
        Punishment p = createPunishment();
        assertEquals(-3, p.ceilDiv(-10, 3));
    }

    @Test
    void testGetHexId() {
        Punishment p = new Punishment("P", "uuid", "R", "O",
                PunishmentType.WARNING, NOW, -1, "", 255);
        assertEquals("FF", p.getHexId());

        Punishment p2 = new Punishment("P", "uuid", "R", "O",
                PunishmentType.WARNING, NOW, -1, "", 16);
        assertEquals("10", p2.getHexId());
    }

    @Test
    void testGetDuration_forPermanentReturnsPermanent() {
        Punishment ban = new Punishment("P", "uuid", "R", "O",
                PunishmentType.BAN, NOW, -1, "", 1);
        assertEquals("permanent", ban.getDuration(false));
    }

    @Test
    void testGetDuration_forTempBan() {
        Punishment tempBan = new Punishment("P", "uuid", "R", "O",
                PunishmentType.TEMP_BAN, NOW, NOW + 3600_000, "", 1);
        String duration = tempBan.getDuration(true);
        assertNotNull(duration);
        assertFalse(duration.equals("permanent"));
    }

    @Test
    void testToString() {
        Punishment p = new Punishment("Test", "uuid", "Reason", "Op",
                PunishmentType.MUTE, 1000, -1, "", 5);
        String str = p.toString();
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("MUTE"));
        assertTrue(str.contains("5"));
    }

    private Punishment createPunishment() {
        return new Punishment("P", "uuid", "R", "O",
                PunishmentType.WARNING, NOW, -1, "", 1);
    }
}
