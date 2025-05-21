package toutouchien.quickhome.utils;

import org.jetbrains.annotations.NotNull;

public class PlayerUtils {
    private PlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidPlayerName(@NotNull String playerName) {
        return playerName.length() <= 16 && playerName.chars().filter(i -> i <= 32 || i >= 127).findAny().isEmpty();
    }
}
