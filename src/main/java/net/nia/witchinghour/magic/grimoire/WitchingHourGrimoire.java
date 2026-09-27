package net.nia.witchinghour.magic.grimoire;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WitchingHourGrimoire extends EntityModel<Entity> {
	private final ModelPart Book;
	private final ModelPart Pages;
	private final ModelPart Cover;
	public WitchingHourGrimoire(ModelPart root) {
		this.Book = root.getChild("Book");
		this.Pages = this.Book.getChild("Pages");
		this.Cover = this.Book.getChild("Cover");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData Book = modelPartData.addChild("Book", ModelPartBuilder.create(), ModelTransform.pivot(5.4F, 22.05F, 0.0F));

		ModelPartData Pages = Book.addChild("Pages", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData PageR_r1 = Pages.addChild("PageR_r1", ModelPartBuilder.create().uv(1, 26).cuboid(-4.025F, -1.0F, -5.0F, 7.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-7.775F, 0.775F, 0.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData PageL_r1 = Pages.addChild("PageL_r1", ModelPartBuilder.create().uv(36, 26).cuboid(-6.0F, -1.0F, -5.0F, 7.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData Cover = Book.addChild("Cover", ModelPartBuilder.create().uv(0, 115).cuboid(0.875F, -0.475F, -6.0F, 3.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.775F, 1.425F, 0.0F));

		ModelPartData CoverL_r1 = Cover.addChild("CoverL_r1", ModelPartBuilder.create().uv(0, 78).cuboid(-5.0F, -1.0F, -6.0F, 7.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(7.775F, -0.775F, 0.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData CoverR_r1 = Cover.addChild("CoverR_r1", ModelPartBuilder.create().uv(1, 91).cuboid(-5.025F, -1.0F, -6.0F, 7.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		Book.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}