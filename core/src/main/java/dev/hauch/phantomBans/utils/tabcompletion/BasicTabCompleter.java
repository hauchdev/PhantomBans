package dev.hauch.phantomBans.utils.tabcompletion;

import java.util.Arrays;
import java.util.List;

/**
 * Basic tab completer with static suggestions.
 */
public class BasicTabCompleter implements TabCompleter {

    private final List<String> suggestions;

    public BasicTabCompleter(String... suggestions) {
        this.suggestions = Arrays.asList(suggestions);
    }

    @Override
    public List<String> onTabComplete(Object sender, String[] args) {
        if (args.length <= suggestions.size()) {
            return suggestions;
        }
        return java.util.Collections.emptyList();
    }
}
