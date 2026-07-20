package dev.hauch.phantomBans.bukkit.event;

import dev.hauch.phantomBans.utils.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a punishment is created.
 */
public class PunishmentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
