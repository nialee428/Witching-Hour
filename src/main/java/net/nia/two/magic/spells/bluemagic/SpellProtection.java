package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellProtection {

    public static final Spell SPELL = new Spell();
    public static final String ID = "SpellProtection";

    public static void protect(SpellBoltEntity bolt, String spell, HitResult hit, String CurseID) {

        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
        if (player == null) {
            return;
        }

        List<Spell> casted = new ArrayList<>();
        casted.add(SPELL);
        if (ManaHelper.consume(bolt, casted, false, 0)) {
            return;
        }

        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
            bolt.discard();
            return;
        }

        if (hit instanceof BlockHitResult h) {

            ItemStack heldItem = player.getMainHandStack();

            if (heldItem != null) {
                NbtCompound nbt = heldItem.getOrCreateNbt();
                if (!nbt.getString("witchinghour:protection").isEmpty()) {
                    return;
                }

                nbt.putString("witchinghour:protection", CurseID);
            }

        } else if (hit instanceof EntityHitResult h) {
            PlayerEntity target = null;

            if (h.getEntity() instanceof PlayerEntity p) {
                target = p;
            }

            if (target == null) {
                return;
            }

            ItemStack heldItem = target.getMainHandStack();

            if (heldItem != null) {
                NbtCompound nbt = heldItem.getOrCreateNbt();
                if (!nbt.getString("witchinghour:protection").isEmpty()) {
                    return;
                }

                nbt.putString("witchinghour:protection", CurseID);
            }

        }

        bolt.getWorld().playSound(
                null,
                hit.getPos().getX(),
                hit.getPos().getY(),
                hit.getPos().getZ(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.AMBIENT,
                1.0f,
                1.0f
        );

        bolt.discard();

    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound enchants = new NbtCompound();
        enchants.putInt("minecraft:protection", 4);

        NbtCompound nbt = new NbtCompound();
        nbt.put("minecraft:stored_enchantments", enchants);

        RECIPE.put(new SpellIngredient(Items.SHIELD, null), 1);
        RECIPE.put(new SpellIngredient(Items.GRINDSTONE, null), 1);

        RECIPE.put(new SpellIngredient(Items.IRON_BOOTS, null), 1);
        RECIPE.put(new SpellIngredient(Items.IRON_LEGGINGS, null), 1);
        RECIPE.put(new SpellIngredient(Items.IRON_CHESTPLATE, null), 1);
        RECIPE.put(new SpellIngredient(Items.IRON_HELMET, null), 1);

        RECIPE.put(new SpellIngredient(Items.ENCHANTED_BOOK, nbt), 4);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "protect", "protected", "protection", "protecting", "protects"
                },

                new double[] {
                        30.0, 30.0, 45.0, 1.0
                },

                SpellType.BLUE_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {
                        bolt.discard();
                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {
                        bolt.discard();
                    }
                },

                RECIPE
        );

    }

}
