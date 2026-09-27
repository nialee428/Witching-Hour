package net.nia.witchinghour.data;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.nia.witchinghour.events.TimeChecker;
import net.nia.witchinghour.magic.SpellListeningState;
import org.joml.Vector3f;

public class DataDisplay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(client.player);

        int mana = data.getMana();
        int level = data.getSpellLevel("General");
        int exp = data.getSpellExp("General");

        int maxMana = 5 * level;
        int expNeeded = 10 * level;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int xPadding = 5;
        int y = 5;
        int lineHeight = 12;

        int color = TimeChecker.getCurrentTimeColor(client);

        String[] lines = {
                "Mana: " + mana + "/" + maxMana,
                "Level: " + level,
                "EXP: " + exp + "/" + expNeeded
        };

        for (String line : lines) {
            int textWidth = client.textRenderer.getWidth(line);
            int x = screenWidth - textWidth - xPadding;

            context.drawTextWithShadow(
                    client.textRenderer,
                    line,
                    x,
                    y,
                    color
            );

            y += lineHeight;
        }

        // -------------------------
        // LISTENING INDICATOR
        // -------------------------
        if (SpellListeningState.isListening()) {

            Vector3f boltColor = data.getBoltColor("Bolt Color");

            int r = (int)(boltColor.x * 255);
            int g = (int)(boltColor.y * 255);
            int b = (int)(boltColor.z * 255);

            int listeningColor = (r << 16) | (g << 8) | b;

            String listeningText = "🎤 Listening for spell...";
            int textWidth = client.textRenderer.getWidth(listeningText);

            int lx = (screenWidth - textWidth) / 2;
            int ly = screenHeight - 50;

            context.drawTextWithShadow(
                    client.textRenderer,
                    listeningText,
                    lx,
                    ly,
                    listeningColor
            );
        }
    }
}