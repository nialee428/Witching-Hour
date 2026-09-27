package net.nia.voicetotext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.nia.voicetotext.speech.SpeechService;
import net.nia.witchinghour.magic.spells.create.SpellClient;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class SpeechCapture {

    private static volatile boolean running = false;

    public static void start() {
        if (running) return;
        running = true;

        // ensure model is loaded
        SpeechService.init();
        if (!SpeechService.isLoaded()) {
            System.err.println("[SpeechCapture] Vosk model not loaded, aborting start.");
            running = false;
            return;
        }

        Thread t = new Thread(SpeechCapture::captureLoop, "Vosk-Mic-Thread");
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(true);
        t.start();
    }

    public static void stop() {
        running = false;
    }

    private static void handleFinalResult(String json) {
        String finalText = "";

        if (json != null && !json.isEmpty()) {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("text")) {
                finalText = obj.get("text").getAsString();
            }
        }

        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.literal(finalText),
                false
        );

        System.out.println("[VTT] Final: '" + finalText + "'");
        SpellClient.updateLastResult(finalText);
        SpellClient.sendPacket(null);
    }

    private static void captureLoop() {
        try {
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
            TargetDataLine mic = AudioSystem.getTargetDataLine(format);
            mic.open(format);
            mic.start();

            byte[] buffer = new byte[(1024/64)/2];

            try (Recognizer recognizer = new Recognizer(SpeechService.getModel(), 16000.0f)) {

                while (running) {
                    int bytesRead = mic.read(buffer, 0, buffer.length);
                    if (bytesRead <= 0) continue;

                    boolean accepted = recognizer.acceptWaveForm(buffer, bytesRead);
                    if (accepted) {
                        break;
                    }
                }

                handleFinalResult(recognizer.getFinalResult());
            }

            mic.stop();
            mic.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}