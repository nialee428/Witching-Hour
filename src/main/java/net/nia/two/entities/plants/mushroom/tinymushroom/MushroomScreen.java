package net.nia.witchinghour.entities.plants.mushroom.tinymushroom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MushroomScreen extends HandledScreen<MushroomScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(
                    "witching-hour",
                    "textures/gui/generic_single_chest.png"
            );

    public MushroomScreen(
            MushroomScreenHandler handler,
            PlayerInventory inventory,
            Text title
    ) {
        super(handler, inventory, title);

        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(
            DrawContext context,
            float delta,
            int mouseX,
            int mouseY
    ) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(
                TEXTURE,
                x,
                y,
                0,
                0,
                this.backgroundWidth,
                this.backgroundHeight,
                this.backgroundWidth,
                this.backgroundHeight
        );
    }
}