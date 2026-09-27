package net.nia.witchinghour.magic.grimoire;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.nia.witchinghour.ModModelLayers;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.grimoire.info.GrimoireDisplayState;

import java.util.ArrayList;
import java.util.List;

public class GrimoireEntityRenderer extends EntityRenderer<GrimoireEntity> {

    private final WitchingHourGrimoire model;
    private final TextRenderer textRenderer;

    public GrimoireEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new WitchingHourGrimoire(ctx.getPart(ModModelLayers.GRIMOIRE));
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public Identifier getTexture(GrimoireEntity entity) {
        return new Identifier("witching-hour", "textures/entity/grimoire.png");
    }

    public static List<Text> wrapText(TextRenderer renderer, Text text, int maxWidth) {
        List<Text> lines = new ArrayList<>();

        // First split on explicit newlines
        String[] rawLines = text.getString().split("\n");

        for (String raw : rawLines) {
            String[] words = raw.split(" ");

            StringBuilder current = new StringBuilder();

            for (String word : words) {
                String test = current + word + " ";
                if (renderer.getWidth(test) > maxWidth) {
                    lines.add(Text.literal(current.toString()));
                    current = new StringBuilder(word + " ");
                } else {
                    current.append(word).append(" ");
                }
            }

            if (!current.isEmpty()) {
                lines.add(Text.literal(current.toString()));
            }
        }

        return lines;
    }

    @Override
    public void render(GrimoireEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();

        double hover = Math.sin((entity.age + tickDelta) * 0.05) * 0.1;
        matrices.translate(0, hover + 1.9, 0);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));

        model.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity))),
                light,
                OverlayTexture.DEFAULT_UV,
                1f, 1f, 1f, 1f
        );

        matrices.pop();

        // -----------------------------
        // ✦ DYNAMIC DISPLAY SYSTEM ✦
        // -----------------------------
        matrices.push();

        matrices.translate(0, hover + 2.4, 0);
        assert MinecraftClient.getInstance().cameraEntity != null;
        float yaw2 = MinecraftClient.getInstance().cameraEntity.getYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw2));

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        GrimoireDisplayState state = GrimoireClientData.get(entity);

        // Title
        Text title = state.getTitle();
        int titleHeight = 12; // one line

        // Wrapped page text
        int maxWidth = 120 * 2;
        List<Text> wrapped = wrapText(textRenderer, state.getCurrentPage(), maxWidth);

        int lineHeight = 12;
        int pageHeight = wrapped.size() * lineHeight;

        // Total block height (title + spacing + page)
        int totalHeight = titleHeight + 4 + pageHeight;

        // Start so the whole block is centered above the book
        int startY = -(totalHeight / 2);

        // Draw title
        float titleX = -textRenderer.getWidth(title) / 2f;
        textRenderer.draw(
                title,
                titleX,
                startY,
                0xAA55FF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        // Draw page text under title
        int lineY = startY + titleHeight + 4;

        for (Text line : wrapped) {
            float offset = -textRenderer.getWidth(line) / 2f;

            textRenderer.draw(
                    line,
                    offset,
                    lineY,
                    0xDDDDDD,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );

            lineY += lineHeight;
        }

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}