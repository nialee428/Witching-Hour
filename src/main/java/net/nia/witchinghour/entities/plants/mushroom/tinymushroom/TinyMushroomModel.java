package net.nia.witchinghour.entities.plants.mushroom.tinymushroom;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class TinyMushroomModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart legs;
    private final ModelPart arms;
    private final ModelPart torso;
    private final ModelPart head;
    public TinyMushroomModel(ModelPart root) {
        this.legs = root.getChild("legs");
        this.arms = root.getChild("arms");
        this.torso = root.getChild("torso");
        this.head = root.getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData legs = modelPartData.addChild("legs", ModelPartBuilder.create().uv(24, 28).cuboid(-2.5F, -4.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 28).cuboid(0.5F, -4.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 25.0F, 0.0F));

        ModelPartData arms = modelPartData.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        ModelPartData cube_r1 = arms.addChild("cube_r1", ModelPartBuilder.create().uv(24, 28).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r2 = arms.addChild("cube_r2", ModelPartBuilder.create().uv(24, 28).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, -2.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create().uv(8, 22).cuboid(-3.0F, -5.5F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.0F, -0.25F, -0.0873F, 0.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(6, 4).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.0F, -0.75F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(9, 6).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.75F, 0.0F, -0.0023F, 0.0013F, -0.0872F));

        ModelPartData cube_r4 = head.addChild("cube_r4", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.0436F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        legs.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        arms.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}