package net.nia.witchinghour.magic.spells.redmagic;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ignition {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Ignition";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.FLINT_AND_STEEL, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "fire", "ignite", "flame", "aflame", "ignition", "igniting", "ignited", "ignites", "flames",
                        "fires", "fiery", "flamed", "burn", "burning", "burned", "burns", "burnt"
                },

                new double[] {
                        5.0, 5.0, 8.0, 1.0
                },

                SpellType.RED_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        BlockPos pos = hit.getBlockPos();

                        if (SpellBoltEntity.hasRandomKeyword(spell)) {
                            pos = pos.down();
                        }

                        BlockState state = bolt.getWorld().getBlockState(pos.up());
                        BlockState state2 = bolt.getWorld().getBlockState(pos);
                        World world = bolt.getWorld();


                        if (state.isAir() && !state2.isReplaceable()) { // bugfix: state.isAir instead of state.isReplaceable() prevents accidental object destruction!

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            bolt.getWorld().setBlockState(
                                    pos.up(),
                                    Blocks.FIRE.getDefaultState()
                            );

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.up().getY(),
                                    pos.getZ(),
                                    SoundEvents.ITEM_FLINTANDSTEEL_USE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    1.0f
                            );

                        } if (state2.getBlock() instanceof CandleBlock) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            world.setBlockState(pos, state2.with(CandleBlock.LIT, true), Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ITEM_FLINTANDSTEEL_USE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    1.0f
                            );
                        } if (state2.getBlock() == Blocks.TNT) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            if (state2.getBlock() instanceof TntBlock) {

                                TntBlock.primeTnt(world, pos);

                                world.removeBlock(pos, false);

                                world.playSound(
                                        null,
                                        pos.getX(),
                                        pos.getY(),
                                        pos.getZ(),
                                        SoundEvents.ITEM_FLINTANDSTEEL_USE,
                                        SoundCategory.BLOCKS,
                                        1.0f,
                                        1.0f
                                );
                            }

                        }

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

                        if (hit.getEntity() != null) {

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

                            hit.getEntity().setOnFireFor(5);

                            hit.getEntity().getWorld().playSound(
                                    null,
                                    hit.getEntity().getX(),
                                    hit.getEntity().getY(),
                                    hit.getEntity().getZ(),
                                    SoundEvents.ITEM_FLINTANDSTEEL_USE,
                                    SoundCategory.AMBIENT,
                                    1.0f,
                                    1.0f
                            );
                        }

                        bolt.discard();
                    }
                },

                RECIPE
        );

    }
}