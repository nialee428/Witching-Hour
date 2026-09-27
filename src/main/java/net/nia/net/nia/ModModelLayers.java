package net.nia.witchinghour;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.entities.golems.GolemModel;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.TinyMushroomModel;
import net.nia.witchinghour.magic.grimoire.WitchingHourGrimoire;

public class ModModelLayers {

    public static final EntityModelLayer GRIMOIRE =
            new EntityModelLayer(new Identifier("witching-hour", "grimoire"), "main");

    public static final EntityModelLayer ICE_GOLEM =
            new EntityModelLayer(
                    new Identifier(WitchingHour.MOD_ID, "ice_golem"),
                    "main"
            );

    public static final EntityModelLayer TINY_MUSHROOM =
            new EntityModelLayer(
                    new Identifier(WitchingHour.MOD_ID, "tiny_mushroom"),
                    "main"
            );

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(GRIMOIRE, WitchingHourGrimoire::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(ICE_GOLEM, GolemModel::getTexturedModelData);

        EntityModelLayerRegistry.registerModelLayer(TINY_MUSHROOM, TinyMushroomModel::getTexturedModelData);
    }
}