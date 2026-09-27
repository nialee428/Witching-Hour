package net.nia.witchinghour.magic.spells.other;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SpellBoltRenderer extends EntityRenderer<SpellBoltEntity> {
    public SpellBoltRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(SpellBoltEntity entity) {
        return null;
    }
}
