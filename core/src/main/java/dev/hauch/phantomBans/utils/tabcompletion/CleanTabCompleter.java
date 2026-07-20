package dev.hauch.phantomBans.utils.tabcompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Tab completer that generates suggestions dynamically based on sender and args.
 */
public class CleanTabCompleter implements TabCompleter {

    public static final String PLAYER_PLACEHOLDER = "[Player]";

    private final BiFunction<Object, String[], List<String>> completer;

    public CleanTabCompleter(BiFunction<Object, String[], List<String>> completer) {
        this.completer = completer;
    }

    @Override
    public List<String> onTabComplete(Object sender, String[] args) {
        return completer.apply(sender, args);
    }

    /**
     * Utility to create a list.
     */
    public static List<String> list(String... values) {
        if (values == null || values.length == 0) return Collections.emptyList();
        return new ArrayList<>(Arrays.asList(values));
    }
}
