package net.nia.voicetotext.speech;

import org.vosk.Model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SpeechService {

    private static Model model;
    private static boolean loaded = false;

    public static void init() {
        if (loaded) return;

        try {
            Path modelPath = Paths.get("resources/vosk-model-en-us-0.22");
            model = new Model(modelPath.toString());
            loaded = true;
            System.out.println("[SpeechService] Vosk model loaded.");
        } catch (Exception e) {
            loaded = false;
            System.err.println("[SpeechService] Failed to load Vosk model.");
            e.printStackTrace();
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static Model getModel() {
        return model;
    }
}