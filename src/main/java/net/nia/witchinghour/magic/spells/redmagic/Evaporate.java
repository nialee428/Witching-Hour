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

public class Evaporate {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Evaporation";

    public static boolean deductMana(SpellBoltEntity bolt) {
        List<Spell> casted = new ArrayList<>();
        casted.add(SPELL);

        return ManaHelper.consume(bolt, casted, false, 0);
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.WATER_BUCKET, null), 1);
        RECIPE.put(new SpellIngredient(Items.FIRE_CHARGE, null), 8);

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "evaporation", "evaporate", "evaporating", "evaporates", "evaporated", "dry", "dried", "dryer",
                        "vaporizes", "vaporizing", "vaporizer", "vaporized", "vaporize", "drying", "drier"
                },

                new double[] {
                        2.0, 7.0, 10.0, 1.0 // MANA COST, LEVEL REQ, EXP REWARD
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

                        if (block != Blocks.WATER) {
                            return;
                        }

                        if (deductMana(bolt)) {
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
                                0.6f
                        );

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