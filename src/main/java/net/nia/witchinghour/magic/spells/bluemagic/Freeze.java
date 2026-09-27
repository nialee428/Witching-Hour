package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Freeze {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Freeze";

    public static boolean deductMana(SpellBoltEntity bolt) {
        List<Spell> casted = new ArrayList<>();
        casted.add(SPELL);

        return ManaHelper.consume(bolt, casted, false, 0);
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.WATER_BUCKET, null), 1);
        RECIPE.put(new SpellIngredient(Items.ICE, null), 8);
        RECIPE.put(new SpellIngredient(Items.PACKED_ICE, null), 4);
        RECIPE.put(new SpellIngredient(Items.BLUE_ICE, null), 2);

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "freeze", "freezes", "frozen", "freezer", "freezing", "freezers", "icy", "chill", "chilled",
                        "chilling", "chiller", "chills", "chillest"
                },

                new double[] {
                        2.0, 15.0, 10.0, 1.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.BLUE_MAGIC, // SPELL TYPE

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

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }


                        BlockPos pos = hit.getBlockPos();
                        BlockState state = bolt.getWorld().getBlockState(pos);
                        Block block = state.getBlock();
                        World world = bolt.getWorld();

                        if (block == Blocks.WATER) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            int level = state.get(FluidBlock.LEVEL);

                            // Source block → Ice
                            if (level == 0) {
                                world.setBlockState(pos, Blocks.ICE.getDefaultState(), Block.NOTIFY_ALL);
                                return;
                            }

                            // Flowing water → Snow layers
                            // You can invert or remap this however you want
                            int layers = Math.max(1, Math.min(7, level));

                            BlockState snow = Blocks.SNOW.getDefaultState()
                                    .with(SnowBlock.LAYERS, layers);

                            world.setBlockState(pos, snow, Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    0.9f
                            );
                        } else if (block == Blocks.ICE) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.PACKED_ICE.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    0.6f
                            );
                        } else if (block == Blocks.PACKED_ICE) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.BLUE_ICE.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    .3f
                            );
                        } else if (block == Blocks.LAVA) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.OBSIDIAN.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    .75f
                            );
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

                        bolt.discard();

                    }
                },

                RECIPE // SPELL RECIPE
        );

    }
}