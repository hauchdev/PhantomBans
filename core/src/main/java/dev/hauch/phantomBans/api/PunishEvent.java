package dev.hauch.phantomBans.api;

import dev.hauch.phantomBans.utils.Punishment;

/**
 * API event fired when a punishment is created.
 * Other plugins can listen for this via {@link PhantomBansAPI#addListener(PunishmentListener)}.
 */
public class PunishEvent {

    private final Punishment punishment;
    private final boolean silent;

    public PunishEvent(Punishment punishment, boolean silent) {
        this.punishment = punishment;
        this.silent = silent;
    }

    /**
     * Get the punishment that was created.
     *
     * @return the punishment
     */
    public Punishment getPunishment() {
        return punishment;
    }

    /**
     * Check if this punishment was created silently (no broadcast).
     *
     * @return true if silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * Get the punishment ID.
     *
     * @return the ID, or -1 if not yet assigned
     */
    public int getId() {
        return punishment.getId();
    }

    /**
     * Get the punished player's name.
     *
     * @return player name
     */
    public String getPlayerName() {
        return punishment.getName();
    }

    /**
     * Get the punished player's UUID.
     *
     * @return UUID string
     */
    public String getPlayerUuid() {
        return punishment.getUuid();
    }

    /**
     * Get the operator who issued the punishment.
     *
     * @return operator name
     */
    public String getOperator() {
        return punishment.getOperator();
    }

    /**
     * Get the punishment reason.
     *
     * @return reason string
     */
    public String getReason() {
        return punishment.getReason();
    }

    /**
     * Get the punishment type name (BAN, MUTE, WARNING, KICK, NOTE).
     *
     * @return type name
     */
    public String getType() {
        return punishment.getType().getName();
    }

    /**
     * Get the duration in milliseconds, or -1 for permanent.
     *
     * @return duration end timestamp
     */
    public long getEnd() {
        return punishment.getEnd();
    }

    /**
     * Check if the punishment is temporary.
     *
     * @return true if temporary
     */
    public boolean isTemporary() {
        return punishment.getType().isTemp();
    }

    /**
     * Check if the punishment is a ban-type (ban, tempban, ipban).
     *
     * @return true if ban
     */
    public boolean isBan() {
        return punishment.getType().getBasic().name().equals("BAN");
    }

    /**
     * Check if the punishment is a mute-type.
     *
     * @return true if mute
     */
    public boolean isMute() {
        return punishment.getType().getBasic().name().equals("MUTE");
    }
}
