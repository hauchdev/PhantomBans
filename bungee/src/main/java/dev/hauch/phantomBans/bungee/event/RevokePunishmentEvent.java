package dev.hauch.phantomBans.bungee.event;

import dev.hauch.phantomBans.utils.Punishment;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called when a punishment is revoked (BungeeCord).
 */
public class RevokePunishmentEvent extends Event {

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
