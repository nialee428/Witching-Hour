package net.nia.witchinghour.magic;

public class SpellListeningState {
    private static boolean listening = false;

    public static boolean isListening() {
        return listening;
    }

    public static void toggle() {
        listening = !listening;
    }

    public static void set(boolean value) {
        listening = value;
    }
}