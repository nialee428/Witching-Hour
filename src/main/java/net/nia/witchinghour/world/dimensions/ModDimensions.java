package net.nia.witchinghour.world.dimensions;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.nia.witchinghour.WitchingHour;
import net.nia.witchinghour.magic.rituals.tometrials.RitualPlateHandler;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> TOMBS_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            new Identifier(WitchingHour.MOD_ID, "tomb"));
    public static final RegistryKey<World> TOMBS_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(WitchingHour.MOD_ID, "tomb"));
    public static final RegistryKey<DimensionType> TOMBS_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(WitchingHour.MOD_ID, "tomb_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(TOMBS_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                true, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                DimensionTypes.OVERWORLD_ID, // effectsLocation
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));
    }

    public static void update(ServerPlayerEntity player) {
        RitualPlateHandler.onStep(player, player.getBlockPos(), player.getServerWorld());
    }
}