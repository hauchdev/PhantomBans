package dev.hauch.phantomBans.manager;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Message Manager for retrieving and formatting messages from configuration files.
 * <p>
 * Now uses <strong>MiniMessage format</strong> ({@code <red>}, {@code <bold>}, etc.)
 * instead of legacy {@code &} color codes. All YAML message files should use
 * MiniMessage tag syntax.
 * </p>
 * <p>
 * Placeholders ({@code %NAME%}, {@code %REASON%}, etc.) are replaced
 * <em>after</em> MiniMessage deserialization, so they may contain legacy
 * {@code §} color codes if needed.
 * </p>
 *
 * @see <a href="https://docs.advntr.dev/minimessage/format.html">MiniMessage Format</a>
 */
public class MessageManager {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder().character('§').hexColors().build();

    private static MethodInterface mi() {
        return Universal.get().getMethods();
    }

    /**
     * Get a message from the messages file, parse MiniMessage tags,
     * convert to legacy {@code §} format, and replace placeholders.
     *
     * @param path       the config path (e.g. {@code "Ban.Done"})
     * @param parameters placeholder pairs: {@code "NAME", value, "REASON", value, ...}
     * @return the fully formatted legacy string
     */
    public static String getMessage(String path, String... parameters) {
        MethodInterface mi = mi();
        String str = mi.getString(mi.getMessages(), path);
        if (str == null) {
            System.out.println("!! Message-Error!\n"
                    + "In order to solve the problem please:"
                    + "\n - Check the Messages.yml-File for any missing or invalid MiniMessage tags"
                    + "\n - Visit docs.advntr.dev/minimessage/format.html for valid syntax"
                    + "\n - Delete the message file and restart the server");
            return "§cFailed! See console for details!";
        }
        return parseAndReplace(str, parameters);
    }

    /**
     * Get a message, optionally prepended with the plugin prefix.
     *
     * @param path       the config path
     * @param prefix     {@code true} to prepend the prefix
     * @param parameters placeholder pairs
     * @return the formatted message with optional prefix
     */
    public static String getMessage(String path, boolean prefix, String... parameters) {
        MethodInterface mi = mi();
        String prefixStr = "";
        if (prefix && !mi.getBoolean(mi.getConfig(), "Disable Prefix", false)) {
            prefixStr = getMessage("General.Prefix") + " ";
        }
        return prefixStr + getMessage(path, parameters);
    }

    /**
     * Get a multi-line layout from the given config file, parse MiniMessage tags,
     * and replace placeholders.
     *
     * @param file       the config file object (messages or layouts)
     * @param path       the config path
     * @param parameters placeholder pairs
     * @return list of formatted legacy strings
     */
    public static List<String> getLayout(Object file, String path, String... parameters) {
        MethodInterface mi = mi();
        if (mi.contains(file, path)) {
            List<String> list = new ArrayList<>();
            for (String str : mi.getStringList(file, path)) {
                list.add(parseAndReplace(str, parameters));
            }
            return list;
        }
        String fileName = mi.getFileName(file);
        System.out.println("!! Message-Error in " + fileName + "!\n"
                + "In order to solve the problem please:"
                + "\n - Check the " + fileName + "-File for any missing or invalid MiniMessage tags"
                + "\n - Visit docs.advntr.dev/minimessage/format.html for valid syntax"
                + "\n - Delete the message file and restart the server");
        return Collections.singletonList("§cFailed! See console for details!");
    }

    /**
     * Send a formatted message to a receiver.
     *
     * @param receiver   the player/command sender
     * @param path       the config path
     * @param prefix     {@code true} to prepend the plugin prefix
     * @param parameters placeholder pairs
     */
    public static void sendMessage(Object receiver, String path, boolean prefix, String... parameters) {
        MethodInterface mi = mi();
        final String message = getMessage(path, parameters);
        if (!message.isEmpty()) {
            final String prefixString = prefix && !mi.getBoolean(mi.getConfig(), "Disable Prefix", false)
                    ? getMessage("General.Prefix") + " " : "";
            mi.sendMessage(receiver, prefixString + message);
        }
    }

    // ── Private Helpers ───────────────────────────────────────────────────── //

    /**
     * Parse a MiniMessage-formatted string, serialize to legacy {@code §} format,
     * then replace {@code %PLACEHOLDER%} tokens with their values.
     * <p>
     * MiniMessage parsing happens <em>first</em> so that placeholder values
     * (which may contain user-provided text like {@code <red>}) are not
     * misinterpreted as MiniMessage tags. Placeholders like {@code %NAME%}
     * are not valid MiniMessage syntax and pass through unchanged.
     * </p>
     */
    private static String parseAndReplace(String template, String... parameters) {
        // Parse MiniMessage tags first — placeholders (%NAME%, etc.) are not
        // valid MiniMessage tags and will pass through the parser unchanged.
        Component component = MINI_MESSAGE.deserialize(template);
        // Serialize to legacy § format for Minecraft client display
        String legacy = LEGACY_SERIALIZER.serialize(component);
        // Replace placeholders AFTER MiniMessage parsing so that values
        // (which may contain § codes or other text) are not reinterpreted.
        return replace(legacy, parameters);
    }

    /**
     * Replace {@code %KEY%} occurrences with corresponding values.
     * Parameters are in key-value pairs: {@code key1, val1, key2, val2, ...}
     */
    private static String replace(String str, String... parameters) {
        for (int i = 0; i < parameters.length - 1; i += 2) {
            str = str.replace("%" + parameters[i] + "%", parameters[i + 1]);
        }
        return str;
    }
}
