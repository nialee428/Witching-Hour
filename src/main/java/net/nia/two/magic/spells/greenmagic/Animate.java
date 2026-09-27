package net.nia.witchinghour.magic.spells.greenmagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.greenmagic.animators.BlueAnimators;
import net.nia.witchinghour.magic.spells.greenmagic.animators.GreenAnimators;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.List;

public class Animate {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Animate";

    public static void init() {

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "animate", "animated", "animator", "animates", "animating", "back to life", "alive", "live", "lives",
                        "lived", "living", "brought to life", "bring this to life", "bring her to life", "bring them to life",
                        "bring it to life", "bring that to life"
                },

                new double[] {
                        5.0, 1.0, 1.0, 0.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.ACTION, // SPELL TYPE

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


                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        World world = bolt.getWorld();
                        BlockPos pos = hit.getBlockPos();
                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();

                        BlueAnimators.checkAnimatable(pos, state, block, world, bolt);
                        GreenAnimators.checkAnimatable(pos, state, block, world, bolt);

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

                null // SPELL RECIPE
        );

    }
}