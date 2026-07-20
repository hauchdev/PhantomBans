package dev.hauch.phantomBans.utils.commands;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.MessageManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.TimeManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Command;
import dev.hauch.phantomBans.utils.CommandUtils;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.PunishmentType;

import java.util.function.Consumer;

/**
 * Processes punishment creation commands (ban, tempban, mute, kick, warn, etc.).
 */
public class PunishmentProcessor implements Consumer<Command.CommandInput> {

    private final PunishmentType type;

    public PunishmentProcessor(PunishmentType type) {
        this.type = type;
    }

    @Override
    public void accept(Command.CommandInput input) {
        MethodInterface mi = Universal.get().getMethods();

        // Check for silent flag (-s)
        boolean silent = false;
        if (input.getPrimary().equalsIgnoreCase("-s")) {
            silent = true;
            input.next();
        }

        String name = input.getPrimary();
        String target;
        String reason = "";

        if (type.isIpOrientated()) {
            // IP ban - try IP first, then name
            target = name.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$") ? name : CommandUtils.processIP(input);
            if (target == null) return;
        } else {
            input.next();
            target = UUIDManager.get().getUUID(name.toLowerCase());
            if (target == null) {
                MessageManager.sendMessage(input.getSender(), "General.FailedFetch", true, "NAME", name);
                return;
            }
        }

        // Check if target is exempt
        if (Universal.get().isExemptPlayer(name)) {
            MessageManager.sendMessage(input.getSender(), type.getName() + ".Exempt", true, "NAME", name);
            return;
        }

        // Build reason from remaining arguments
        String fullReason = String.join(" ", input.getArgs());

        // Handle time-based punishments
        long end = -1;
        String calculation = "";
        String actualReason = fullReason;

        if (type.isTemp()) {
            if (fullReason.isEmpty()) {
                MessageManager.sendMessage(input.getSender(), type.getName() + ".Usage", true);
                return;
            }
            String[] parts = fullReason.split(" ", 2);
            String timeStr = parts[0];

            if (timeStr.startsWith("#")) {
                // Use layout calculation
                calculation = timeStr.substring(1);
                int level = PunishmentManager.get().getCalculationLevel(target, calculation);
                java.util.List<String> timeLayout = mi.getStringList(mi.getLayouts(), "Time." + calculation);
                if (timeLayout != null && !timeLayout.isEmpty()) {
                    String time = timeLayout.get(Math.min(level, timeLayout.size() - 1));
                    end = TimeManager.getTime() + TimeManager.toMilliSec(time.toLowerCase());
                } else {
                    MessageManager.sendMessage(input.getSender(), "General.LayoutNotFound", true, "NAME", calculation);
                    return;
                }
                actualReason = parts.length > 1 ? parts[1] : "";
            } else {
                // Direct time parsing
                end = parseTime(timeStr);
                if (end <= 0) {
                    MessageManager.sendMessage(input.getSender(), type.getName() + ".Usage", true);
                    return;
                }
                end += TimeManager.getTime();
                actualReason = parts.length > 1 ? parts[1] : "";
            }
        }

        // Create the punishment
        Punishment punishment = new Punishment(
                name, target, actualReason, mi.getName(input.getSender()),
                type, TimeManager.getTime(), end, calculation, -1);
        punishment.create(silent);

        // Send done message
        if (type != PunishmentType.KICK) {
            MessageManager.sendMessage(input.getSender(), type.getName() + ".Done", true,
                    "NAME", name, "REASON", actualReason.isEmpty() ? mi.getString(mi.getConfig(), "DefaultReason", "none") : actualReason);
        }
    }

    /**
     * Parse a time string (e.g., "7d", "30m", "2h") to milliseconds.
     */
    private long parseTime(String timeStr) {
        try {
            String number = timeStr.replaceAll("[^0-9]", "");
            String unit = timeStr.replaceAll("[0-9]", "").toLowerCase();
            long value = Long.parseLong(number);
            return TimeManager.toMilliSec(value + unit);
        } catch (Exception e) {
            return -1;
        }
    }
}
