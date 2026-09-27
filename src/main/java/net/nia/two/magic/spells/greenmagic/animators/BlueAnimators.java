package net.nia.witchinghour.magic.spells.greenmagic.animators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.entities.golems.icegolem.IceGolemEntity;
import net.nia.witchinghour.magic.spells.other.SpellBoltEntity;

public class BlueAnimators {

    public static void checkIceGolem(
            BlockPos pos,
            BlockState state,
            Block block,
            World world,
            SpellBoltEntity bolt
    ) {

        // The center block must be Blue Ice.
        if (!state.isOf(Blocks.BLUE_ICE)) {
            return;
        }

        /*
         * Check all four cardinal directions.
         *
         * North
         * South
         * East
         * West
         */
        Direction[] directions = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST
        };

        for (Direction direction : directions) {

            BlockPos pumpkinPos = pos.up();

            BlockPos leftArm = pos.offset(direction);
            BlockPos rightArm = pos.offset(direction.getOpposite());

            /*
             *              PUMPKIN
             *                 |
             * BLUE ICE - BLUE ICE - BLUE ICE
             *                 |
             *            BLUE ICE
             */

            // Pumpkin above the body.
            if (!isPumpkin(world, pumpkinPos)) {
                continue;
            }

            // Left arm.
            if (!isBlueIce(world, leftArm)) {
                continue;
            }

            // Right arm.
            if (!isBlueIce(world, rightArm)) {
                continue;
            }

            // Lower body.
            if (!isBlueIce(world, pos.down())) {
                continue;
            }

            /*
             * We found a complete Ice Golem structure.
             */
            spawnIceGolem(
                    world,
                    pos,
                    pumpkinPos,
                    leftArm,
                    rightArm,
                    pos.down(),
                    bolt.getOwner()
            );

            return;
        }
    }

    private static boolean isBlueIce(
            World world,
            BlockPos pos
    ) {
        return world.getBlockState(pos).isOf(Blocks.BLUE_ICE);
    }

    private static boolean isPumpkin(
            World world,
            BlockPos pos
    ) {
        Block block = world.getBlockState(pos).getBlock();

        return block == Blocks.PUMPKIN
                || block == Blocks.CARVED_PUMPKIN;
    }

    private static void spawnIceGolem(
            World world,
            BlockPos bodyPos,
            BlockPos pumpkinPos,
            BlockPos leftArm,
            BlockPos rightArm,
            BlockPos lowerBody,
            Entity player
    ) {

        /*
         * We'll add the actual Ice Golem spawning here.
         *
         * For now, remove the structure so we know
         * the detection itself is working.
         */



        if (world.isClient()) {
            return;
        }

        IceGolemEntity golem = WitchingHourEntities.ICE_GOLEM.create((ServerWorld) world);

        if (golem == null) {
            return;
        }

        /*
         * Spawn the golem in the center of the structure.
         *
         * bodyPos is the center Blue Ice block.
         */
        golem.refreshPositionAndAngles(
                bodyPos.getX() + 0.5D,
                bodyPos.getY(),
                bodyPos.getZ() + 0.5D,
                0.0F,
                0.0F
        );
        if (player instanceof PlayerEntity p) {
            golem.setOwner(p);
        }

        // Add the golem to the world.
        world.spawnEntity(golem);
        world.breakBlock(pumpkinPos, false);
        world.breakBlock(bodyPos, false);
        world.breakBlock(leftArm, false);
        world.breakBlock(rightArm, false);
        world.breakBlock(lowerBody, false);
    }

    public static void checkAnimatable(
            BlockPos pos,
            BlockState state,
            Block block,
            World world,
            SpellBoltEntity bolt
    ) {

        checkIceGolem(pos, state, block, world, bolt);

    }
}