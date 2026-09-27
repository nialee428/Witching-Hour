package net.nia.witchinghour.entities.golems;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GolemModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	public GolemModel(ModelPart root) {
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.right_arm = root.getChild("right_arm");
		this.left_arm = root.getChild("left_arm");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 40).cuboid(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, new Dilation(0.0F))
		.uv(0, 70).cuboid(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.0F, -2.0F));

		ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(60, 21).cuboid(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

		ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(60, 58).cuboid(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(37, 0).cuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 11.0F, 0.0F));

		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(60, 0).cuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 11.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

    @Override
    public void setAngles(
            T entity,
            float limbAngle,
            float limbDistance,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        // Head follows where the golem is looking
        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;

        // Walking animation
        this.right_arm.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI)
                * 0.5F * limbDistance;

        this.left_arm.pitch = MathHelper.cos(limbAngle * 0.6662F)
                * 0.5F * limbDistance;

        this.right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F)
                * 1.4F * limbDistance;

        this.left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI)
                * 1.4F * limbDistance;
    }
}