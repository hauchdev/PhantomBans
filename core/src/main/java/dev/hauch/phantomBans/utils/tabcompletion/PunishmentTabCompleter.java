package dev.hauch.phantomBans.utils.tabcompletion;

import dev.hauch.phantomBans.Universal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer for punishment commands that suggests player names.
 */
public class PunishmentTabCompleter implements TabCompleter {

    private final boolean suggestDuration;

    public PunishmentTabCompleter(boolean suggestDuration) {
        this.suggestDuration = suggestDuration;
    }

    @Override
    public List<String> onTabComplete(Object sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest online player names
            for (Object player : Universal.get().getMethods().getOnlinePlayers()) {
                suggestions.add(Universal.get().getMethods().getName(player));
            }
        } else if (args.length == 2 && suggestDuration) {
            // Suggest durations for temp commands
            suggestions.add("1d");
            suggestions.add("7d");
            suggestions.add("14d");
            suggestions.add("30d");
            suggestions.add("1h");
            suggestions.add("6h");
            suggestions.add("12h");
            suggestions.add("-s");
        } else if (args.length == 2) {
            suggestions.add("-s");
            suggestions.add("[Reason]");
        } else if (args.length >= 3) {
            suggestions.add("[Reason]");
        }

        return suggestions.stream()
                .filter(s -> args[args.length - 1].isEmpty() || s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
