package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Extinguish {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Extinguish";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound waterNbt = new NbtCompound();
        waterNbt.putString("Potion", "minecraft:water");

        RECIPE.put(new SpellIngredient(Items.POTION, waterNbt), 4); // WATER BOTTLE
        RECIPE.put(new SpellIngredient(Items.POWDER_SNOW_BUCKET, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "extinguish", "extinguishes", "extinguishing", "extinguished", "extinguisher", "snuff", "snuffed",
                        "snuffer", "snuffing", "snuffs"
                },

                new double[] {
                        1.0, 5.0, 4.0, 0.0
                },

                SpellType.BLUE_MAGIC,

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
                        BlockState state = bolt.getWorld().getBlockState(pos);
                        Block block = state.getBlock();
                        World world = bolt.getWorld();

                        if (block instanceof AbstractFireBlock) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.AIR.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    1.0f
                            );
                        } else if (block instanceof CampfireBlock) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            world.setBlockState(
                                    pos,
                                    state.with(CampfireBlock.LIT, false),
                                    Block.NOTIFY_ALL
                            );

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    1.0f
                            );
                        } else if (block instanceof CandleBlock) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            world.setBlockState(
                                    pos,
                                    state.with(CandleBlock.LIT, false),
                                    Block.NOTIFY_ALL
                            );

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.BLOCK_CANDLE_EXTINGUISH,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    1.0f
                            );
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

                        World world = bolt.getWorld();

                        hit.getEntity().extinguishWithSound();
                        world.playSound(
                                null,
                                hit.getEntity().getX(),
                                hit.getEntity().getY(),
                                hit.getEntity().getZ(),
                                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                SoundCategory.BLOCKS,
                                1.0f,
                                1.0f
                        );

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}