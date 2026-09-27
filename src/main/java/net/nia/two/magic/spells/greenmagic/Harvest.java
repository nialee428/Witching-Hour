package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Harvest {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Harvest";

    private static boolean isHarvestable(BlockState state) {
        Block block = state.getBlock();

        return block instanceof PlantBlock
                || block instanceof SaplingBlock
                || block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof AttachedStemBlock
                || block instanceof MushroomPlantBlock
                || block instanceof NetherWartBlock
                || block instanceof VineBlock
                || block instanceof LeavesBlock
                || block instanceof CactusBlock
                || block instanceof SugarCaneBlock
                || block instanceof BambooBlock
                || block instanceof KelpBlock
                || block instanceof SeagrassBlock
                || block instanceof ChorusFlowerBlock
                || block instanceof ChorusPlantBlock
                || block instanceof TallPlantBlock

                // Wood
                || block == Blocks.OAK_LOG
                || block == Blocks.SPRUCE_LOG
                || block == Blocks.BIRCH_LOG
                || block == Blocks.JUNGLE_LOG
                || block == Blocks.ACACIA_LOG
                || block == Blocks.DARK_OAK_LOG
                || block == Blocks.MANGROVE_LOG
                || block == Blocks.CHERRY_LOG

                || block == Blocks.OAK_WOOD
                || block == Blocks.SPRUCE_WOOD
                || block == Blocks.BIRCH_WOOD
                || block == Blocks.JUNGLE_WOOD
                || block == Blocks.ACACIA_WOOD
                || block == Blocks.DARK_OAK_WOOD
                || block == Blocks.MANGROVE_WOOD
                || block == Blocks.CHERRY_WOOD

                || block == Blocks.CRIMSON_STEM
                || block == Blocks.WARPED_STEM
                || block == Blocks.CRIMSON_HYPHAE
                || block == Blocks.WARPED_HYPHAE

                // QoL blocks from your old list
                || block == Blocks.GRAVEL
                || block == Blocks.GLOWSTONE
                || block == Blocks.TUFF
                || block == Blocks.DIORITE
                || block == Blocks.ANDESITE
                || block == Blocks.GRANITE;
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.SHEARS, null), 1);
        RECIPE.put(new SpellIngredient(Items.IRON_HOE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "harvest", "harvested", "harvesting", "harvester", "harvests"
                },

                new double[] {
                        3.0, 5.0, 3.0, 1.0
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
                        World world = bolt.getWorld();
                        BlockState state = world.getBlockState(pos);


                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        if (isHarvestable(state)) {
                            Block.dropStacks(state, world, player.getBlockPos(), null, player, player.getMainHandStack());
                            world.breakBlock(pos, false);
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

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}