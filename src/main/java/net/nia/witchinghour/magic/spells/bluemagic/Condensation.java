package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class Condensation {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Condensation";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.CAULDRON, null), 1);
        RECIPE.put(new SpellIngredient(Items.WATER_BUCKET, null), 4);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "condensation", "condensate", "condensing", "condensed", "condenses", "soak", "soaked", "soaking",
                        "soaker", "soaks", "soakers", "douse", "douses", "doused", "dousing", "douser", "dousers", "drench",
                        "drenches", "drenched", "drenching", "drencher", "drenchers", "condense", "condenser"
                },

                new double[] {
                        2.0, 20.0, 15.0, 1.0
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

                        if (!(block instanceof AirBlock)) {
                            pos = hit.getBlockPos();

                            state = bolt.getWorld().getBlockState(pos.up());
                            block = state.getBlock();
                            world = bolt.getWorld();

                            if (!(block instanceof AirBlock)) {
                                return;
                            }
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

                        world.setBlockState(pos,
                                Blocks.WATER.getDefaultState(),
                                Block.NOTIFY_ALL);

                        world.playSound(
                                null,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                SoundEvents.ITEM_BUCKET_EMPTY,
                                SoundCategory.BLOCKS,
                                1.0f,
                                1.0f
                        );


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