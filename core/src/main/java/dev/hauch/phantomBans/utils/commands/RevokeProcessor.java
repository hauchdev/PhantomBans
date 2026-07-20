package dev.hauch.phantomBans.utils.commands;

import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.MessageManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.manager.UUIDManager;
import dev.hauch.phantomBans.utils.Command;
import dev.hauch.phantomBans.utils.Punishment;
import dev.hauch.phantomBans.utils.PunishmentType;

import java.util.function.Consumer;

/**
 * Processes revocation commands (unban, unmute, unwarn, unnote).
 */
public class RevokeProcessor implements Consumer<Command.CommandInput> {

    private final PunishmentType type;

    public RevokeProcessor(PunishmentType type) {
        this.type = type;
    }

    @Override
    public void accept(Command.CommandInput input) {
        String name = input.getPrimary();
        input.next();
        String uuid = UUIDManager.get().getUUID(name.toLowerCase());

        if (uuid == null) {
            MessageManager.sendMessage(input.getSender(), "General.FailedFetch", true, "NAME", name);
            return;
        }

        Punishment punishment = type == PunishmentType.BAN
                ? PunishmentManager.get().getBan(uuid)
                : PunishmentManager.get().getMute(uuid);

        if (punishment == null) {
            MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".NotFound", true, "NAME", name);
            return;
        }

        String operator = Universal.get().getMethods().getName(input.getSender());
        punishment.delete(operator, false, true);
        MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".Done", true, "NAME", name);
    }
}
