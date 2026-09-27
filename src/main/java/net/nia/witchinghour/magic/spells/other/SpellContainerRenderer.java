package net.nia.witchinghour.magic.spells.other;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SpellContainerRenderer extends EntityRenderer<SpellContainer> {
    public SpellContainerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(SpellContainer entity) {
        return null;
    }
}
