package net.nia.witchinghour.magic;

import net.minecraft.client.MinecraftClient;
import net.nia.voicetotext.SpeechCapture;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.input.InputMain;
import net.nia.witchinghour.magic.spells.create.SpellClient;
import net.nia.witchinghour.magic.spells.purplemagic.Telekinesis;
import net.nia.witchinghour.magic.spells.purplemagic.TelekinesisControl;

public class GestureHandler {

    private static boolean wasDown = false;
    private static boolean allowCast = false;
    private static boolean allowSnap = false;

    public static void update(MinecraftClient client) {
        if (client.player == null) return;
        boolean isCastingTelekinesis = Telekinesis.getTargets(client.player) != null &&
                !Telekinesis.getTargets(client.player).isEmpty();

        if (client.mouse.wasLeftButtonClicked()) {
            WitchingHourNetworking.sendTelekinesisControlPacket(TelekinesisControl.PUSH);

            if (client.player.isSneaking()) {
                WitchingHourNetworking.sendTelekinesisControlPacket(TelekinesisControl.RELEASE);
            }
        }

        // Detect right click press
        if (client.mouse.wasRightButtonClicked()) {
            WitchingHourNetworking.sendTelekinesisControlPacket(TelekinesisControl.PULL);

            if (client.player.isSneaking()) {
                WitchingHourNetworking.sendTelekinesisControlPacket(TelekinesisControl.PLACE);
            }
        }

        boolean isDown = InputMain.GESTURE_KEY.isPressed();
        boolean doCast = InputMain.CAST_KEY.isPressed();
        boolean doSnap = InputMain.SNAP_KEY.isPressed();

        if (isDown && !wasDown) {
            onPress();
        }

        if (!isDown && wasDown) {
            onRelease();
        }

        if (doCast && !allowCast) {
            SpellClient.sendPacket(null);
        }

        if (doSnap && !allowSnap) {
            WitchingHourNetworking.sendSnapPacket();
        }

        wasDown = isDown;
        allowCast = doCast;
        allowSnap = doSnap;

    }

    private static void onPress() {
        SpeechCapture.start();
        SpellListeningState.set(true);
    }

    private static void onRelease() {
        SpeechCapture.stop();
        SpellListeningState.set(false);
    }

}
