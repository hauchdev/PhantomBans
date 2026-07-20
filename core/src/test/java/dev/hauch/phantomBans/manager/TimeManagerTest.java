package dev.hauch.phantomBans.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TimeManager} time parsing logic.
 */
class TimeManagerTest {

    @Test
    void testToMilliSec_seconds() {
        assertEquals(5_000L, TimeManager.toMilliSec("5s"));
        assertEquals(30_000L, TimeManager.toMilliSec("30s"));
    }

    @Test
    void testToMilliSec_minutes() {
        assertEquals(60_000L, TimeManager.toMilliSec("1m"));
        assertEquals(300_000L, TimeManager.toMilliSec("5m"));
        assertEquals(3_600_000L, TimeManager.toMilliSec("60m"));
    }

    @Test
    void testToMilliSec_hours() {
        assertEquals(3_600_000L, TimeManager.toMilliSec("1h"));
        assertEquals(7_200_000L, TimeManager.toMilliSec("2h"));
        assertEquals(86_400_000L, TimeManager.toMilliSec("24h"));
    }

    @Test
    void testToMilliSec_days() {
        assertEquals(86_400_000L, TimeManager.toMilliSec("1d"));
        assertEquals(604_800_000L, TimeManager.toMilliSec("7d"));
        assertEquals(2_592_000_000L, TimeManager.toMilliSec("30d"));
    }

    @Test
    void testToMilliSec_weeks() {
        assertEquals(604_800_000L, TimeManager.toMilliSec("1w"));
        assertEquals(1_209_600_000L, TimeManager.toMilliSec("2w"));
    }

    @Test
    void testToMilliSec_months() {
        assertEquals(2_592_000_000L, TimeManager.toMilliSec("1mo"));
        assertEquals(12_960_000_000L, TimeManager.toMilliSec("5mo"));
    }

    @Test
    void testToMilliSec_caseInsensitive() {
        assertEquals(86_400_000L, TimeManager.toMilliSec("1D"));
        assertEquals(3_600_000L, TimeManager.toMilliSec("1H"));
        assertEquals(60_000L, TimeManager.toMilliSec("1M"));
    }

    @Test
    void testToMilliSec_returnsMinusOneForInvalidUnit() {
        assertEquals(-1, TimeManager.toMilliSec("5x"));
        assertEquals(-1, TimeManager.toMilliSec("10y"));
    }

    @Test
    void testGetTime_returnsPositiveValue() {
        assertTrue(TimeManager.getTime() > 0);
    }
}
