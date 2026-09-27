package net.nia.witchinghour;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.entities.golems.icegolem.IceGolemEntity;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.TinyMushroomEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.other.SpellBoltEntity;
import net.nia.witchinghour.magic.spells.other.SpellContainer;

public class WitchingHourEntities {

    public static final EntityType<SpellBoltEntity> SPELL_BOLT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(WitchingHour.MOD_ID, "spell_bolt"),
            FabricEntityTypeBuilder.<SpellBoltEntity>create(SpawnGroup.MISC, SpellBoltEntity::new)
                    .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
                    .trackRangeBlocks(1000)
                    .trackedUpdateRate(2)
                    .build()
    );

    public static final EntityType<SpellContainer> SPELL_CONTAINER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(WitchingHour.MOD_ID, "spell_container"),
            FabricEntityTypeBuilder.<SpellContainer>create(SpawnGroup.MISC, SpellContainer::new)
                    .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
                    .trackRangeBlocks(1000)
                    .trackedUpdateRate(2)
                    .build()
    );


    public static final EntityType<IceGolemEntity> ICE_GOLEM =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(WitchingHour.MOD_ID, "ice_golem"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, IceGolemEntity::new)
                            .dimensions(EntityDimensions.fixed(1.4f, 2.7f))
                            .trackRangeBlocks(10)
                            .trackedUpdateRate(3)
                            .build()
            );


    public static final EntityType<TinyMushroomEntity> TINY_MUSHROOM =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(WitchingHour.MOD_ID, "tiny_mushroom"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, TinyMushroomEntity::new)
                            .dimensions(EntityDimensions.fixed(.5f, .65f))
                            .trackRangeBlocks(10)
                            .trackedUpdateRate(3)
                            .build()
            );

    public static final EntityType<GrimoireEntity> GRIMOIRE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(WitchingHour.MOD_ID, "grimoire"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GrimoireEntity::new)
                            .dimensions(EntityDimensions.fixed(1f, 1f))
                            .fireImmune()
                            .build()
            );


    public static void init() {}
}