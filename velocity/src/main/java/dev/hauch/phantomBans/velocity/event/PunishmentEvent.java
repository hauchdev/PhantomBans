package dev.hauch.phantomBans.velocity.event;

import dev.hauch.phantomBans.utils.Punishment;

/**
 * Event fired when a punishment is created (Velocity).
 */
public class PunishmentEvent {

    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
    }

    public Punishment getPunishment() {
        return punishment;
    }
}
