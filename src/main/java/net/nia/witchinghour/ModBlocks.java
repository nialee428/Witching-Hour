package net.nia.witchinghour;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    private static Block register(String name, Block block) {
        return Registry.register(
                Registries.BLOCK,
                new Identifier(WitchingHour.MOD_ID, name),
                block
        );
    }

    public static void registerAll() {
    }
}