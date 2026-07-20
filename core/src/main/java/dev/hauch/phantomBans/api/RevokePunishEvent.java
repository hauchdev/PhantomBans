package dev.hauch.phantomBans.api;

import dev.hauch.phantomBans.utils.Punishment;

/**
 * API event fired when a punishment is revoked (unbanned, unmuted, unwarned, etc.).
 * Other plugins can listen for this via {@link PhantomBansAPI#addListener(PunishmentListener)}.
 */
public class RevokePunishEvent {

    private final Punishment punishment;
    private final boolean massClear;
    private final String revokedBy;

    public RevokePunishEvent(Punishment punishment, boolean massClear, String revokedBy) {
        this.punishment = punishment;
        this.massClear = massClear;
        this.revokedBy = revokedBy;
    }

    /**
     * Get the punishment that was revoked.
     *
     * @return the punishment
     */
    public Punishment getPunishment() {
        return punishment;
    }

    /**
     * Check if this was part of a mass clear operation.
     *
     * @return true if mass clear
     */
    public boolean isMassClear() {
        return massClear;
    }

    /**
     * Get who revoked the punishment.
     *
     * @return operator name, or null if auto-expired
     */
    public String getRevokedBy() {
        return revokedBy;
    }

    /**
     * Get the punishment ID.
     *
     * @return the ID
     */
    public int getId() {
        return punishment.getId();
    }

    /**
     * Get the player's name.
     *
     * @return player name
     */
    public String getPlayerName() {
        return punishment.getName();
    }

    /**
     * Get the player's UUID.
     *
     * @return UUID string
     */
    public String getPlayerUuid() {
        return punishment.getUuid();
    }

    /**
     * Get the punishment type.
     *
     * @return type name
     */
    public String getType() {
        return punishment.getType().getName();
    }
}
