package net.nia.witchinghour.magic.spells.redmagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.magic.spells.purplemagic.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class Destroy {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Destroy";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "destroy", "destroying", "destroyed", "destruction", "destroyer", "destroys", "break", "breaking",
                        "broken", "broke"
                },

                new double[] {
                        1.0, 1.0, 2.0, 1.0
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

                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

                        float hardness = state.getHardness(world, pos) * 2;

                        if (data.getSpellLevel("General") < hardness || state.isOf(Blocks.BEDROCK)) {
                            return;
                        }

                        if (state.isOf(Blocks.OBSIDIAN)) {
                            world.setBlockState(
                                    pos,
                                    Blocks.CRYING_OBSIDIAN.getDefaultState(),
                                    Block.NOTIFY_ALL
                            );

                            state = world.getBlockState(pos);
                        }

                        if (data.getSpellLevel("General") < hardness || state.getBlock() == Blocks.BEDROCK) {
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

                        if (bolt.casted.contains(Waypoint.SPELL) || bolt.casted.contains(Shield.SPELL)) {
                            return;
                        }

                        world.breakBlock(pos, true);

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

                null
        );

    }
}