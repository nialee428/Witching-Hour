package net.nia.witchinghour;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.Entity;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;

public class ModEntityComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                ModComponents.PLAYER_MAGIC,
                player -> new PlayerMagicData(player),
                RespawnCopyStrategy.ALWAYS_COPY
        );
        registry.registerFor(
                Entity.class,
                ModComponents.ENTITY_MAGIC,
                entity -> new EntityMagicData(entity)
        );
    }
}
