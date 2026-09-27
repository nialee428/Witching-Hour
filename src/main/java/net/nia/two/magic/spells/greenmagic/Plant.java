package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plant {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Plant";

    private static boolean isPlant(Block block) {
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
                || block == Blocks.WARPED_HYPHAE;
    }

    public static boolean placePlant(
            World world,
            BlockPos pos,
            ItemStack stack,
            PlayerEntity player,
            Direction facing
    ) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block block = blockItem.getBlock();

        if (!isPlant(block)) {
            return false;
        }

        if (!world.getBlockState(pos).isReplaceable()) {
            return false;
        }

        BlockState state = block.getDefaultState();

        // Directional plants
        if (block instanceof CocoaBlock) {
            state = state.with(CocoaBlock.FACING, facing);
        }

        // Vines
        if (block instanceof VineBlock) {
            Direction direction = facing.getOpposite();

            state = state.with(
                    VineBlock.getFacingProperty(direction),
                    true
            );
        }

        world.setBlockState(pos, state, Block.NOTIFY_ALL);

        world.playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                SoundEvents.ITEM_CROP_PLANT,
                SoundCategory.BLOCKS,
                1.0f,
                1.0f
        );

        return true;
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.BONE_MEAL, null), 4);
        RECIPE.put(new SpellIngredient(Items.DIRT, null), 4);
        RECIPE.put(new SpellIngredient(Items.STONE_HOE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "plant", "plants", "planted", "planter", "planters", "planting"
                },

                new double[] {
                        1.0, 1.0, 3.0, 1.0
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
                        BlockState state = bolt.getWorld().getBlockState(pos.up());
                        World world = bolt.getWorld();

                        if (!state.isAir()) {
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

                        ItemStack held = player.getMainHandStack();
                        Item item = held.getItem();

                        BlockPos placePos = pos.up();

                        boolean success = placePlant(
                                world,
                                placePos,
                                held,
                                player,
                                player.getHorizontalFacing()
                        );

                        if (success) {
                            if (!player.isCreative()) {
                                held.decrement(1);
                            }
                        }

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