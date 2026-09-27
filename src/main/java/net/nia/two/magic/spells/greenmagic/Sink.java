package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.Scheduler;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sink {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Sink";
    public static final List<Block> EARTH_BLOCKS = List.of(
            // --- Dirt family ---
            Blocks.DIRT,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL,
            Blocks.ROOTED_DIRT,
            Blocks.MUD,
            Blocks.MUDDY_MANGROVE_ROOTS,
            Blocks.CLAY,

            // --- Grass / Mycelium ---
            Blocks.GRASS_BLOCK,
            Blocks.MYCELIUM,
            Blocks.MOSS_BLOCK,

            // --- Sand family ---
            Blocks.SAND,
            Blocks.RED_SAND,
            Blocks.SANDSTONE,
            Blocks.CHISELED_SANDSTONE,
            Blocks.CUT_SANDSTONE,
            Blocks.SMOOTH_SANDSTONE,
            Blocks.RED_SANDSTONE,
            Blocks.CHISELED_RED_SANDSTONE,
            Blocks.CUT_RED_SANDSTONE,
            Blocks.SMOOTH_RED_SANDSTONE,

            // --- Gravel ---
            Blocks.GRAVEL,

            // --- Stone family ---
            Blocks.STONE,
            Blocks.GRANITE,
            Blocks.POLISHED_GRANITE,
            Blocks.DIORITE,
            Blocks.POLISHED_DIORITE,
            Blocks.ANDESITE,
            Blocks.POLISHED_ANDESITE,

            // --- Cobblestone ---
            Blocks.COBBLESTONE,
            Blocks.MOSSY_COBBLESTONE,

            // --- Stone bricks ---
            Blocks.STONE_BRICKS,
            Blocks.CRACKED_STONE_BRICKS,
            Blocks.MOSSY_STONE_BRICKS,
            Blocks.CHISELED_STONE_BRICKS,

            // --- Deepslate family ---
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE,
            Blocks.POLISHED_DEEPSLATE,
            Blocks.DEEPSLATE_BRICKS,
            Blocks.CRACKED_DEEPSLATE_BRICKS,
            Blocks.DEEPSLATE_TILES,
            Blocks.CRACKED_DEEPSLATE_TILES,
            Blocks.CHISELED_DEEPSLATE,

            // --- Tuff & Dripstone ---
            Blocks.TUFF,
            Blocks.DRIPSTONE_BLOCK,

            // --- Terracotta ---
            Blocks.TERRACOTTA,
            Blocks.WHITE_TERRACOTTA,
            Blocks.ORANGE_TERRACOTTA,
            Blocks.MAGENTA_TERRACOTTA,
            Blocks.LIGHT_BLUE_TERRACOTTA,
            Blocks.YELLOW_TERRACOTTA,
            Blocks.LIME_TERRACOTTA,
            Blocks.PINK_TERRACOTTA,
            Blocks.GRAY_TERRACOTTA,
            Blocks.LIGHT_GRAY_TERRACOTTA,
            Blocks.CYAN_TERRACOTTA,
            Blocks.PURPLE_TERRACOTTA,
            Blocks.BLUE_TERRACOTTA,
            Blocks.BROWN_TERRACOTTA,
            Blocks.GREEN_TERRACOTTA,
            Blocks.RED_TERRACOTTA,
            Blocks.BLACK_TERRACOTTA,

            // --- Mud bricks ---
            Blocks.MUD_BRICKS,

            // --- Misc natural earth blocks ---
            Blocks.CALCITE,
            Blocks.BASALT,
            Blocks.SMOOTH_BASALT,
            Blocks.MAGMA_BLOCK,
            Blocks.SOUL_SAND,
            Blocks.SOUL_SOIL,
            Blocks.NETHERRACK,
            Blocks.BLACKSTONE,
            Blocks.POLISHED_BLACKSTONE,
            Blocks.POLISHED_BLACKSTONE_BRICKS,
            Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
            Blocks.CHISELED_POLISHED_BLACKSTONE,
            Blocks.END_STONE
    );

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.SOUL_SAND, null), 8);
        RECIPE.put(new SpellIngredient(Items.COBBLED_DEEPSLATE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "sink", "sinking", "sinks", "sank", "sunk", "sunken", "bury", "buried", "burying", "buries"
                },

                new double[] {
                        45.0, 20.0, 32.0, 1.0
                },

                SpellType.GREEN_MAGIC,

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

                        Entity target = hit.getEntity();

                        BlockPos base = target.getBlockPos();
                        World world = target.getWorld();

                        BlockState state1 = world.getBlockState(base.down());
                        BlockState state2 = world.getBlockState(base.down(2));
                        BlockState state3 = world.getBlockState(base.down(3));
                        BlockState state4 = world.getBlockState(base.down(4));

                        boolean valid =
                                (state1.isAir() || EARTH_BLOCKS.contains(state1.getBlock())) && (EARTH_BLOCKS.contains(state2.getBlock()) && EARTH_BLOCKS.contains(state3.getBlock()) && EARTH_BLOCKS.contains(state4.getBlock()));

                        if (!valid) {
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

                        if (target instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        double goalY = target.getY() - 4;

                        target.teleport(target.getX(), target.getY() - 1, target.getZ());

                        Scheduler.schedule(2, () ->
                                target.teleport(target.getX(), target.getY() - 1, target.getZ())
                        );

                        Scheduler.schedule(4, () ->
                                target.teleport(target.getX(), target.getY() - 1, target.getZ())
                        );

                        Scheduler.schedule(6, () ->
                                target.teleport(target.getX(), target.getY() - 1, target.getZ())
                        );

                        Scheduler.schedule(8, () ->
                                target.teleport(target.getX(), goalY, target.getZ())
                        );

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}