<div align="center">

  <!-- Banner -->
  <img src="https://img.shields.io/badge/PhantomBans-1.0.0-red?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2NCIgaGVpZ2h0PSI2NCIgdmlld0JveD0iMCAwIDY0IDY0Ij48cGF0aCBmaWxsPSIjZmZmIiBkPSJNMzIgMkMxNS40IDIgMiAxNS40IDIgMzJzMTMuNCAzMCAzMCAzMCAzMC0xMy40IDMwLTMwUzQ4LjYgMiAzMiAyem0wIDU2Yy0xNC40IDAtMjYtMTEuNi0yNi0yNlMxNy42IDYgMzIgNnMyNiAxMS42IDI2IDI2LTExLjYgMjYtMjYgMjZ6bS02LTI2bC02IDYgNiA2IDItMi00LTQgNC00LTItMnptMTIgMGwyIDIgNCA0LTQgNC0yIDIgNi02LTYtNnoiLz48L3N2Zz4=" />

  <h1>PhantomBans</h1>

  <p>
    <strong>A modern, multi-platform punishment system for Minecraft servers.</strong>
  </p>

  <p>
    <a href="https://github.com/Hauchdev/PhantomBans/actions"><img src="https://img.shields.io/github/actions/workflow/status/Hauchdev/PhantomBans/ci.yml?branch=master&style=flat-square&logo=github" alt="CI Status"/></a>
    <a href="https://github.com/Hauchdev/PhantomBans/releases"><img src="https://img.shields.io/github/v/release/Hauchdev/PhantomBans?style=flat-square&logo=github" alt="Release"/></a>
    <a href="https://github.com/Hauchdev/PhantomBans/blob/master/LICENSE"><img src="https://img.shields.io/github/license/Hauchdev/PhantomBans?style=flat-square" alt="License"/></a>
    <a href="https://github.com/Hauchdev/PhantomBans/issues"><img src="https://img.shields.io/github/issues/Hauchdev/PhantomBans?style=flat-square" alt="Issues"/></a>
    <img src="https://img.shields.io/badge/Java-17%2B-red?style=flat-square&logo=openjdk" alt="Java 17+"/>
    <img src="https://img.shields.io/badge/Minecraft-1.18%2B-blue?style=flat-square&logo=minecraft" alt="Minecraft 1.18+"/>
  </p>

  <hr/>

  <p>
    <b>Bukkit</b> • <b>Spigot</b> • <b>Paper</b> • <b>BungeeCord</b> • <b>Waterfall</b> • <b>Velocity</b>
  </p>

  <hr/>

</div>

---

## 🌟 Overview

**PhantomBans** is a powerful, cross-platform punishment system designed for Minecraft networks. Whether you run a single Bukkit server or a large multi-proxy network with BungeeCord or Velocity, PhantomBans provides a unified, feature-rich moderation experience.

Inspired by the legendary AdvancedBan, PhantomBans is built from the ground up with **Java 17**, **MiniMessage formatting**, and a modern codebase that is easy to extend and maintain.

---

## ✨ Features

### 🛡️ Punishment Types

| Type | Permanent | Temporary | Description |
|------|-----------|-----------|-------------|
| **Ban** | ✅ `/ban` | ✅ `/tempban` | Prevents a player from joining |
| **IP-Ban** | ✅ `/ipban` | ✅ `/tempipban` | Bans by IP address |
| **Mute** | ✅ `/mute` | ✅ `/tempmute` | Prevents a player from chatting |
| **Warn** | ✅ `/warn` | ✅ `/tempwarn` | Issues a warning (with configurable actions) |
| **Kick** | ✅ `/kick` | ❌ | Disconnects a player |
| **Note** | ✅ `/note` | ❌ | Adds a private staff note |

### 🎨 Modern Formatting

- **MiniMessage** — Use `<red>`, `<bold>`, `<gradient:red:gold>` tags instead of legacy `&` codes
- **Unicode borders** — Clean separators like `━━━` and `┃` for readable output
- **Fully customizable** — Every message, layout, and notification is editable in YAML

### 🔧 Powerful Commands

- `change-reason <ID> <new reason>` — Update a punishment reason retroactively
- `unpunish <ID>` — Remove any punishment by its ID
- `history <player>` — View complete punishment history with pagination
- `check <player>` — Real-time punishment status overview
- `banlist` — List all active bans

