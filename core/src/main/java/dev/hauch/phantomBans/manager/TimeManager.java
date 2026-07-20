package dev.hauch.phantomBans.manager;

import dev.hauch.phantomBans.Universal;

import java.util.Date;

/**
 * Centralized time manager.
 */
public class TimeManager {

    public static long getTime() {
        return new Date().getTime()
                + Universal.get().getMethods().getInteger(Universal.get().getMethods().getConfig(), "TimeDiff", 0) * 60 * 60 * 1000;
    }

    public static long toMilliSec(String s) {
        String[] sl = s.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        long i = Long.parseLong(sl[0]);
        return switch (sl[1]) {
            case "s" -> i * 1000;
            case "m" -> i * 1000 * 60;
            case "h" -> i * 1000 * 60 * 60;
            case "d" -> i * 1000 * 60 * 60 * 24;
            case "w" -> i * 1000 * 60 * 60 * 24 * 7;
            case "mo" -> i * 1000 * 60 * 60 * 24 * 30;
            default -> -1;
        };
    }
}
