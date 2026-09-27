package net.nia.witchinghour.magic.spells.redmagic;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
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

public class Collapse {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Collapse";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.SAND, null), 4);
        RECIPE.put(new SpellIngredient(Items.GRAVEL, null), 4);
        RECIPE.put(new SpellIngredient(Items.ANVIL, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "collapse", "collapsed", "collapsing", "crumble", "crumbled", "crumbling", "plummet", "plummeted",
                        "plummeting", "fall", "falls", "fell", "falling"
                },

                new double[] {
                        3.0, 10.0, 13.0, 1.0
                },

                SpellType.RED_MAGIC,

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
                        BlockState state2 = world.getBlockState(pos.down());
                        BlockState state3 = world.getBlockState(pos.down().down());

                        if (state.isReplaceable() || !state2.isReplaceable() || !state3.isReplaceable()) {
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

                        world.removeBlock(pos, false);

                        FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, pos, state);
                        falling.setHurtEntities(0.5f, 8);

                        world.spawnEntity(falling);

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