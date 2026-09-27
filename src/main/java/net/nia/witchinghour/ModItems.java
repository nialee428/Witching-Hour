package net.nia.witchinghour;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.item.LostJournalItem;
import net.nia.witchinghour.magic.grimoire.GrimoireItem;

public class ModItems {

    public static Item LOST_JOURNAL_BLACK;
    public static Item LOST_JOURNAL_GREEN;
    public static Item LOST_JOURNAL_ORANGE;
    public static Item LOST_JOURNAL_RED;
    public static Item LOST_JOURNAL_BLUE;
    public static Item LOST_JOURNAL_PURPLE;
    public static Item LOST_JOURNAL_WHITE;
    public static Item LOST_JOURNAL_GOLD;

    public static Item GRIMOIRE;

    private static Item registerBook(String name) {
        return Registry.register(
                Registries.ITEM,
                new Identifier(WitchingHour.MOD_ID, name),
                new LostJournalItem(new Item.Settings().maxCount(1))
        );
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(
                Registries.ITEM,
                new Identifier(WitchingHour.MOD_ID, name),
                new BlockItem(block, new Item.Settings())
        );
    }

    // Call this once during onInitialize
    public static void registerAll() {
        LOST_JOURNAL_BLACK = registerBook("lost_journal_black");
        LOST_JOURNAL_GREEN = registerBook("lost_journal_green");
        LOST_JOURNAL_ORANGE = registerBook("lost_journal_orange");
        LOST_JOURNAL_RED = registerBook("lost_journal_red");
        LOST_JOURNAL_BLUE = registerBook("lost_journal_blue");
        LOST_JOURNAL_PURPLE = registerBook("lost_journal_purple");
        LOST_JOURNAL_WHITE = registerBook("lost_journal_white");
        LOST_JOURNAL_GOLD = registerBook("lost_journal_gold");

        GRIMOIRE = Registry.register(
                Registries.ITEM,
                new Identifier(WitchingHour.MOD_ID, "grimoire"),
                new GrimoireItem(new FabricItemSettings().maxCount(1))
        );
    }
}