### 🧠 Smart Features

- **Warn actions** — Automatically execute commands when a player accumulates warnings
- **Layout system** — Use `@LayoutName` for custom punishment messages
- **Time calculation** — Progressive durations with `#LayoutName` (e.g., 1st offense: 7d, 2nd: 30d)
- **Silent punishments** — Prefix commands with `-s` for silent execution
- **Exempt players** — Protect certain players from being punished

### 🌐 Cross-Platform

- **Single-server** — Works on Bukkit/Spigot/Paper out of the box
- **Proxy networks** — Share punishments across BungeeCord, Waterfall, or Velocity
- **Shared database** — MySQL for centralized data; HSQLDB for local storage
- **Auto-discovery** — Detects your platform and configures itself

---

## 📦 Installation

### Prerequisites

- **Java 17** or higher
- **Minecraft server** running one of the supported platforms:
  - Paper 1.18+ (recommended)
  - Spigot 1.18+
  - BungeeCord / Waterfall
  - Velocity 3.3+

### Steps

1. **Download** the latest JAR from the [Releases page](https://github.com/Hauchdev/PhantomBans/releases/latest)

   | Platform | File |
   |----------|------|
   | Bukkit / Paper / Spigot | `PhantomBans-Bukkit-x.x.x.jar` |
   | BungeeCord / Waterfall | `PhantomBans-Bungee-x.x.x.jar` |
   | Velocity | `PhantomBans-Velocity-x.x.x.jar` |
   | **All-in-one** | `PhantomBans-Bundle-x.x.x.jar` |

2. **Place** the JAR in your server's `plugins/` folder

3. **Restart** your server (or use `/reload confirm` on Paper)

4. **Configure** the plugin by editing the generated files:
   - `plugins/PhantomBans/config.yml` — Main settings
   - `plugins/PhantomBans/Messages.yml` — All messages (MiniMessage format)
   - `plugins/PhantomBans/Layouts.yml` — Custom punishment layouts
   - `plugins/PhantomBans/MySQL.yml` — Database credentials (if using MySQL)

---

## 🎮 Commands

### Punishment Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/ban <player> [reason]` | `pb.ban.perma` | Permanently ban a player |
| `/tempban <player> <duration> [reason]` | `pb.ban.temp` | Temporarily ban a player |
| `/ipban <player/IP> [reason]` | `pb.ipban.perma` | IP-ban a player |
| `/tempipban <player/IP> <duration> [reason]` | `pb.ipban.temp` | Temporarily IP-ban a player |
| `/mute <player> [reason]` | `pb.mute.perma` | Mute a player |
| `/tempmute <player> <duration> [reason]` | `pb.mute.temp` | Temporarily mute a player |
| `/warn <player> [reason]` | `pb.warn.perma` | Warn a player |
| `/tempwarn <player> <duration> [reason]` | `pb.warn.temp` | Temporarily warn a player |
| `/kick <player> [reason]` | `pb.kick.use` | Kick a player |
| `/note <player> <note>` | `pb.note.use` | Add a note to a player |

### Revocation Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/unban <player>` | `pb.ban.undo` | Unban a player |
| `/unmute <player>` | `pb.mute.undo` | Unmute a player |
| `/unwarn <ID>` | `pb.warn.undo` | Remove a warning by ID |
| `/unwarn clear <player>` | `pb.warn.undo` | Clear all warnings for a player |
| `/unnote <ID>` | `pb.note.undo` | Remove a note by ID |
| `/unnote clear <player>` | `pb.note.undo` | Clear all notes for a player |
| `/unpunish <ID>` | `pb.all.undo` | Remove any punishment by ID |

### Information Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/check <player>` | `pb.check` | View a player's punishment status |
| `/history <player> [page]` | `pb.history` | View punishment history |
| `/banlist [page]` | `pb.banlist` | List all active bans |
| `/warns [player] [page]` | `pb.warns.own` / `pb.warns.other` | View warnings |
| `/notes [player] [page]` | `pb.notes.own` / `pb.notes.other` | View notes |

### Utility Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/change-reason <ID> <new reason>` | `pb.changeReason` | Update a punishment's reason |
| `/pb help` | `pb.help` | Show command help |
| `/pb reload` | `pb.reload` | Reload all configuration files |

> **Tip:** Prefix any punishment command with `-s` to execute it silently (no broadcast).  
> Example: `/ban -s Notch Hacked client`

### Duration Format

| Suffix | Unit | Example |
|--------|------|---------|
| `s` | Seconds | `30s` |
| `m` | Minutes | `15m` |
| `h` | Hours | `2h` |
| `d` | Days | `7d` |
| `w` | Weeks | `2w` |
| `mo` | Months | `1mo` |

---

## 🔐 Permissions

### Punishment Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `pb.ban.perma` | op | Permanent ban |
| `pb.ban.temp` | op | Temporary ban |
| `pb.ipban.perma` | op | Permanent IP ban |
| `pb.ipban.temp` | op | Temporary IP ban |
| `pb.mute.perma` | op | Permanent mute |
| `pb.mute.temp` | op | Temporary mute |
| `pb.warn.perma` | op | Permanent warn |
| `pb.warn.temp` | op | Temporary warn |
| `pb.kick.use` | op | Kick |
| `pb.note.use` | op | Add notes |

### Moderation Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `pb.ban.undo` | op | Unban |
| `pb.mute.undo` | op | Unmute |
| `pb.warn.undo` | op | Remove warnings |
| `pb.note.undo` | op | Remove notes |
| `pb.all.undo` | op | Remove any punishment |
| `pb.changeReason` | op | Change punishment reason |

### Information Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `pb.check` | op | Check player status |
| `pb.check.ip` | op | View IP addresses in /check |
| `pb.history` | op | View punishment history |
| `pb.banlist` | op | List active bans |
| `pb.warns.own` | true | View own warnings |
| `pb.warns.other` | op | View others' warnings |
| `pb.notes.own` | true | View own notes |
| `pb.notes.other` | op | View others' notes |

### Admin Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `pb.help` | op | View help |
| `pb.reload` | op | Reload configuration |

### Notification Permissions

| Permission | Description |
|-----------|-------------|
| `pb.notify.Ban` | Receive ban notifications |
| `pb.notify.Tempban` | Receive temp-ban notifications |
| `pb.notify.Mute` | Receive mute notifications |
| `pb.notify.Kick` | Receive kick notifications |
| `pb.notify.Warn` | Receive warn notifications |
| `pb.notify.Note` | Receive note notifications |
| `pb.undoNotify.Ban` | Receive unban notifications |
| `pb.undoNotify.Mute` | Receive unmute notifications |

> **💡 Tip:** Enable `EnableAllPermissionNodes: true` in `config.yml` so players with `pb.ban.all` can use all ban sub-commands.

---

## ⚙️ Configuration

### config.yml

```yaml
# Time settings
TimeDiff: 0                          # Server timezone offset (hours)
DateFormat: "dd.MM.yyyy-HH:mm"       # Date format (Java SimpleDateFormat)

# Database
UseMySQL: false                      # false = HSQLDB (local), true = MySQL
LockdownOnError: true                # Kick players if DB fails to load

# Update checker
UpdateChecker:
  Repository: "Hauchdev/PhantomBans" # GitHub repo for version checks

# Messages
Disable Prefix: false                # Remove plugin prefix from messages
DefaultReason: "Misconduct"          # Default punishment reason

# Permissions
EnableAllPermissionNodes: false       # Allow wildcard permissions (e.g., pb.ban.all)
ExemptPlayers: []                     # Players immune to punishments

# Warn actions (executed when warnings accumulate)
WarnActions:
  1: "msg %PLAYER% &cYou have received your first warning!"
  3: "kick %PLAYER% &cKicked for 3 warnings."
  5: "tempban %PLAYER% 7d &cAuto-banned for 5 warnings."
```

### Messages.yml

All messages use **[MiniMessage format](https://docs.advntr.dev/minimessage/format.html)**:

```yaml
General:
  Prefix: "<dark_gray>[<red>PhantomBans<dark_gray>]"
  NoPerms: "<red>You don't have permission to use this command!"

Ban:
  Done: "<red><italic>%NAME% <gray>was successfully banned!"
  Notification:
    - "<dark_gray>[<red>!<dark_gray>] <red><italic>%NAME% <gray>was banned by <yellow><italic>%OPERATOR%"
  Layout:
    - "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    - "<dark_gray>┃ <red><bold>You have been banned!"
    - "<dark_gray>┃ <gray>Reason: <white>%REASON%"
    - "<dark_gray>┃ <gray>ID: <white>#%HEXID%"
    - "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
```

---

## 📚 API

PhantomBans exposes a public API for other plugins to integrate with.

### Gradle / Maven

```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.Hauchdev</groupId>
  <artifactId>PhantomBans</artifactId>
  <version>Tag</version>
  <scope>provided</scope>
</dependency>
```

### Usage

```java
import dev.hauch.phantomBans.api.PhantomBansAPI;
import dev.hauch.phantomBans.api.PunishmentListener;
import dev.hauch.phantomBans.api.PunishEvent;
import dev.hauch.phantomBans.utils.Punishment;

// ── Listen for punishment events ───────────────────────────────────────
PhantomBansAPI.addListener(new PunishmentListener() {
    @Override
    public void onPunish(PunishEvent event) {
        Punishment p = event.getPunishment();
        Bukkit.broadcastMessage(p.getName() + " was " + p.getType().getName() + "ed!");
    }
});

// ── Check player status ────────────────────────────────────────────────
boolean banned = PhantomBansAPI.isBanned(playerUuid);
boolean muted  = PhantomBansAPI.isMuted(playerUuid);

// ── Get active punishments ────────────────────────────────────────────
Punishment ban  = PhantomBansAPI.getActiveBan(playerUuid);
Punishment mute = PhantomBansAPI.getActiveMute(playerUuid);

// ── Issue punishments programmatically ─────────────────────────────────
PhantomBansAPI.banPlayer("Notch", "Console", "Griefing");
PhantomBansAPI.tempMutePlayer("jeb_", "Console", "Spam", 3600000); // 1 hour
PhantomBansAPI.warnPlayer("Dinnerbone", "Console", "First warning");
```

---

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/Hauchdev/PhantomBans.git
cd Phantom-Bans

# Compile and package
mvn clean package -DskipTests

# Run tests
mvn test -DskipTests=false

# The compiled JARs will be in:
#   bukkit/target/PhantomBans-Bukkit-*.jar
#   bungee/target/PhantomBans-Bungee-*.jar
#   velocity/target/PhantomBans-Velocity-*.jar
#   bundle/target/PhantomBans-Bundle-*.jar
```

### Project Structure

```
Phantom-Bans/
├── core/          # Platform-independent shared logic
├── bukkit/        # Bukkit/Spigot/Paper implementation
├── bungee/        # BungeeCord/Waterfall implementation
├── velocity/      # Velocity implementation
├── bundle/        # All-in-one shaded JAR
└── pom.xml        # Parent Maven POM
```

---

## 🧪 Testing

```bash
mvn test -DskipTests=false
```

The project uses JUnit 5 for unit testing. Tests are located in:

- `core/src/test/java/` — Core module tests (TimeManager, CommandUtils, Punishment, etc.)

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **🐛 Report bugs** — Open an [issue](https://github.com/Hauchdev/PhantomBans/issues) with detailed reproduction steps
2. **💡 Suggest features** — Open an [issue](https://github.com/Hauchdev/PhantomBans/issues) with your idea
3. **🔀 Submit PRs** — Fork the repo, make your changes, and open a pull request

### Development Guidelines

- Use **Java 17** features where appropriate
- Follow the existing code style and conventions
- Keep the **MethodInterface** pattern for cross-platform compatibility
- Write unit tests for new functionality
- Use **MiniMessage** for all user-facing messages

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <p>
    <strong>PhantomBans</strong> — Made with ❤️ by <a href="https://github.com/Hauchdev">Hauchdev</a>
  </p>
  <p>
    <a href="https://github.com/Hauchdev/PhantomBans/issues">Report Bug</a> •
    <a href="https://github.com/Hauchdev/PhantomBans/issues">Request Feature</a> •
    <a href="https://github.com/Hauchdev/PhantomBans/discussions">Discussions</a>
  </p>
  <br/>
  <p>
    <img src="https://img.shields.io/github/stars/Hauchdev/PhantomBans?style=social" alt="Stars"/>
    <img src="https://img.shields.io/github/forks/Hauchdev/PhantomBans?style=social" alt="Forks"/>
  </p>
</div>
