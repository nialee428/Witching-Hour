package net.nia.witchinghour.magic.spells.other;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellRecipe {

    public static void craft(GrimoireEntity book, ServerPlayerEntity player, ServerWorld world, Spell spell) {

        // 1. Collect items
        float pitch = 0.1f;
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

        Box area = new Box(
                book.getX() - 5, book.getY() - 5, book.getZ() - 5,
                book.getX() + 5, book.getY() + 5, book.getZ() + 5
        );

        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, area, e -> true);

        // 2. Build provided map
        Map<SpellIngredient, Integer> provided = new HashMap<>();
        for (ItemEntity entity : items) {
            ItemStack stack = entity.getStack();
            SpellIngredient ing = new SpellIngredient(stack.getItem(), stack.getNbt());
            provided.merge(ing, stack.getCount(), Integer::sum);
        }

        // 3. Check recipe
        if (!matchesRecipe(spell.recipe, provided) || data.getSpellLevel("General") < spell.data[1]) {

            world.playSound(
                    null,
                    book.getX(),
                    book.getY(),
                    book.getZ(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.NEUTRAL,
                    1,
                    pitch
            );

            return;
        }

        // 4. Consume items
        consumeIngredients(world, items, spell.recipe);

        // 5. Unlock spell
        data.unlockSpell(spell.ID);

        pitch = 1.1f;

        world.playSound(
                null,
                book.getX(),
                book.getY(),
                book.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.NEUTRAL,
                1,
                pitch
        );
    }


    private static boolean matchesRecipe(Map<SpellIngredient, Integer> recipe,
                                         Map<SpellIngredient, Integer> provided) {

        for (Map.Entry<SpellIngredient, Integer> entry : recipe.entrySet()) {
            SpellIngredient needed = entry.getKey();
            int requiredCount = entry.getValue();

            int providedCount = provided.entrySet().stream()
                    .filter(e -> ingredientsMatch(needed, e.getKey()))
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            if (providedCount < requiredCount) {
                return false;
            }
        }

        return true;
    }

    private static boolean ingredientsMatch(SpellIngredient needed, SpellIngredient provided) {
        if (needed.item() != provided.item()) return false;

        NbtCompound needNbt = needed.nbt();
        NbtCompound haveNbt = provided.nbt();

        // If the recipe does NOT require NBT, accept any NBT (including empty or null)
        if (needNbt == null || needNbt.isEmpty()) {
            return true;
        }

        // If the recipe requires NBT but the provided item has none → fail
        if (haveNbt == null || haveNbt.isEmpty()) {
            return false;
        }

        // Exact match required
        return needNbt.equals(haveNbt);
    }

    private static void consumeIngredients(ServerWorld world, List<ItemEntity> items,
                                           Map<SpellIngredient, Integer> recipe) {

        Map<SpellIngredient, Integer> toConsume = new HashMap<>(recipe);

        for (ItemEntity entity : items) {
            ItemStack stack = entity.getStack();
            SpellIngredient provided = new SpellIngredient(stack.getItem(), stack.getNbt());

            for (SpellIngredient needed : toConsume.keySet()) {
                if (!ingredientsMatch(needed, provided)) continue;

                int need = toConsume.get(needed);
                int have = stack.getCount();

                int take = Math.min(need, have);

                stack.decrement(take);
                need -= take;

                if (need <= 0) {
                    toConsume.put(needed, 0);
                } else {
                    toConsume.put(needed, need);
                }

                if (stack.isEmpty()) {
                    entity.discard();
                }
            }
        }
    }


}