package net.nia.witchinghour.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nia.witchinghour.Scheduler;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EntityMagicData implements Component, AutoSyncedComponent {

    private final Entity entity;
    private final Map<String, String> stringData = new HashMap<>();
    private final List<NbtCompound> storedBlocks = new ArrayList<>();
    private final Map<String, String> curse = new HashMap<>();

    public EntityMagicData(Entity entity) {
        this.entity = entity;
    }

    // --- String data ---
    public String get(String key) {
        return stringData.getOrDefault(key, "");
    }

    public void set(String key, String value) {
        stringData.put(key, value);
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    public void remove(String key) {
        stringData.remove(key);
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    // --- Pending invites ---
    public void addPendingInvite(String playerName) {
        List<String> invites = new ArrayList<>();
        stringData.forEach((k, v) -> {
            if (k.startsWith("Pending Invite")) invites.add(v);
        });
        invites.add(playerName);

        stringData.entrySet().removeIf(e -> e.getKey().startsWith("Pending Invite"));
        for (int i = 0; i < invites.size(); i++) {
            stringData.put("Pending Invite" + i, invites.get(i));
        }
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    public boolean hasPendingInvite(String playerName) {
        return stringData.entrySet().stream()
                .anyMatch(e -> e.getKey().startsWith("Pending Invite") && e.getValue().equals(playerName));
    }

    public void removePendingInvite(String playerName) {
        List<String> invites = new ArrayList<>();
        stringData.forEach((k, v) -> {
            if (k.startsWith("Pending Invite")) invites.add(v);
        });
        invites.removeIf(playerName::equals);

        stringData.entrySet().removeIf(e -> e.getKey().startsWith("Pending Invite"));
        for (int i = 0; i < invites.size(); i++) {
            stringData.put("Pending Invite" + i, invites.get(i));
        }
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    // --- Curses ---
    public Set<String> getCurses() {
        return Collections.unmodifiableSet(curse.keySet());
    }

    public String getCurse(String curseName) {
        return curse.getOrDefault(curseName, "");
    }

    public void addCurse(String curseName, int Strength, String caster) {
        if (curse.containsKey(curseName) && !getCurse(curseName).isEmpty()) {
            int strength = Integer.parseInt(getCurse(curseName).split("Strength:")[1]);
            if (strength >= Strength) {
                return;
            }
        }
        String stats = "Caster(" + caster + ")" + "Strength:" + Strength;
        curse.put(curseName, stats);
    }

    public void removeCurse(String curseName) {
        curse.remove(curseName);
    }

    // --- Block storage ---
    public void addBlock(World world, BlockPos pos, boolean ignoreState) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (ignoreState) {
            block = Blocks.AIR;
        }

        NbtCompound blockData = new NbtCompound();

        blockData.putString("Block", Registries.BLOCK.getId(block).toString());

        NbtCompound properties = new NbtCompound();
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
            properties.putString(entry.getKey().getName(), entry.getValue().toString());
        }
        blockData.put("Properties", properties);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockData.put("BlockEntity", blockEntity.createNbt());
        }

        blockData.putInt("X", pos.getX());
        blockData.putInt("Y", pos.getY());
        blockData.putInt("Z", pos.getZ());

        if (storedBlocks.contains(blockData)) {
            return;
        }

        storedBlocks.add(blockData);
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    public List<NbtCompound> getStoredBlocks() {
        return new ArrayList<>(storedBlocks);
    }

    public void removeBlock(int index) {
        if (index >= 0 && index < storedBlocks.size()) {
            storedBlocks.remove(index);
            ModComponents.ENTITY_MAGIC.sync(entity);
        }
    }

    // --- Restore a single block ---
    public void restoreBlock(World world, int index) {
        if (index < 0 || index >= storedBlocks.size()) return;

        NbtCompound blockData = storedBlocks.get(index);
        BlockPos pos = new BlockPos(
                blockData.getInt("X"),
                blockData.getInt("Y"),
                blockData.getInt("Z")
        );

        Block block = Registries.BLOCK.get(new Identifier(blockData.getString("Block")));
        if (block == null) return;

        AtomicReference<BlockState> state = new AtomicReference<>(block.getDefaultState());

        if (blockData.contains("Properties")) {
            NbtCompound properties = blockData.getCompound("Properties");
            for (String key : properties.getKeys()) {
                Property<?> property = state.get().getBlock().getStateManager().getProperty(key);
                if (property != null) {
                    property.parse(properties.getString(key)).ifPresent(value -> {
                        @SuppressWarnings("rawtypes")
                        Property rawProperty = property;
                        state.set(state.get().with(rawProperty, (Comparable) value));
                    });
                }
            }
        }

        world.setBlockState(pos, state.get(), 3);

        world.playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                state.get().getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS,
                1.0f,
                1.0f
        );

        if (blockData.contains("BlockEntity")) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.readNbt(blockData.getCompound("BlockEntity"));
            }
        }

        storedBlocks.remove(index);
        ModComponents.ENTITY_MAGIC.sync(entity);
    }

    private void spawnMagicFragment(
            World world,
            BlockState state,
            BlockPos targetPos,
            Vec3d center,
            Runnable onArrive
    ) {

        final double height = 13.0 + world.random.nextDouble() * 3.0;

        BlockPos spawnPos = BlockPos.ofFloored(center.x, center.y + height, center.z);

        FallingBlockEntity fragment = FallingBlockEntity.spawnFromBlock(world, spawnPos, state);

        fragment.setNoGravity(true);
        fragment.setOnGround(false);
        fragment.dropItem = false;
        fragment.setDestroyedOnLanding();

        world.spawnEntity(fragment);

        double dx = (targetPos.getX() + 0.5) - spawnPos.getX();
        double dy = (targetPos.getY() + 0.5) - spawnPos.getY();
        double dz = (targetPos.getZ() + 0.5) - spawnPos.getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double avgSpeed = 1;
        int totalTicks = (int)(distance / avgSpeed);
        totalTicks = Math.max(25, Math.min(45, totalTicks));

        final double startAngle = world.random.nextDouble() * Math.PI * 2;

        for (int tick = 0; tick < totalTicks; tick++) {

            int delay = Math.min(39, tick);

            int finalTotalTicks = totalTicks;
            Scheduler.schedule(delay, () -> {
                if (!fragment.isAlive()) return;

                double progress = delay / (double) finalTotalTicks;

                double targetX = targetPos.getX() + 0.5;
                double targetY = targetPos.getY() + 0.5;
                double targetZ = targetPos.getZ() + 0.5;

                double toCenterX = center.x - fragment.getX();
                double toCenterZ = center.z - fragment.getZ();

                double dist = Math.sqrt(toCenterX * toCenterX + toCenterZ * toCenterZ);
                if (dist > 0.001) {
                    toCenterX /= dist;
                    toCenterZ /= dist;
                }

                double tangentX = -toCenterZ;
                double tangentZ = toCenterX;

                double toTargetX = targetX - fragment.getX();
                double toTargetY = targetY - fragment.getY();
                double toTargetZ = targetZ - fragment.getZ();

                double targetDist = Math.sqrt(
                        toTargetX * toTargetX +
                                toTargetY * toTargetY +
                                toTargetZ * toTargetZ
                );

                if (targetDist > 0.001) {
                    toTargetX /= targetDist;
                    toTargetY /= targetDist;
                    toTargetZ /= targetDist;
                }

                double eased = Math.pow(progress, 0.6);

                double spinFade = Math.pow(1.0 - progress, 1.5);
                double spinStrength = 1.1 * spinFade;

                double homingStrength = 0.25 + eased * 0.65;

                double downwardBias = 0.08 * (1.0 - progress);

                double vx =
                        tangentX * spinStrength +
                                toTargetX * homingStrength;

                double vz =
                        tangentZ * spinStrength +
                                toTargetZ * homingStrength;

                double vy =
                        toTargetY * homingStrength -
                                downwardBias;

                double wobbleFade = 1.0 - Math.pow(progress, 2.5); // dies off hard near end
                double angle = startAngle + progress * 50.0;

                vx += Math.sin(angle * 2.0) * 0.02 * wobbleFade;
                vz += Math.cos(angle * 2.0) * 0.02 * wobbleFade;

                if (targetDist < 1.2) {
                    vx *= 0.6;
                    vy *= 0.6;
                    vz *= 0.6;
                }

                fragment.setVelocity(vx, vy, vz);
                fragment.velocityDirty = true;
            });
        }

        Scheduler.schedule(totalTicks - 1, () -> {
            if (fragment.isAlive()) {
                fragment.discard();
            }
        });

        Scheduler.schedule(totalTicks, onArrive);
    }

    public void restoreAllBlocks(World world) {

        storedBlocks.sort(Comparator.comparingInt(nbt -> nbt.getInt("Y")));
        List<NbtCompound> copy = new ArrayList<>(storedBlocks);

        int delay = 0;
        int speed = 2;

        for (int i = 0; i < copy.size(); i++) {

            NbtCompound blockData = copy.get(i);

            BlockPos pos = new BlockPos(
                    blockData.getInt("X"),
                    blockData.getInt("Y"),
                    blockData.getInt("Z")
            );

            if (!world.getBlockState(pos).isAir()) {
                return;
            }

            Block block = Registries.BLOCK.get(new Identifier(blockData.getString("Block")));

            final BlockState[] builtState = {block.getDefaultState()};

            if (blockData.contains("Properties")) {
                NbtCompound properties = blockData.getCompound("Properties");
                for (String key : properties.getKeys()) {
                    Property<?> property = builtState[0].getBlock().getStateManager().getProperty(key);
                    if (property != null) {
                        property.parse(properties.getString(key)).ifPresent(value -> {
                            @SuppressWarnings("rawtypes")
                            Property raw = property;
                            builtState[0] = builtState[0].with(raw, (Comparable) value);
                        });
                    }
                }
            }

            BlockState finalState = builtState[0];
            BlockPos finalPos = pos;
            int indexToRestore = 0;

            delay += speed;

            Scheduler.schedule(delay, () -> {
                if (!storedBlocks.isEmpty()) {
                    spawnMagicFragment(
                            world,
                            finalState,
                            finalPos,
                            entity.getPos(),
                            () -> restoreBlock(world, indexToRestore)
                    );
                }
            });
        }

        Scheduler.schedule(delay + 30, () -> {
            if (storedBlocks.isEmpty()) {
                entity.discard();
            }
        });
    }

    // --- NBT serialization ---
    @Override
    public void readFromNbt(NbtCompound tag) {
        stringData.clear();
        storedBlocks.clear();

        readStringMap(tag.getCompound("curse"), curse);

        NbtCompound strings = tag.getCompound("stringData");
        for (String key : strings.getKeys()) {
            stringData.put(key, strings.getString(key));
        }

        if (tag.contains("storedBlocks")) {
            NbtList list = tag.getList("storedBlocks", 10);
            for (int i = 0; i < list.size(); i++) {
                storedBlocks.add(list.getCompound(i));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound strings = new NbtCompound();
        stringData.forEach(strings::putString);
        tag.put("stringData", strings);

        tag.put("curse", writeStringMap(curse));

        NbtList list = new NbtList();
        storedBlocks.forEach(list::add);
        tag.put("storedBlocks", list);
    }

    private void readStringMap(NbtCompound tag, Map<String, String> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            map.put(key, tag.getString(key));
        }
    }

    private NbtCompound writeStringMap(Map<String, String> map) {
        NbtCompound tag = new NbtCompound();
        for (var entry : map.entrySet()) {
            tag.putString(entry.getKey(), entry.getValue());
        }
        return tag;
    }
}