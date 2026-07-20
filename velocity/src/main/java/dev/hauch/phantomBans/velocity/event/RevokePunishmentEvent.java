package dev.hauch.phantomBans.velocity.event;

import dev.hauch.phantomBans.utils.Punishment;

/**
 * Event fired when a punishment is revoked (Velocity).
 */
public class RevokePunishmentEvent {

    private final Punishment punishment;
    private final boolean massClear;

    public RevokePunishmentEvent(Punishment punishment, boolean massClear) {
        this.punishment = punishment;
        this.massClear = massClear;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public boolean isMassClear() {
        return massClear;
    }
}
