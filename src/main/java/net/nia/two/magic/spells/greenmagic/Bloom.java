package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.Scheduler;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bloom {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Bloom";

    private static void instantlyGrow(ServerWorld world, BlockPos pos) {
        net.minecraft.util.math.random.Random random = world.getRandom();

        for (int i = 0; i < 20; i++) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!(block instanceof Fertilizable fertilizable)) {
                break;
            }

            if (!fertilizable.isFertilizable(world, pos, state, false)) {
                break;
            }

            if (!fertilizable.canGrow(world, random, pos, state)) {
                break;
            }

            fertilizable.grow(world, random, pos, state);
        }
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.BONE_MEAL, null), 8);
        RECIPE.put(new SpellIngredient(Items.HONEY_BOTTLE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "bloom", "flourish", "sprout", "blooming", "bloomed", "flourished", "flourishing", "sprouted",
                        "sprouting", "blooms", "flourishes", "sprouts"
                },

                new double[] {
                        2.0, 15.0, 10.0, 1.0
                },

                SpellType.GREEN_MAGIC,

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
                        BlockState state2 = bolt.getWorld().getBlockState(pos.up());
                        Block block = state.getBlock();

                        if (bolt.getWorld() instanceof ServerWorld serverWorld && block instanceof Fertilizable fertilizable) {
                            net.minecraft.util.math.random.Random random = serverWorld.getRandom();

                            if (fertilizable.isFertilizable(serverWorld, pos, state, false) && fertilizable.canGrow(serverWorld, random, pos, state)) {


                                List<Spell> casted = new ArrayList<>();
                                casted.add(SPELL);

                                if (ManaHelper.consume(bolt, casted, false, 0)) {
                                    return;
                                }

                                if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                    bolt.discard();
                                    return;
                                }

                                Scheduler.schedule(1, () -> {
                                    instantlyGrow(serverWorld, pos);
                                });

                                BoneMealItem.createParticles(player.getWorld(), pos, 5);
                                bolt.getWorld().playSound(
                                        null,
                                        pos,
                                        SoundEvents.ITEM_BONE_MEAL_USE,
                                        SoundCategory.BLOCKS,
                                        1.0f,
                                        1.0f
                                );
                            }

                            if (fertilizable.isFertilizable(serverWorld, pos.up(), state2, false) && fertilizable.canGrow(serverWorld, random, pos.up(), state2)) {


                                List<Spell> casted = new ArrayList<>();
                                casted.add(SPELL);

                                if (ManaHelper.consume(bolt, casted, false, 0)) {
                                    return;
                                }

                                if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                    bolt.discard();
                                    return;
                                }

                                Scheduler.schedule(1, () -> {
                                    instantlyGrow(serverWorld, pos.up());
                                });

                                BoneMealItem.createParticles(player.getWorld(), pos.up(), 5);
                                bolt.getWorld().playSound(
                                        null,
                                        pos.up(),
                                        SoundEvents.ITEM_BONE_MEAL_USE,
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
                        Entity target = hit.getEntity();

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        BlockPos pos = hit.getEntity().getBlockPos();
                        BlockState state = bolt.getWorld().getBlockState(pos);
                        BlockState state2 = bolt.getWorld().getBlockState(pos.up());
                        Block block = state.getBlock();

                        if (bolt.getWorld() instanceof ServerWorld serverWorld && block instanceof Fertilizable fertilizable) {
                            net.minecraft.util.math.random.Random random = serverWorld.getRandom();

                            if (fertilizable.isFertilizable(serverWorld, pos, state, false) && fertilizable.canGrow(serverWorld, random, pos, state)) {


                                List<Spell> casted = new ArrayList<>();
                                casted.add(SPELL);

                                if (ManaHelper.consume(bolt, casted, false, 0)) {
                                    return;
                                }

                                if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                    bolt.discard();
                                    return;
                                }

                                Scheduler.schedule(1, () -> {
                                    instantlyGrow(serverWorld, pos);
                                });

                                BoneMealItem.createParticles(player.getWorld(), pos, 5);
                                bolt.getWorld().playSound(
                                        null,
                                        pos,
                                        SoundEvents.ITEM_BONE_MEAL_USE,
                                        SoundCategory.BLOCKS,
                                        1.0f,
                                        1.0f
                                );
                            }

                            if (fertilizable.isFertilizable(serverWorld, pos.up(), state2, false) && fertilizable.canGrow(serverWorld, random, pos.up(), state2)) {


                                List<Spell> casted = new ArrayList<>();
                                casted.add(SPELL);

                                if (ManaHelper.consume(bolt, casted, false, 0)) {
                                    return;
                                }

                                if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                    bolt.discard();
                                    return;
                                }

                                Scheduler.schedule(1, () -> {
                                    instantlyGrow(serverWorld, pos.up());
                                });

                                BoneMealItem.createParticles(player.getWorld(), pos.up(), 5);
                                bolt.getWorld().playSound(
                                        null,
                                        pos.up(),
                                        SoundEvents.ITEM_BONE_MEAL_USE,
                                        SoundCategory.BLOCKS,
                                        1.0f,
                                        1.0f
                                );
                            }
                        }

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }

}
