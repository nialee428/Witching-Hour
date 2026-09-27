package net.nia.witchinghour.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentKey<PlayerMagicData> PLAYER_MAGIC =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier("witchinghour", "player_magic"),
                    PlayerMagicData.class
            );
    public static final ComponentKey<EntityMagicData> ENTITY_MAGIC =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier("witchinghour", "entity_magic"),
                    EntityMagicData.class
            );
}
