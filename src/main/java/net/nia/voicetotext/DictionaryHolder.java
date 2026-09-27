package net.nia.voicetotext;

import java.io.IOException;

public class DictionaryHolder {

    private static CMUDictionary dictionary;

    public static void initialize() {
        try {
            dictionary = new CMUDictionary();
            System.out.println("[RhymeEngine] CMU Dictionary loaded!");
        } catch (IOException e) {
            System.err.println("[RhymeEngine] Failed to load CMU Dictionary: " + e.getMessage());
        }
    }

    public static CMUDictionary get() {
        return dictionary;
    }

    public static boolean isLoaded() {
        return dictionary != null;
    }
}