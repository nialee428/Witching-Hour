package net.nia.witchinghour.world.dimensions.tomb;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RoomChainGenerator {

    public static final Identifier[] ROOMS = new Identifier[] {
            new Identifier("witching-hour", "trial_green"),
            new Identifier("witching-hour", "trial_white"),
            new Identifier("witching-hour", "trial_purple"),
            new Identifier("witching-hour", "trial_yellow"),
            new Identifier("witching-hour", "trial_blue"),
            new Identifier("witching-hour", "trial_red"),
            new Identifier("witching-hour", "trial_black")
    };


    public static void generateChain(ServerWorld world, BlockPos start) {

        BlockPos current = start;

        for (int i = 0; i < ROOMS.length; i++) {

            // Place current room
            placeRoom(world, current, ROOMS[i]);

            // Move to next position
            if (i < 3) {
                // green -> white -> purple (straight)
                current = current.add(0, 0, -48);
            }
            else if (i == 3) {
                // purple -> yellow (your special offset jump)
                current = current.add(20, 27, -48);
            }
            else {
                // yellow -> blue -> red -> black (continue from new level)
                current = current.add(0, 0, -48);
            }
        }
    }

    public static BlockPos getPlayerStart(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        LaneRegistry reg = LaneRegistry.get(world);

        int lane = reg.getLane(player.getUuid());

        return new BlockPos(lane * 64, 80, 0);
    }

    public static void placeRoom(ServerWorld world, BlockPos origin, Identifier id) {

        StructureTemplateManager manager = world.getStructureTemplateManager();
        var optional = manager.getTemplate(id);

        if (optional.isEmpty()) {
            System.out.println("Missing structure: " + id);
            return;
        }

        StructureTemplate template = optional.get();

        StructurePlacementData data = new StructurePlacementData()
                .setIgnoreEntities(false)
                .setUpdateNeighbors(false);

        template.place(world, origin, origin, data, world.getRandom(), 2);

        System.out.println("Placed room: " + id + " at " + origin);
    }

    public static void generatePlayerRooms(ServerPlayerEntity player, ServerWorld world) {
        BlockPos start = getPlayerStart(player);
        generateChain(world, start);
    }

}