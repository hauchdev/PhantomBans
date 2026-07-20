package dev.hauch.phantomBans.utils.commands;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.MessageManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Command;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.SQLQuery;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Processes list commands with pagination (banlist, history, warns, notes).
 */
public class ListProcessor implements Consumer<Command.CommandInput> {

    private final Function<String, List<Punishment>> punishmentProvider;
    private final String confSection;
    private final boolean requiresPlayer;
    private final boolean showTypes;

    /**
     * @param punishmentProvider function to get punishments
     * @param confSection        config section for messages
     * @param requiresPlayer     whether a player name is required
     * @param showTypes          whether to show punishment types in entries
     */
    public ListProcessor(Function<String, List<Punishment>> punishmentProvider,
                         String confSection, boolean requiresPlayer, boolean showTypes) {
        this.punishmentProvider = punishmentProvider;
        this.confSection = confSection;
        this.requiresPlayer = requiresPlayer;
        this.showTypes = showTypes;
    }

    @Override
    public void accept(Command.CommandInput input) {
        MethodInterface mi = Universal.get().getMethods();

        // Determine the target (player name or null for global lists)
        String target = null;
        if (requiresPlayer || input.hasNext()) {
            String name = input.getPrimary();
            input.next();

            // Check if name is a page number
            if (name.matches("[1-9][0-9]*")) {
                // This is a page number, not a player name
                if (requiresPlayer) {
                    MessageManager.sendMessage(input.getSender(), confSection + ".Usage", true);
                    return;
                }
                // For global lists, pass null and use name as page
                target = null;
                // Re-process with page
                processList(input.getSender(), target, Integer.parseInt(name));
                return;
            }

            String uuid = UUIDManager.get().getUUID(name.toLowerCase());
            if (uuid == null) {
                MessageManager.sendMessage(input.getSender(), "General.FailedFetch", true, "NAME", name);
                return;
            }
            target = uuid;
        }

        // Get page number
        int page = 1;
        if (input.hasNext()) {
            try {
                page = Integer.parseInt(input.getPrimary());
            } catch (NumberFormatException ignored) {}
        }

        processList(input.getSender(), target, page);
    }

    private void processList(Object sender, String target, int page) {
        MethodInterface mi = Universal.get().getMethods();
        List<Punishment> punishments;

        if (target != null) {
            punishments = punishmentProvider.apply(target);
        } else {
            // Global list (banlist)
            punishments = punishmentProvider.apply(null);
        }

        if (punishments == null || punishments.isEmpty()) {
            MessageManager.sendMessage(sender, confSection + ".NoEntries", true,
                    "NAME", target != null ? mi.getName(target) : "");
            return;
        }

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) punishments.size() / pageSize);

        if (page < 1 || page > totalPages) {
            MessageManager.sendMessage(sender, confSection + ".OutOfIndex", true, "PAGE", String.valueOf(page));
            return;
        }

        // Send header
        List<String> header = MessageManager.getLayout(mi.getMessages(), confSection + ".Header",
                "PREFIX", MessageManager.getMessage("General.Prefix"),
                "NAME", target != null ? mi.getName(target) : "");
        for (String line : header) {
            mi.sendMessage(sender, line);
        }

        // Send entries
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, punishments.size());
        for (int i = start; i < end; i++) {
            Punishment p = punishments.get(i);
            List<String> entry = MessageManager.getLayout(mi.getMessages(), confSection + ".Entry",
                    "NAME", p.getName(),
                    "TYPE", showTypes ? p.getType().getName() : "",
                    "REASON", p.getReason(),
                    "OPERATOR", p.getOperator(),
                    "DATE", p.getDate(p.getStart()),
                    "DURATION", p.getDuration(false),
                    "ID", String.valueOf(p.getId()),
                    "HEXID", p.getHexId());
            for (String line : entry) {
                mi.sendMessage(sender, line);
            }
        }

        // Send footer
        String footer = MessageManager.getMessage(confSection + ".Footer", false,
                "CURRENT_PAGE", String.valueOf(page),
                "TOTAL_PAGES", String.valueOf(totalPages),
                "COUNT", String.valueOf(punishments.size()));
        mi.sendMessage(sender, footer);

        // Send page footer if not on last page
        if (page < totalPages) {
            String pageFooter = MessageManager.getMessage(confSection + ".PageFooter", false,
                    "NEXT_PAGE", String.valueOf(page + 1),
                    "NAME", target != null ? mi.getName(target) : "");
            mi.sendMessage(sender, pageFooter);
        }
    }
}
