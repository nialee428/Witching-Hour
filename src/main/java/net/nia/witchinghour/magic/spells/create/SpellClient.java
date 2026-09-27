package net.nia.witchinghour.magic.spells.create;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.input.InputMain;
import net.nia.witchinghour.magic.grimoire.info.GrimoireCommands;
import net.nia.witchinghour.magic.spells.other.SpellTranslator;

public class SpellClient {

    public static String lastResult;

    public static void updateLastResult(String result) {
        lastResult = result;
    }

    public static void sendPacket(String result) {

        if ((result == null || result.isBlank()) && lastResult != null) {
            result = lastResult;
        } else if (result == null || result.isBlank() && lastResult == null) {
            return;
        }


        if (result.isEmpty()) {
            System.out.println("[WitchingHour] Initial result returned empty, skipping packet.");
            return;
        }

        result = result.toLowerCase();

        GrimoireCommands.translateCommand(result);
        result = SpellTranslator.translateSpell(result);
        if (result.isEmpty()) {
            System.out.println("[WitchingHour] Translator returned empty, skipping packet.");
            return;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(result);
        buf.writeInt(5);

        Vec3d dir = MinecraftClient.getInstance().player.getRotationVec(1.0f);
        buf.writeDouble(dir.x);
        buf.writeDouble(dir.y);
        buf.writeDouble(dir.z);

        ClientPlayNetworking.send(WitchingHourNetworking.CAST_SPELL_PACKET, buf);
    }

}
