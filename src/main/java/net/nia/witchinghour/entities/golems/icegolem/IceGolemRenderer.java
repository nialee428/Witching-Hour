package net.nia.witchinghour.entities.golems.icegolem;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.ModModelLayers;
import net.nia.witchinghour.WitchingHour;
import net.nia.witchinghour.entities.golems.GolemModel;

public class IceGolemRenderer extends MobEntityRenderer<IceGolemEntity, GolemModel<IceGolemEntity>> {

    private static final Identifier TEXTURE =
            new Identifier(WitchingHour.MOD_ID, "textures/entity/ice_golem.png");

    public IceGolemRenderer(EntityRendererFactory.Context context) {
        super(
                context,
                new GolemModel<>(
                        context.getPart(ModModelLayers.ICE_GOLEM)
                ),
                0.7f
        );
    }

    @Override
    public Identifier getTexture(IceGolemEntity entity) {
        return TEXTURE;
    }
}