package net.nia.witchinghour.magic.rituals.tometrials;

import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.events.TimeChecker;
import net.nia.witchinghour.world.dimensions.ModDimensions;
import net.nia.witchinghour.world.dimensions.tomb.LaneRegistry;
import net.nia.witchinghour.world.dimensions.tomb.RitualGenerationManager;
import net.nia.witchinghour.world.dimensions.tomb.TrialRules;

public class RitualPlateHandler {

    private static boolean isValidRitualStructure(World world, BlockPos center) {

        BlockPos[] corners = {
                center.add(1, 0, 1),
                center.add(1, 0, -1),
                center.add(-1, 0, 1),
                center.add(-1, 0, -1)
        };

        BlockPos[] edges = {
                center.add(1, 0, 0),
                center.add(-1, 0, 0),
                center.add(0, 0, 1),
                center.add(0, 0, -1)
        };

        for (BlockPos pos : corners) {
            if (!(world.getBlockState(pos).getBlock() instanceof CandleBlock)) return false;
            if (!world.getBlockState(pos).get(CandleBlock.LIT)) return false;
            if (world.getBlockState(pos).get(CandleBlock.CANDLES) != 3) return false;
            if (!world.getBlockState(pos).isOf(Blocks.BLACK_CANDLE)) return false;
        }

        for (BlockPos pos : edges) {
            if (!world.getBlockState(pos).isOf(Blocks.WITHER_SKELETON_SKULL)) return false;
        }

        return true;
    }

    public static void onStep(ServerPlayerEntity player, BlockPos pos, World world) {

        if (!(world.getBlockState(pos).getBlock() instanceof PressurePlateBlock)) return;
        if (!world.getBlockState(pos).isOf(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE)) return;

        if (!isValidRitualStructure(world, pos)) return;
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

        if (data.getPlayerName().isEmpty()) {
            return;
        }

        teleportToTomb(player);
    }

    private static void teleportToTomb(ServerPlayerEntity player) {

        ServerWorld targetWorld = player.getServer().getWorld(ModDimensions.TOMBS_LEVEL_KEY);
        if (targetWorld == null) return;

        if (targetWorld != player.getWorld() && !TimeChecker.isWitchingHour) return;

        LaneRegistry reg = LaneRegistry.get(targetWorld);

        if (!reg.hasLane(player.getUuid())) {

            int lane = reg.assignLane(targetWorld, player.getUuid()) + 1;

            if (lane == -1) {
                player.sendMessage(Text.literal("§cThe ritual fails... It seems this is a shared space."), false);
                return;
            }

            player.sendMessage(Text.literal("A lane has been bound to your soul."+"  DEBUG:"+lane), false);
        }

        int lane = reg.getLane(player.getUuid()) + 1;

        BlockPos start = new BlockPos(lane * 80, 80, 0);

        if (!reg.hasGeneratedRooms(player.getUuid())) {
            RitualGenerationManager.queue(player.getUuid(), lane, start);
            reg.markRoomsGenerated(player.getUuid());
        }

        player.teleport(targetWorld,
                start.getX() + 24,
                start.getY() + 16,
                start.getZ() + 24,
                player.getYaw(),
                player.getPitch()
        );
        TrialRules rules = LaneRegistry.getRules(player.getUuid());

        System.out.println("Lane: " + lane + " | Generated: " + reg.hasGeneratedRooms(player.getUuid()));
    }
}