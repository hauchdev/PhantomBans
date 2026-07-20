package dev.hauch.phantomBans;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Universal} mute command matching logic.
 */
class UniversalTest {

    private static Universal universal;
    private static final List<String> MUTE_COMMANDS = Arrays.asList(
            "/msg", "/tell", "/w", "/reply", "/r", "/me", "/action"
    );

    @BeforeAll
    static void setUp() {
        universal = Universal.get();
        universal.setup(new TestMethodInterface());
    }

    @Test
    void testIsMuteCommand_shouldMatchExactCommand() {
        assertTrue(universal.isMuteCommand("/msg Hello there", MUTE_COMMANDS));
        assertTrue(universal.isMuteCommand("/tell player hi", MUTE_COMMANDS));
        assertTrue(universal.isMuteCommand("/w player", MUTE_COMMANDS));
    }

    @Test
    void testIsMuteCommand_shouldNotMatchNonMuteCommands() {
        assertFalse(universal.isMuteCommand("/ban player", MUTE_COMMANDS));
        assertFalse(universal.isMuteCommand("/kick player", MUTE_COMMANDS));
        assertFalse(universal.isMuteCommand("/tp player", MUTE_COMMANDS));
    }

    @Test
    void testIsMuteCommand_shouldBeCaseInsensitive() {
        assertTrue(universal.isMuteCommand("/MSG Hello", MUTE_COMMANDS));
        assertTrue(universal.isMuteCommand("/TELL player", MUTE_COMMANDS));
        assertTrue(universal.isMuteCommand("/Reply 123", MUTE_COMMANDS));
    }

    @Test
    void testIsMuteCommand_shouldHandleColonPrefix() {
        assertTrue(universal.isMuteCommand("/minecraft:msg Hello", MUTE_COMMANDS));
        assertTrue(universal.isMuteCommand("/minecraft:tell player hi", MUTE_COMMANDS));
    }

    @Test
    void testIsMuteCommand_shouldMatchMultiWordMuteCommands() {
        List<String> multiWordCommands = Arrays.asList("/party chat", "/group msg");
        assertTrue(universal.isMuteCommand("/party chat hello", multiWordCommands));
        assertTrue(universal.isMuteCommand("/group msg hi there", multiWordCommands));
        assertFalse(universal.isMuteCommand("/party", multiWordCommands));
        assertFalse(universal.isMuteCommand("/group invite player", multiWordCommands));
    }

    @Test
    void testIsMuteCommand_withEmptyArgs() {
        assertTrue(universal.isMuteCommand("/msg", MUTE_COMMANDS));
    }

    @Test
    void testMuteCommandMatches_exactMatch() {
        String[] words = {"/msg", "hello"};
        assertTrue(universal.muteCommandMatches(words, "/msg"));
    }

    @Test
    void testMuteCommandMatches_multiWord() {
        String[] words = {"/party", "chat", "hello"};
        assertTrue(universal.muteCommandMatches(words, "/party chat"));

        String[] shortWords = {"/party"};
        assertFalse(universal.muteCommandMatches(shortWords, "/party chat"));
    }

    @Test
    void testMuteCommandMatches_noMatch() {
        String[] words = {"/ban", "player"};
        assertFalse(universal.muteCommandMatches(words, "/msg"));
    }
}
