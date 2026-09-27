package net.nia.witchinghour;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup WITCHING_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(WitchingHour.MOD_ID, "witchinghour_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.witchinghour_group"))
                    .icon(() -> new ItemStack(ModItems.LOST_JOURNAL_BLACK))
                    .entries((context, entries) -> {
                        entries.add(ModItems.LOST_JOURNAL_BLACK);
                        entries.add(ModItems.LOST_JOURNAL_GREEN);
                        entries.add(ModItems.LOST_JOURNAL_ORANGE);
                        entries.add(ModItems.LOST_JOURNAL_RED);
                        entries.add(ModItems.LOST_JOURNAL_BLUE);
                        entries.add(ModItems.LOST_JOURNAL_PURPLE);
                        entries.add(ModItems.LOST_JOURNAL_WHITE);
                        entries.add(ModItems.LOST_JOURNAL_GOLD);

                        entries.add(ModItems.GRIMOIRE);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        // This method exists just for symmetry with ModItems.registerAll()
    }
}