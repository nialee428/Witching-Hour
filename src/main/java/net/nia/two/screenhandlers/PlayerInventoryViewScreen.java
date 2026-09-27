package net.nia.witchinghour.screenhandlers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerInventoryViewScreen extends HandledScreen<PlayerInventoryViewScreenHandler> {

    private static final Identifier PANEL_TEXTURE =
            new Identifier("witching-hour", "textures/gui/inventory_panel.png");

    private static final int PANEL_HEIGHT = 96; // 4 rows chest-style
    private static final int PANEL_WIDTH = 176;

    public PlayerInventoryViewScreen(PlayerInventoryViewScreenHandler handler,
                                     PlayerInventory inventory,
                                     Text title) {
        super(handler, inventory, title);

        this.backgroundWidth = PANEL_WIDTH;
        this.backgroundHeight = PANEL_HEIGHT;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

        int topY = this.y;
        int bottomY = this.y + PANEL_HEIGHT + 10;

        // Draw top panel
        context.drawTexture(PANEL_TEXTURE, this.x, topY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Draw bottom panel
        context.drawTexture(PANEL_TEXTURE, this.x, bottomY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, Text.literal("Target Inventory"), 8, 6, 0x404040, false);
        context.drawText(this.textRenderer, Text.literal("Your Inventory"), 8, PANEL_HEIGHT + 16, 0x404040, false);
    }
}