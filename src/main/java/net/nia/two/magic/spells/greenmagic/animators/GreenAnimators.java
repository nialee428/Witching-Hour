package net.nia.witchinghour.magic.spells.greenmagic.animators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.TinyMushroomEntity;
import net.nia.witchinghour.magic.spells.other.SpellBoltEntity;

public class GreenAnimators {

    public static void checkTinyMushroom(
            BlockPos pos,
            BlockState state,
            Block block,
            World world,
            SpellBoltEntity bolt
    ) {

        // The center block must be a red mushroom.
        if (!state.isOf(Blocks.RED_MUSHROOM)) {
            return;
        }

        spawnTinyMushroom(
                world,
                pos,
                bolt.getOwner()
        );
    }

    private static void spawnTinyMushroom(
            World world,
            BlockPos bodyPos,
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

        TinyMushroomEntity tinyMushroomEntity = WitchingHourEntities.TINY_MUSHROOM.create((ServerWorld) world);

        if (tinyMushroomEntity == null) {
            return;
        }

        /*
         * Spawn the golem in the center of the structure.
         *
         * bodyPos is the center Blue Ice block.
         */
        tinyMushroomEntity.refreshPositionAndAngles(
                bodyPos.getX() + 0.5D,
                bodyPos.getY(),
                bodyPos.getZ() + 0.5D,
                0.0F,
                0.0F
        );
        if (player instanceof PlayerEntity p) {
            tinyMushroomEntity.setOwner(p);
        }

        // Add the golem to the world.
        world.spawnEntity(tinyMushroomEntity);
        world.breakBlock(bodyPos, false);
    }

    public static void checkAnimatable(
            BlockPos pos,
            BlockState state,
            Block block,
            World world,
            SpellBoltEntity bolt
    ) {

        checkTinyMushroom(pos, state, block, world, bolt);

    }
}