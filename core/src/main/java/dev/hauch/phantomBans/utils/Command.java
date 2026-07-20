package dev.hauch.phantomBans.utils;

import dev.hauch.phantomBans.MethodInterface;
import dev.hauch.phantomBans.Universal;
import dev.hauch.phantomBans.manager.MessageManager;
import dev.hauch.phantomBans.manager.PunishmentManager;
import dev.hauch.phantomBans.utils.commands.ListProcessor;
import dev.hauch.phantomBans.utils.commands.PunishmentProcessor;
import dev.hauch.phantomBans.utils.commands.RevokeByIdProcessor;
import dev.hauch.phantomBans.utils.commands.RevokeProcessor;
import dev.hauch.phantomBans.utils.tabcompletion.BasicTabCompleter;
import dev.hauch.phantomBans.utils.tabcompletion.CleanTabCompleter;
import dev.hauch.phantomBans.utils.tabcompletion.PunishmentTabCompleter;
import dev.hauch.phantomBans.utils.tabcompletion.TabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Command enum representing all punishment commands with dedicated processors.
 */
public enum Command {

    // ── Punishment creation commands ─────────────────────────────────── //
    BAN(
            PunishmentType.BAN.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.BAN),
            PunishmentType.BAN.getConfSection("Usage"),
            "ban"
    ),
    TEMP_BAN(
            PunishmentType.TEMP_BAN.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_BAN),
            PunishmentType.TEMP_BAN.getConfSection("Usage"),
            "tempban"
    ),
    IP_BAN(
            PunishmentType.IP_BAN.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.IP_BAN),
            PunishmentType.IP_BAN.getConfSection("Usage"),
            "ipban", "banip", "ban-ip"
    ),
    TEMP_IP_BAN(
            PunishmentType.TEMP_IP_BAN.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_IP_BAN),
            PunishmentType.TEMP_IP_BAN.getConfSection("Usage"),
            "tempipban"
    ),
    MUTE(
            PunishmentType.MUTE.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.MUTE),
            PunishmentType.MUTE.getConfSection("Usage"),
            "mute"
    ),
    TEMP_MUTE(
            PunishmentType.TEMP_MUTE.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_MUTE),
            PunishmentType.TEMP_MUTE.getConfSection("Usage"),
            "tempmute"
    ),
    WARN(
            PunishmentType.WARNING.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.WARNING),
            PunishmentType.WARNING.getConfSection("Usage"),
            "warn"
    ),
    TEMP_WARN(
            PunishmentType.TEMP_WARNING.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_WARNING),
            PunishmentType.TEMP_WARNING.getConfSection("Usage"),
            "tempwarn"
    ),
    NOTE(
            PunishmentType.NOTE.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.NOTE),
            PunishmentType.NOTE.getConfSection("Usage"),
            "note"
    ),
    KICK(
            PunishmentType.KICK.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            input -> {
                if (!Universal.get().getMethods().isOnline(input.getPrimary())) {
                    MessageManager.sendMessage(input.getSender(), "Kick.NotOnline", true, "NAME", input.getPrimary());
                    return;
                }
                new PunishmentProcessor(PunishmentType.KICK).accept(input);
            },
            PunishmentType.KICK.getConfSection("Usage"),
            "kick"
    ),

    // ── Revocation commands ──────────────────────────────────────────── //
    UN_BAN(
            "pb." + PunishmentType.BAN.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter("[Name]"),
            new RevokeProcessor(PunishmentType.BAN),
            "Un" + PunishmentType.BAN.getConfSection("Usage"),
            "unban"
    ),
    UN_MUTE(
            "pb." + PunishmentType.MUTE.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter("[Name]"),
            new RevokeProcessor(PunishmentType.MUTE),
            "Un" + PunishmentType.MUTE.getConfSection("Usage"),
            "unmute"
    ),
    UN_WARN(
            "pb." + PunishmentType.WARNING.getName() + ".undo",
            "[0-9]+|(?i:clear \\S+)",
            new CleanTabCompleter((user, args) -> {
                if (args.length == 1) {
                    return CleanTabCompleter.list("[ID]", "clear");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
                    return CleanTabCompleter.list("[Name]");
                }
                return CleanTabCompleter.list();
            }),
            input -> {
                final String confSection = PunishmentType.WARNING.getName();
                if (input.getPrimaryData().equals("clear")) {
                    input.next();
                    String name = input.getPrimary();
                    String uuid = CommandUtils.processName(input);
                    if (uuid == null) return;

                    List<Punishment> punishments = PunishmentManager.get().getWarns(uuid);
                    if (punishments.isEmpty()) {
                        MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Empty", true, "NAME", name);
                        return;
                    }
                    String operator = Universal.get().getMethods().getName(input.getSender());
                    for (Punishment punishment : punishments) {
                        punishment.delete(operator, true, true);
                    }
                    MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Done", true,
                            "COUNT", String.valueOf(punishments.size()));
                } else {
                    new RevokeByIdProcessor("Un" + confSection,
                            (id) -> PunishmentManager.get().getWarn(id)).accept(input);
                }
            },
            "Un" + PunishmentType.WARNING.getConfSection("Usage"),
            "unwarn"
    ),
    UN_NOTE(
            "pb." + PunishmentType.NOTE.getName() + ".undo",
            "[0-9]+|(?i:clear \\S+)",
            new CleanTabCompleter((user, args) -> {
                if (args.length == 1) {
                    return CleanTabCompleter.list("[ID]", "clear");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
                    return CleanTabCompleter.list("[Name]");
                }
                return CleanTabCompleter.list();
            }),
            input -> {
                final String confSection = PunishmentType.NOTE.getName();
                if (input.getPrimaryData().equals("clear")) {
                    input.next();
                    String name = input.getPrimary();
                    String uuid = CommandUtils.processName(input);
                    if (uuid == null) return;

                    List<Punishment> punishments = PunishmentManager.get().getNotes(uuid);
                    if (punishments.isEmpty()) {
                        MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Empty", true, "NAME", name);
                        return;
                    }
                    String operator = Universal.get().getMethods().getName(input.getSender());
                    for (Punishment punishment : punishments) {
                        punishment.delete(operator, true, true);
                    }
                    MessageManager.sendMessage(input.getSender(), "Un" + confSection + ".Clear.Done", true,
                            "COUNT", String.valueOf(punishments.size()));
                } else {
                    new RevokeByIdProcessor("Un" + confSection,
                            (id) -> PunishmentManager.get().getNote(id)).accept(input);
                }
            },
            "Un" + PunishmentType.NOTE.getConfSection("Usage"),
            "unnote"
    ),
    UN_PUNISH(
            "pb.all.undo",
            "[0-9]+",
            new BasicTabCompleter("<ID>"),
            new RevokeByIdProcessor("UnPunish",
                    (id) -> PunishmentManager.get().getPunishment(id)),
            "UnPunish.Usage",
            "unpunish"
    ),

    // ── Utility commands ─────────────────────────────────────────────── //
    CHANGE_REASON(
            "pb.changeReason",
            "([0-9]+|(?i)(ban|mute) \\S+) .+",
            new CleanTabCompleter((user, args) -> {
                if (args.length <= 1) {
                    return CleanTabCompleter.list("<ID>", "ban", "mute");
                }
                boolean playerTarget = args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("mute");
                if (args.length == 2 && playerTarget) {
                    return CleanTabCompleter.list("[Name]");
                } else if ((playerTarget && args.length == 3) || args.length == 2) {
                    return CleanTabCompleter.list("new reason...");
                }
                return CleanTabCompleter.list();
            }),
            input -> {
                Punishment punishment;
                if (input.getPrimaryData().matches("[0-9]*")) {
                    int id = Integer.parseInt(input.getPrimaryData());
                    input.next();
                    punishment = PunishmentManager.get().getPunishment(id);
                } else {
                    PunishmentType type = PunishmentType.valueOf(input.getPrimary().toUpperCase());
                    input.next();
                    String target = input.getPrimary();
                    if (!target.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                        target = CommandUtils.processName(input);
                        if (target == null) return;
                    } else {
                        input.next();
                    }
                    punishment = CommandUtils.getPunishment(target, type);
                }
                String reason = CommandUtils.processReason(input);
                if (reason == null) return;

                if (punishment != null) {
                    punishment.updateReason(reason);
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.Done", true,
                            "ID", String.valueOf(punishment.getId()));
                } else {
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.NotFound", true);
                }
            },
            "ChangeReason.Usage",
            "change-reason"
    ),

    // ── List commands ────────────────────────────────────────────────── //
    BAN_LIST(
            "pb.banlist",
            "([1-9][0-9]*)?",
            new BasicTabCompleter("<Page>"),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(SQLQuery.SELECT_ALL_PUNISHMENTS_LIMIT, 150),
                    "Banlist", false, false),
            "Banlist.Usage",
            "banlist"
    ),
    HISTORY(
            "pb.history",
            "\\S+( [1-9][0-9]*)?",
            new CleanTabCompleter((user, args) -> {
                if (args.length == 1) return CleanTabCompleter.list("[Name]");
                else if (args.length == 2) return CleanTabCompleter.list("<Page>");
                return CleanTabCompleter.list();
            }),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(target, null, false),
                    "History", true, true),
            "History.Usage",
            "history"
    ),
    WARNS(
            null,
            "\\S+( [1-9][0-9]*)?|\\S+|",
            new CleanTabCompleter((user, args) -> {
                if (args.length == 1) {
                    if (Universal.get().getMethods().hasPerms(user, "pb.warns.other"))
                        return CleanTabCompleter.list("[Name]", "<Page>");
                    else
                        return CleanTabCompleter.list("<Page>");
                } else if (args.length == 2 && !args[0].matches("\\d+")) {
                    return CleanTabCompleter.list("<Page>");
                }
                return CleanTabCompleter.list();
            }),
            input -> {
                if (input.hasNext() && !input.getPrimary().matches("[1-9][0-9]*")) {
                    // Other player's warns
                    if (!Universal.get().hasPerms(input.getSender(), "pb.warns.other")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }
                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(target, PunishmentType.WARNING, true),
                            "Warns", false, true).accept(input);
                } else {
                    // Own warns
                    if (!Universal.get().hasPerms(input.getSender(), "pb.warns.own")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }
                    String name = Universal.get().getMethods().getName(input.getSender());
                    String identifier = CommandUtils.processName(new Command.CommandInput(input.getSender(), new String[]{name}));
                    if (identifier != null) {
                        new ListProcessor(
                                target -> PunishmentManager.get().getPunishments(identifier, PunishmentType.WARNING, true),
                                "WarnsOwn", false, false).accept(input);
                    }
                }
            },
            "Warns.Usage",
            "warns"
    ),
    NOTES(
            null,
            "\\S+( [1-9][0-9]*)?|\\S+|",
            new CleanTabCompleter((user, args) -> {
                if (args.length == 1) {
                    if (Universal.get().getMethods().hasPerms(user, "pb.notes.other"))
                        return CleanTabCompleter.list("[Name]", "<Page>");
                    else
                        return CleanTabCompleter.list("<Page>");
                } else if (args.length == 2 && !args[0].matches("\\d+")) {
                    return CleanTabCompleter.list("<Page>");
                }
                return CleanTabCompleter.list();
            }),
            input -> {
                if (input.hasNext() && !input.getPrimary().matches("[1-9][0-9]*")) {
                    if (!Universal.get().hasPerms(input.getSender(), "pb.notes.other")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }
                    new ListProcessor(
                            target -> PunishmentManager.get().getPunishments(target, PunishmentType.NOTE, true),
                            "Notes", false, true).accept(input);
                } else {
                    if (!Universal.get().hasPerms(input.getSender(), "pb.notes.own")) {
                        MessageManager.sendMessage(input.getSender(), "General.NoPerms", true);
                        return;
                    }
                    String name = Universal.get().getMethods().getName(input.getSender());
                    String identifier = CommandUtils.processName(new Command.CommandInput(input.getSender(), new String[]{name}));
                    if (identifier != null) {
                        new ListProcessor(
                                target -> PunishmentManager.get().getPunishments(identifier, PunishmentType.NOTE, true),
                                "NotesOwn", false, false).accept(input);
                    }
                }
            },
            "Notes.Usage",
            "notes"
    ),

    // ── Info commands ────────────────────────────────────────────────── //
    CHECK(
            "pb.check",
            "\\S+",
            new BasicTabCompleter("[Name]"),
            input -> {
                String name = input.getPrimary();
                String uuid = CommandUtils.processName(input);
                if (uuid == null) return;

                String ip = Universal.get().getIps().getOrDefault(name.toLowerCase(), "not cached");
                String loc = Universal.get().getMethods().getFromUrlJson("http://ip-api.com/json/" + ip, "country");
                Punishment mute = PunishmentManager.get().getMute(uuid);
                Punishment ban = PunishmentManager.get().getBan(uuid);
                String cached = MessageManager.getMessage("Check.Cached", false);
                String notCached = MessageManager.getMessage("Check.NotCached", false);
                boolean nameCached = PunishmentManager.get().isCached(name.toLowerCase());
                boolean ipCached = PunishmentManager.get().isCached(ip);
                boolean uuidCached = PunishmentManager.get().isCached(uuid);

                Object sender = input.getSender();
                MethodInterface mi = Universal.get().getMethods();
                MessageManager.sendMessage(sender, "Check.Header", true,
                        "NAME", name, "CACHED", nameCached ? cached : notCached);
                MessageManager.sendMessage(sender, "Check.UUID", false,
                        "UUID", uuid, "CACHED", uuidCached ? cached : notCached);
                if (Universal.get().hasPerms(sender, "pb.check.ip")) {
                    MessageManager.sendMessage(sender, "Check.IP", false,
                            "IP", ip, "CACHED", ipCached ? cached : notCached);
                }
                MessageManager.sendMessage(sender, "Check.Geo", false, "LOCATION", loc == null ? "failed!" : loc);
                MessageManager.sendMessage(sender, "Check.Mute", false,
                        "DURATION", mute == null ? "§anone" : mute.getType().isTemp() ? "§e" + mute.getDuration(false) : "§cperma");
                if (mute != null) {
                    MessageManager.sendMessage(sender, "Check.MuteReason", false, "REASON", mute.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Ban", false,
                        "DURATION", ban == null ? "§anone" : ban.getType().isTemp() ? "§e" + ban.getDuration(false) : "§cperma");
                if (ban != null) {
                    MessageManager.sendMessage(sender, "Check.BanReason", false, "REASON", ban.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Warn", false, "COUNT", String.valueOf(PunishmentManager.get().getCurrentWarns(uuid)));
                MessageManager.sendMessage(sender, "Check.Note", false, "COUNT", String.valueOf(PunishmentManager.get().getCurrentNotes(uuid)));
            },
            "Check.Usage",
            "check"
    ),

    // ── Admin commands ───────────────────────────────────────────────── //
    ADVANCED_BAN(
            null,
            ".*",
            new BasicTabCompleter("help", "reload"),
            input -> {
                MethodInterface mi = Universal.get().getMethods();
                Object sender = input.getSender();
                if (input.hasNext()) {
                    if (input.getPrimaryData().equals("reload")) {
                        if (Universal.get().hasPerms(sender, "pb.reload")) {
                            mi.loadFiles();
                            mi.sendMessage(sender, "§a§lPhantomBans §8§l» §7Reloaded!");
                        } else {
                            MessageManager.sendMessage(sender, "General.NoPerms", true);
                        }
                        return;
                    } else if (input.getPrimaryData().equals("help")) {
                        if (Universal.get().hasPerms(sender, "pb.help")) {
                            mi.sendMessage(sender, "§8");
                            mi.sendMessage(sender, "§c§lPhantomBans §7Command-Help");
                            mi.sendMessage(sender, "§8");
                            mi.sendMessage(sender, "§c/ban [Name] [Reason/@Layout]");
                            mi.sendMessage(sender, "§8» §7Ban a user permanently");
                            mi.sendMessage(sender, "§c/tempban [Name] [Time] [Reason]");
                            mi.sendMessage(sender, "§8» §7Ban a user temporarily");
                            mi.sendMessage(sender, "§c/ipban [Name/IP] [Reason]");
                            mi.sendMessage(sender, "§8» §7IP-ban a user");
                            mi.sendMessage(sender, "§c/mute [Name] [Reason/@Layout]");
                            mi.sendMessage(sender, "§8» §7Mute a user permanently");
                            mi.sendMessage(sender, "§c/tempmute [Name] [Time] [Reason]");
                            mi.sendMessage(sender, "§8» §7Mute a user temporarily");
                            mi.sendMessage(sender, "§c/kick [Name] [Reason/@Layout]");
                            mi.sendMessage(sender, "§8» §7Kick a user");
                            mi.sendMessage(sender, "§c/warn [Name] [Reason/@Layout]");
                            mi.sendMessage(sender, "§8» §7Warn a user");
                            mi.sendMessage(sender, "§c/tempwarn [Name] [Time] [Reason]");
                            mi.sendMessage(sender, "§8» §7Warn a user temporarily");
                            mi.sendMessage(sender, "§c/note [Name] [Reason]");
                            mi.sendMessage(sender, "§8» §7Note a user");
                            mi.sendMessage(sender, "§c/unban [Name]");
                            mi.sendMessage(sender, "§8» §7Unban a user");
                            mi.sendMessage(sender, "§c/unmute [Name]");
                            mi.sendMessage(sender, "§8» §7Unmute a user");
                            mi.sendMessage(sender, "§c/unwarn [ID] or /unwarn clear [Name]");
                            mi.sendMessage(sender, "§8» §7Remove a warning");
                            mi.sendMessage(sender, "§c/unnote [ID] or /unnote clear [Name]");
                            mi.sendMessage(sender, "§8» §7Remove a note");
                            mi.sendMessage(sender, "§c/history [Name] <Page>");
                            mi.sendMessage(sender, "§8» §7View punishment history");
                            mi.sendMessage(sender, "§c/banlist <Page>");
                            mi.sendMessage(sender, "§8» §7List all active bans");
                            mi.sendMessage(sender, "§c/check [Name]");
                            mi.sendMessage(sender, "§8» §7Check a user's punishment status");
                            mi.sendMessage(sender, "§c/warns [Name] <Page>");
                            mi.sendMessage(sender, "§8» §7View warnings");
                            mi.sendMessage(sender, "§c/notes [Name] <Page>");
                            mi.sendMessage(sender, "§8» §7View notes");
                            mi.sendMessage(sender, "§c/change-reason [ID] [New Reason]");
                            mi.sendMessage(sender, "§8» §7Change a punishment reason");
                            mi.sendMessage(sender, "§c/unpunish [ID]");
                            mi.sendMessage(sender, "§8» §7Remove any punishment by ID");
                        }
                        return;
                    }
                }
                mi.sendMessage(sender, "§c§lPhantomBans v1.0");
                mi.sendMessage(sender, "§7Use §c/pb help §7for a list of commands");
            },
            "",
            "phantombans", "pb"
    );

    private final String permission;
    private final String argsRegex;
    private final TabCompleter tabCompleter;
    private final Consumer<CommandInput> executor;
    private final String usagePath;
    private final String[] names;

    Command(String permission, String argsRegex, TabCompleter tabCompleter,
            Consumer<CommandInput> executor, String usagePath, String... names) {
        this.permission = permission;
        this.argsRegex = argsRegex;
        this.tabCompleter = tabCompleter;
        this.executor = executor;
        this.usagePath = usagePath;
        this.names = names;
    }

    // ── Getters ──────────────────────────────────────────────────────── //
    public String getPermission() { return permission; }
    public String getArgsRegex() { return argsRegex; }
    public TabCompleter getTabCompleter() { return tabCompleter; }
    public String getUsagePath() { return usagePath; }
    public String[] getNames() { return names; }

    public boolean validateArguments(String[] args) {
        if (argsRegex == null || argsRegex.isEmpty()) return true;
        String joined = String.join(" ", args);
        return joined.matches(argsRegex);
    }

    public void execute(Object sender, String[] args) {
        executor.accept(new CommandInput(sender, args));
    }

    /**
     * Find a command by its alias/name.
     */
    public static Command getByName(String name) {
        for (Command cmd : values()) {
            for (String n : cmd.getNames()) {
                if (n.equalsIgnoreCase(name)) return cmd;
            }
        }
        return null;
    }

    // ── Command Input ────────────────────────────────────────────────── //
    /**
     * Wraps command sender and arguments for processing.
     */
    public static class CommandInput {
        private final Object sender;
        private final String[] args;
        private int index = 0;

        public CommandInput(Object sender, String[] args) {
            this.sender = sender;
            this.args = args;
        }

        public Object getSender() { return sender; }
        public String getPrimary() { return index < args.length ? args[index] : ""; }
        public String getPrimaryData() { return index < args.length ? args[index] : ""; }
        public boolean hasNext() { return index < args.length - 1; }

        public void next() {
            if (index < args.length) index++;
        }

        public String[] getArgs() {
            return Arrays.copyOfRange(args, index, args.length);
        }
    }
}
