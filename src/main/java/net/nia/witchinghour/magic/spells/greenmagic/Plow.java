package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plow {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Plow";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.DIRT, null), 8);
        RECIPE.put(new SpellIngredient(Items.IRON_HOE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "plow", "plowed", "plowing", "plower"
                },

                new double[] {
                        1.0, 10.0, 4.0, 1.0
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
                        Block block = state.getBlock();

                        boolean isTillable = (Blocks.GRASS_BLOCK == block ||
                                Blocks.DIRT == block ||
                                Blocks.DIRT_PATH == block ||
                                Blocks.PODZOL == block);

                        if (isTillable) {

                            List<Spell> casted = new ArrayList<>();
                            casted.add(SPELL);
                            if (ManaHelper.consume(bolt, casted, false, 0)) {
                                return;
                            }

                            if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                                bolt.discard();
                                return;
                            }

                            bolt.getWorld().setBlockState(pos, Blocks.FARMLAND.getDefaultState());

                            bolt.getWorld().playSound(
                                    null,
                                    pos,
                                    SoundEvents.ITEM_HOE_TILL,
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

                        Entity target = hit.getEntity();
                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;

                        if (target == null || player == null) {
                            return;
                        }

                        BlockPos pos = hit.getEntity().getBlockPos();
                        BlockState state = bolt.getWorld().getBlockState(pos);
                        Block block = state.getBlock();

                        boolean isTillable = (Blocks.GRASS_BLOCK == block ||
                                Blocks.DIRT == block ||
                                Blocks.DIRT_PATH == block ||
                                Blocks.PODZOL == block);

                        if (isTillable) {

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

                            bolt.getWorld().setBlockState(pos, Blocks.FARMLAND.getDefaultState());

                            bolt.getWorld().playSound(
                                    null,
                                    pos,
                                    SoundEvents.ITEM_HOE_TILL,
                                    SoundCategory.BLOCKS,
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
