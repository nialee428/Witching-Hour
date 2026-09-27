package net.nia.witchinghour.magic.spells.whitemagic.charms;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cleanse {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Cleanse";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound weaknessNbt = new NbtCompound();
        weaknessNbt.putString("Potion", "minecraft:weakness");

        NbtCompound slownessNbt = new NbtCompound();
        slownessNbt.putString("Potion", "minecraft:slowness");

        NbtCompound harmingNbt = new NbtCompound();
        harmingNbt.putString("Potion", "minecraft:harming");

        NbtCompound poisonNbt = new NbtCompound();
        poisonNbt.putString("Potion", "minecraft:poison");

        RECIPE.put(new SpellIngredient(Items.MILK_BUCKET, null), 4);
        RECIPE.put(new SpellIngredient(Items.GRINDSTONE, null), 1);

        RECIPE.put(new SpellIngredient(Items.POTION, weaknessNbt), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, slownessNbt), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, harmingNbt), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, poisonNbt), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "cleanse", "cleansed", "cleansing", "cleanser", "dispel", "dispelling", "dispelled", "dispeller"
                },

                new double[] {
                        15.0, 10.0, 21.0, 1.0
                },

                SpellType.WHITE_MAGIC,

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

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }


                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }


                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        if (hit.getEntity() instanceof LivingEntity entity) {
                            entity.clearStatusEffects();
                        }

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}