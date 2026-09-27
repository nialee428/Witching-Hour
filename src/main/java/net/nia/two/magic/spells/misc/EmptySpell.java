package net.nia.witchinghour.magic.spells.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptySpell {

    public static final Spell SPELL = new Spell();
    public static final String ID = "EmptySpell";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound waterNbt = new NbtCompound();
        waterNbt.putString("Potion", "minecraft:water");

        RECIPE.put(new SpellIngredient(Items.POTION, waterNbt), 1); // WATER BOTTLE
        RECIPE.put(new SpellIngredient(Items.ICE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        ""
                },

                new double[] {
                        0.0, 0.0, 0.0, 0.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.ACTION, // SPELL TYPE

                new SpellBehavior() { // SPELL BEHAVIORS

                    @Override
                    public void onBlockHit( // BLOCK HIT
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {

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



                        bolt.discard();

                    }

                    @Override
                    public void onEntityHit( // ENTITY HIT
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

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
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



                        bolt.discard();

                    }
                },

                RECIPE // SPELL RECIPE
        );

    }
}