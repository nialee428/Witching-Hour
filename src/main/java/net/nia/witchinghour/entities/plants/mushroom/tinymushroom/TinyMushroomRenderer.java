package net.nia.witchinghour.entities.plants.mushroom.tinymushroom;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.ModModelLayers;
import net.nia.witchinghour.WitchingHour;

public class TinyMushroomRenderer extends MobEntityRenderer<TinyMushroomEntity, TinyMushroomModel<TinyMushroomEntity>> {

    private static final Identifier TEXTURE =
            new Identifier(WitchingHour.MOD_ID, "textures/entity/tiny_mushroom.png");

    public TinyMushroomRenderer(EntityRendererFactory.Context context) {
        super(
                context,
                new TinyMushroomModel<>(
                        context.getPart(ModModelLayers.TINY_MUSHROOM)
                ),
                0.7f
        );
    }

    @Override
    public Identifier getTexture(TinyMushroomEntity entity) {
        return TEXTURE;
    }
}