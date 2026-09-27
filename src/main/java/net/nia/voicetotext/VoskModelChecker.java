package net.nia.voicetotext;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.nia.voicetotext.speech.SpeechService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VoskModelChecker {

    public static File getModDirectory() {
        File modsFolder = new File(MinecraftClient.getInstance().runDirectory, "mods");

        if (modsFolder.exists() && modsFolder.isDirectory()) {
            File[] modFiles = modsFolder.listFiles((dir, name) ->
                    name.startsWith("witching-hour") && name.endsWith(".jar")
            );

            if (modFiles != null && modFiles.length > 0) {
                return modFiles[0].getParentFile();
            }
        }
        return null;
    }

    public static Path getModelPath() {
        File modDir = getModDirectory();
        if (modDir == null) return null;

        File resourcesFolder = new File(modDir.getParentFile(), "resources");
        return Paths.get(resourcesFolder.getAbsolutePath(), "vosk-model-en-us-0.22");
    }

    public static boolean modelExists() {
        Path modelPath = getModelPath();
        return modelPath != null && modelPath.toFile().exists();
    }

    public static void performCheck() {
        if (!modelExists()) {
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("[Voice to Text] Vosk model not found in /resources/. Speech recognition disabled.")
                                .formatted(Formatting.RED)
                );
            });
            return;
        }

        // Set native library path (same as Witching Hour)
        Path modelPath = getModelPath();
        Path nativePath = modelPath.resolve("win32-x86-64");

        System.setProperty("jna.library.path", nativePath.toString());
        System.setProperty("jna.boot.library.path", nativePath.toString());

        SpeechService.init();
    }
}