package net.nia.witchinghour.world.dimensions.tomb;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RitualGenerationManager {

    public static class Task {
        public final UUID player;
        public final int lane;
        public final BlockPos start;
        public int index = 0;

        public Task(UUID player, int lane, BlockPos start) {
            this.player = player;
            this.lane = lane;
            this.start = start;
        }
    }

    private static final List<Task> QUEUE = new ArrayList<>();

    public static void queue(UUID player, int lane, BlockPos start) {
        QUEUE.add(new Task(player, lane, start));
    }

    public static void tick(ServerWorld world) {
        if (QUEUE.isEmpty()) return;

        // process only 1–2 tasks per tick to avoid spikes
        int tasksThisTick = 7;

        for (int i = 0; i < tasksThisTick && !QUEUE.isEmpty(); i++) {
            Task task = QUEUE.get(0);

            boolean done = processTask(world, task);

            if (done) {
                QUEUE.remove(0);
            }
        }
    }

    private static boolean processTask(ServerWorld world, Task task) {

        if (task.index >= RoomChainGenerator.ROOMS.length) {
            return true; // finished
        }

        BlockPos pos = calculatePosition(task.start, task.index);

        RoomChainGenerator.placeRoom(world, pos, RoomChainGenerator.ROOMS[task.index]);

        task.index++;
        return false;
    }

    private static BlockPos calculatePosition(BlockPos start, int i) {
        if (i == 0) return start;
        if (i == 1) return start.add(0, 0, -48);
        if (i == 2) return start.add(0, 0, -48-48);
        if (i == 3) return start.add(20, 27, -96-48);

        return start.add(20, 27, -96 + ((i - 2) * -48));
    }
}