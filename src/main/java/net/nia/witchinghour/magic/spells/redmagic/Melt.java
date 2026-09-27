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
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Melt {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Melt";
    public static final List<Block> STONE_BLOCKS = List.of(
            // Core Stone
            Blocks.STONE,
            Blocks.COBBLESTONE,

            // Andesite
            Blocks.ANDESITE,

            // Diorite
            Blocks.DIORITE,

            // Granite
            Blocks.GRANITE,

            // Deepslate
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE,

            // Blackstone
            Blocks.BLACKSTONE,

            // Tuff
            Blocks.TUFF,

            // Basalt
            Blocks.BASALT,
            Blocks.POLISHED_BASALT,

            Blocks.OBSIDIAN,
            Blocks.CRYING_OBSIDIAN
    );

    public static boolean deductMana(SpellBoltEntity bolt) {
        List<Spell> casted = new ArrayList<>();
        casted.add(SPELL);

        return ManaHelper.consume(bolt, casted, false, 0);
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.LAVA_BUCKET, null), 4);
        RECIPE.put(new SpellIngredient(Items.FIRE_CHARGE, null), 4);
        RECIPE.put(new SpellIngredient(Items.STONE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "melt", "melts", "melting", "melted", "melter", "molten", "disintegrate", "disintegrates",
                        "disintegrated", "disintegrating", "disintegrator", "disintegrators"
                },

                new double[] {
                        2.0, 15.0, 10.0, 1.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.RED_MAGIC, // SPELL TYPE

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


                        BlockPos pos = hit.getBlockPos();
                        BlockState state = bolt.getWorld().getBlockState(pos);
                        Block block = state.getBlock();
                        World world = bolt.getWorld();

                        if (STONE_BLOCKS.contains(block)) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.LAVA.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    0.6f
                            );
                        } else if (block == Blocks.ICE) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.WATER.getDefaultState(),
                                    Block.NOTIFY_ALL);

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
                        } else if (block == Blocks.PACKED_ICE) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            world.setBlockState(pos,
                                    Blocks.ICE.getDefaultState(),
                                    Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    .7f
                            );
                        } else if (block == Blocks.BLUE_ICE) {

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
                                    .5f
                            );
                        } else if (block instanceof SnowBlock) {

                            if (deductMana(bolt)) {
                                return;
                            }

                            int layers = state.get(SnowBlock.LAYERS); // 1–8

                            // Convert snow layers to water flow level
                            int waterLevel = Math.max(0, 8 - layers); // 8->0, 7->1, 6→2, ... 1->7

                            BlockState water = Blocks.WATER.getDefaultState()
                                    .with(FluidBlock.LEVEL, waterLevel);

                            world.setBlockState(pos, water, Block.NOTIFY_ALL);

                            world.playSound(
                                    null,
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                                    SoundCategory.BLOCKS,
                                    1.0f,
                                    .8f
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