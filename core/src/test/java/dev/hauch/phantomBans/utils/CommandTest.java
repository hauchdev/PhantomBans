package dev.hauch.phantomBans.utils;

import dev.hauch.phantomBans.TestMethodInterface;
import dev.hauch.phantomBans.Universal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Command} enum.
 */
class CommandTest {

    @BeforeAll
    static void setUp() {
        Universal.get().setup(new TestMethodInterface());
    }

    @Test
    void testGetByName_returnsCorrectCommand() {
        assertSame(Command.BAN, Command.getByName("ban"));
        assertSame(Command.TEMP_BAN, Command.getByName("tempban"));
        assertSame(Command.MUTE, Command.getByName("mute"));
        assertSame(Command.KICK, Command.getByName("kick"));
        assertSame(Command.UN_BAN, Command.getByName("unban"));
        assertSame(Command.ADVANCED_BAN, Command.getByName("phantombans"));
        assertSame(Command.ADVANCED_BAN, Command.getByName("pb"));
        assertSame(Command.BAN_LIST, Command.getByName("banlist"));
        assertSame(Command.HISTORY, Command.getByName("history"));
        assertSame(Command.CHECK, Command.getByName("check"));
    }

    @Test
    void testGetByName_returnsNullForInvalid() {
        assertNull(Command.getByName("invalid"));
        assertNull(Command.getByName(""));
    }

    @Test
    void testGetByName_caseInsensitive() {
        assertSame(Command.BAN, Command.getByName("BAN"));
        assertSame(Command.MUTE, Command.getByName("Mute"));
        assertSame(Command.ADVANCED_BAN, Command.getByName("PB"));
    }

    @Test
    void testGetNames_containsAllAliases() {
        assertArrayEquals(new String[]{"ban"}, Command.BAN.getNames());
        assertArrayEquals(new String[]{"ipban", "banip", "ban-ip"}, Command.IP_BAN.getNames());
        assertArrayEquals(new String[]{"phantombans", "pb"}, Command.ADVANCED_BAN.getNames());
    }

    @ParameterizedTest
    @CsvSource({
            "BAN, pb.ban.perma",
            "TEMP_BAN, pb.ban.temp",
            "MUTE, pb.mute.perma",
            "UN_BAN, pb.Ban.undo",
            "CHECK, pb.check",
            "ADVANCED_BAN, null"
    })
    void testGetPermission(Command cmd, String expectedPerm) {
        if (expectedPerm.equals("null")) {
            assertNull(cmd.getPermission());
        } else {
            assertEquals(expectedPerm, cmd.getPermission());
        }
    }

    @Test
    void testValidateArguments_validArgs() {
        assertTrue(Command.BAN.validateArguments(new String[]{"Player1", "Griefing"}));
        assertTrue(Command.MUTE.validateArguments(new String[]{"Player2", "Spam"}));
        assertTrue(Command.UN_BAN.validateArguments(new String[]{"Player1"}));
        assertTrue(Command.BAN_LIST.validateArguments(new String[]{"1"}));
        assertTrue(Command.BAN_LIST.validateArguments(new String[]{}));
    }

    @Test
    void testValidateArguments_invalidArgs() {
        assertFalse(Command.UN_BAN.validateArguments(new String[]{}));
        assertFalse(Command.HISTORY.validateArguments(new String[]{}));
        assertFalse(Command.CHECK.validateArguments(new String[]{}));
    }

    @Test
    void testCommandInput_basicUsage() {
        Command.CommandInput input = new Command.CommandInput("Console", new String[]{"Player1", "Spam", "chat"});
        assertEquals("Console", input.getSender());
        assertEquals("Player1", input.getPrimary());
        assertTrue(input.hasNext());
        input.next();
        assertEquals("Spam", input.getPrimary());
        assertTrue(input.hasNext());
        input.next();
        assertEquals("chat", input.getPrimary());
        assertFalse(input.hasNext());
    }

    @Test
    void testCommandInput_hasNext() {
        Command.CommandInput input = new Command.CommandInput("Console", new String[]{"only"});
        assertFalse(input.hasNext());
        assertEquals("only", input.getPrimary());
    }

    @Test
    void testCommandInput_getArgs() {
        Command.CommandInput input = new Command.CommandInput("Console", new String[]{"a", "b", "c"});
        input.next(); // skip "a"
        assertArrayEquals(new String[]{"b", "c"}, input.getArgs());
    }

    @Test
    void testCommandInput_getPrimaryData() {
        Command.CommandInput input = new Command.CommandInput("Console", new String[]{"test"});
        assertEquals("test", input.getPrimaryData());
        assertEquals("test", input.getPrimary());
    }

    @Test
    void testCommandInput_getArgsAfterAllConsumed() {
        Command.CommandInput input = new Command.CommandInput("Console", new String[]{"a", "b"});
        input.next();
        input.next();
        assertArrayEquals(new String[]{}, input.getArgs());
    }

    @Test
    void testGetUsagePath() {
        assertEquals("Ban.Usage", Command.BAN.getUsagePath());
        assertEquals("Mute.Usage", Command.MUTE.getUsagePath());
        assertEquals("Check.Usage", Command.CHECK.getUsagePath());
    }
}
