package dev.hauch.phantomBans.utils.commands;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.MessageManager;
import dev.hauch.phantomBans.utils.Command;
import dev.hauch.phantomBans.utils.Punishment;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Processes revocation by ID (unpunish, unwarn by ID, unnote by ID).
 */
public class RevokeByIdProcessor implements Consumer<Command.CommandInput> {

    private final String confSection;
    private final Function<Integer, Punishment> punishmentSupplier;

    public RevokeByIdProcessor(String confSection, Function<Integer, Punishment> punishmentSupplier) {
        this.confSection = confSection;
        this.punishmentSupplier = punishmentSupplier;
    }

    @Override
    public void accept(Command.CommandInput input) {
        try {
            int id = Integer.parseInt(input.getPrimary());
            Punishment punishment = punishmentSupplier.apply(id);
            if (punishment == null) {
                MessageManager.sendMessage(input.getSender(), confSection + ".NotFound", true, "ID", String.valueOf(id));
                return;
            }
            String operator = Universal.get().getMethods().getName(input.getSender());
            punishment.delete(operator, false, true);
            MessageManager.sendMessage(input.getSender(), confSection + ".Done", true, "ID", String.valueOf(id));
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(input.getSender(), confSection + ".Usage", true);
        }
    }
}
