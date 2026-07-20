package dev.hauch.phantomBans.utils;

/**
 * Punishment types enumeration.
 */
public enum PunishmentType {
    BAN("Ban", null, false, "pb.ban.perma"),
    TEMP_BAN("Tempban", BAN, true, "pb.ban.temp"),
    IP_BAN("Ipban", BAN, false, "pb.ipban.perma"),
    TEMP_IP_BAN("Tempipban", BAN, true, "pb.ipban.temp"),
    MUTE("Mute", null, false, "pb.mute.perma"),
    TEMP_MUTE("Tempmute", MUTE, true, "pb.mute.temp"),
    WARNING("Warn", null, false, "pb.warn.perma"),
    TEMP_WARNING("Tempwarn", WARNING, true, "pb.warn.temp"),
    KICK("Kick", null, false, "pb.kick.use"),
    NOTE("Note", null, false, "pb.note.use");

    private final String name;
    private final String perms;
    private final PunishmentType basic;
    private final boolean temp;

    PunishmentType(String name, PunishmentType basic, boolean temp, String perms) {
        this.name = name;
        this.basic = basic;
        this.temp = temp;
        this.perms = perms;
    }

    public static PunishmentType fromCommandName(String cmd) {
        return switch (cmd) {
            case "ban" -> BAN;
            case "tempban" -> TEMP_BAN;
            case "ban-ip", "banip", "ipban" -> IP_BAN;
            case "tempipban", "tipban" -> TEMP_IP_BAN;
            case "mute" -> MUTE;
            case "tempmute" -> TEMP_MUTE;
            case "warn" -> WARNING;
            case "note" -> NOTE;
            case "tempwarn" -> TEMP_WARNING;
            case "kick" -> KICK;
            default -> null;
        };
    }

    public String getName() { return name; }
    public String getPerms() { return perms; }
    public boolean isTemp() { return temp; }
    public String getConfSection(String path) { return name + "." + path; }

    public PunishmentType getBasic() {
        return basic == null ? this : basic;
    }

    public PunishmentType getPermanent() {
        if (this == IP_BAN || this == TEMP_IP_BAN) return IP_BAN;
        return getBasic();
    }

    public boolean isIpOrientated() {
        return this == IP_BAN || this == TEMP_IP_BAN;
    }
}
