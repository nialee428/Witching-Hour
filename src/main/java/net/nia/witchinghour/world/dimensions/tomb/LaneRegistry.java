package net.nia.witchinghour.world.dimensions.tomb;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.*;

public class LaneRegistry extends PersistentState {

    public static final String KEY = "witching_hour_lanes";

    private final Map<UUID, Integer> playerToLane = new HashMap<>();
    private final Set<UUID> generatedRooms = new HashSet<>();
    private int nextLane = 0;
    private static final Set<UUID> IN_TRIALS = new HashSet<>();
    private static final Map<UUID, TrialRules> RULES = new HashMap<>();

    public static TrialRules getRules(UUID uuid) {
        return RULES.computeIfAbsent(uuid, id -> defaultRules());
    }

    private static TrialRules defaultRules() {
        return new TrialRules()
                .allowInteract(
                        Blocks.LEVER,
                        Blocks.OAK_BUTTON,
                        Blocks.SPRUCE_BUTTON,
                        Blocks.BIG_DRIPLEAF,
                        Blocks.BIG_DRIPLEAF_STEM,
                        Blocks.SPRUCE_TRAPDOOR,
                        Blocks.CHEST,
                        Blocks.TRAPPED_CHEST,
                        Blocks.CRAFTING_TABLE,
                        Blocks.BREWING_STAND,
                        Blocks.BLAST_FURNACE,
                        Blocks.FURNACE,
                        Blocks.SMOKER,
                        Blocks.TNT,
                        Blocks.OAK_FENCE_GATE
                )

                .allowBreak(
                )

                .allowPlace(
                        Blocks.WHITE_WOOL,
                        Blocks.BIG_DRIPLEAF,
                        Blocks.BIG_DRIPLEAF_STEM,
                        Blocks.SPRUCE_BUTTON,
                        Blocks.OAK_BUTTON,
                        Blocks.LEVER
                );
    }

    public static LaneRegistry get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                LaneRegistry::readNbt,
                LaneRegistry::new,
                KEY
        );
    }

    public LaneRegistry() {}

    public static LaneRegistry readNbt(NbtCompound nbt) {
        LaneRegistry reg = new LaneRegistry();

        NbtCompound laneMap = nbt.getCompound("lanes");
        for (String uuidStr : laneMap.getKeys()) {
            int lane = laneMap.getInt(uuidStr);
            reg.playerToLane.put(UUID.fromString(uuidStr), lane);
        }

        NbtList genList = nbt.getList("generatedRooms", NbtElement.STRING_TYPE);
        for (int i = 0; i < genList.size(); i++) {
            reg.generatedRooms.add(UUID.fromString(genList.getString(i)));
        }

        int maxLane = -1;
        for (int lane : reg.playerToLane.values()) {
            if (lane > maxLane) maxLane = lane;
        }

        reg.nextLane = Math.max(nbt.getInt("nextLane"), maxLane + 1);

        return reg;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound laneMap = new NbtCompound();
        for (Map.Entry<UUID, Integer> e : playerToLane.entrySet()) {
            laneMap.putInt(e.getKey().toString(), e.getValue());
        }
        nbt.put("lanes", laneMap);

        NbtList genList = new NbtList();
        for (UUID uuid : generatedRooms) {
            genList.add(NbtString.of(uuid.toString()));
        }
        nbt.put("generatedRooms", genList);

        nbt.putInt("nextLane", nextLane);

        return nbt;
    }

    public boolean hasLane(UUID uuid) {
        return playerToLane.containsKey(uuid);
    }

    public int getLane(UUID uuid) {
        if (!playerToLane.containsKey(uuid)) {
            throw new IllegalStateException("Player has no lane: " + uuid);
        }
        return playerToLane.get(uuid);
    }

    public int assignLane(ServerWorld world, UUID uuid) {

        int lane = nextLane;
        int attempts = 0;

        while (!isLaneAreaClear(world, lane)) {
            lane++;
            attempts++;

            if (attempts > world.getServer().getMaxPlayerCount()) {
                return -1;
            }
        }

        nextLane = lane + 1;
        playerToLane.put(uuid, lane);
        markDirty();

        return lane;
    }

    public boolean hasGeneratedRooms(UUID uuid) {
        return generatedRooms.contains(uuid);
    }

    public void markRoomsGenerated(UUID uuid) {
        generatedRooms.add(uuid);
        markDirty();
    }

    private boolean isLaneAreaClear(ServerWorld world, int lane) {

        int laneX = lane * 80;

        int minX = laneX;
        int maxX    = laneX + 79;

        int minZ = -350;
        int maxZ = 50;

        int minY = world.getBottomY();
        int maxY = world.getTopY();

        for (int x = minX; x <= maxX; x += 8) {
            for (int z = minZ; z <= maxZ; z += 8) {
                for (int y = minY; y <= maxY; y += 16) {

                    BlockPos pos = new BlockPos(x, y, z);

                    if (!world.isAir(pos)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public static void enterTrials(UUID uuid) {
        IN_TRIALS.add(uuid);
    }

    public static void leaveTrials(UUID uuid) {
        IN_TRIALS.remove(uuid);
    }

    public static boolean isInTrials(UUID uuid) {
        return IN_TRIALS.contains(uuid);
    }
}