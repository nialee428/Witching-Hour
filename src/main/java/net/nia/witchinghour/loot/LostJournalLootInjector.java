package net.nia.witchinghour.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;
import net.nia.witchinghour.ModItems;

import java.util.function.Supplier;

public class LostJournalLootInjector {

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

            if (id.equals(new Identifier("minecraft", "chests/ancient_city"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_BLACK, 0.1f*8f));
            }

            if (id.equals(new Identifier("minecraft", "chests/abandoned_mineshaft"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_PURPLE, 0.05f*8f));
            }

            if (id.equals(new Identifier("minecraft", "chests/nether_bridge"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_RED, 0.01f*8f));
            }

            if (id.getPath().startsWith("chests/bastion")) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_RED, 0.15f*8f));
            }

            if (id.equals(new Identifier("minecraft", "chests/simple_dungeon"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_GOLD, 0.04f*8f));
            }

            if (id.equals(new Identifier("minecraft", "chests/buried_treasure"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_BLUE, 1.0f));
            }

            if (id.equals(new Identifier("minecraft", "chests/igloo_chest"))) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_WHITE, 1.0f));
            }

            if (id.getPath().startsWith("chests/stronghold")) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_GREEN, 0.025f*8f));
            }

            if (id.getPath().contains("chests/village/")) {
                tableBuilder.pool(journalPool(() -> ModItems.LOST_JOURNAL_ORANGE, 1.0f));
            }

        });
    }

    /**
     * Creates a loot pool builder for a journal item.
     * Uses a Supplier to lazily fetch the item to avoid registry timing crashes.
     */
    private static LootPool.Builder journalPool(Supplier<Item> journalSupplier, float chance) {
        return LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .conditionally(RandomChanceLootCondition.builder(chance))
                .with(ItemEntry.builder(journalSupplier.get()));
    }
}