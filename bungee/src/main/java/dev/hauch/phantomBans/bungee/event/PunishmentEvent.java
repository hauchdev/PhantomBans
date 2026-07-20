package dev.hauch.phantomBans.bungee.event;

import dev.hauch.phantomBans.utils.Punishment;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called when a punishment is created (BungeeCord).
 */
public class PunishmentEvent extends Event {

    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
    }

    public Punishment getPunishment() {
        return punishment;
    }
}
