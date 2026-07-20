package dev.hauch.phantomBans.utils.tabcompletion;

/**
 * Interface for tab completion logic.
 */
@FunctionalInterface
public interface TabCompleter {
    /**
     * Get tab completion options.
     *
     * @param sender the command sender
     * @param args   the current arguments
     * @return list of completion options
     */
    java.util.List<String> onTabComplete(Object sender, String[] args);
}
