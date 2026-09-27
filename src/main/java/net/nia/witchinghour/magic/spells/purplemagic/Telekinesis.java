package net.nia.witchinghour.magic.spells.purplemagic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.*;

public class Telekinesis {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Telekinesis";

    private static final int MANA_TICK_RATE = 10;
    private static final double BASE_STRENGTH = 0.05;
    private static final double BLOCK_BASE_STRENGTH = 0.4;

    private static final Map<UUID, List<UUID>> ACTIVE = new HashMap<>();
    private static final Map<UUID, Integer> MANA_TICK = new HashMap<>();
    private static final Map<UUID, Map<UUID, Vec3d>> ENTITY_OFFSETS = new HashMap<>();
    private static final Map<UUID, Map<UUID, Double>> ENTITY_DISTANCES = new HashMap<>();

    private static final Map<UUID, Vec3d> GROUP_CENTER = new HashMap<>();

    public static List<UUID> getTargets(PlayerEntity player) {
        return ACTIVE.getOrDefault(player.getUuid(), Collections.emptyList());
    }

    public static void start(PlayerEntity player, Entity target) {
        if (getTargets(player).contains(target.getUuid())) {
            return;
        }

        UUID playerId = player.getUuid();
        UUID targetId = target.getUuid();

        List<UUID> list = ACTIVE.computeIfAbsent(playerId, k -> new ArrayList<>());
        Map<UUID, Vec3d> offsets = ENTITY_OFFSETS.computeIfAbsent(playerId, k -> new HashMap<>());
        Map<UUID, Double> distances = ENTITY_DISTANCES.computeIfAbsent(playerId, k -> new HashMap<>());

        Vec3d playerPos = player.getEyePos();

        if (list.isEmpty()) {
            GROUP_CENTER.put(playerId, target.getPos());
            offsets.put(targetId, Vec3d.ZERO);
        } else {
            Vec3d center = GROUP_CENTER.get(playerId);
            offsets.put(targetId, target.getPos().subtract(center));
        }

        double distance = target.getPos().subtract(playerPos).length();
        distances.put(targetId, distance);

        if (list.contains(targetId)) {
            return;
        }

        list.add(targetId);
        MANA_TICK.put(playerId, 0);
    }

    public static void stop(PlayerEntity player, boolean placeBlocks) {
        UUID playerId = player.getUuid();

        List<UUID> targets = ACTIVE.remove(playerId);
        MANA_TICK.remove(playerId);
        ENTITY_OFFSETS.remove(playerId);
        ENTITY_DISTANCES.remove(playerId);
        GROUP_CENTER.remove(playerId);

        if (targets == null) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        for (UUID id : targets) {
            Entity target = world.getEntity(id);
            if (target == null) continue;

            target.setNoGravity(false);

            if (target instanceof ServerPlayerEntity sp) {
                sp.getAbilities().flying = false;
                if (!sp.isCreative()) {
                    sp.getAbilities().allowFlying = false;
                }

                player.fallDistance = 0;

                sp.getAbilities().setFlySpeed(0.05f);
                sp.sendAbilitiesUpdate();
            }

            if (placeBlocks && target instanceof FallingBlockEntity falling) {
                BlockState state = falling.getBlockState();
                world.setBlockState(target.getBlockPos(), state, 3);
                falling.discard();
            }
        }
    }

    public static void adjustDistance(PlayerEntity player, double delta) {
        Map<UUID, Double> distances = ENTITY_DISTANCES.get(player.getUuid());
        if (distances == null) return;

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        double maxDistance = Math.max(1, data.getSpellLevel("General")) * 1.5;

        if (maxDistance > 100) {
            maxDistance = 100;
        }

        for (UUID id : distances.keySet()) {
            double newDist = distances.get(id) + delta;
            newDist = Math.max(1.0, Math.min(maxDistance, newDist));
            distances.put(id, newDist);
        }
    }

    private static Vec3d rotateOffset(Vec3d offset, Vec3d look) {
        return null;
    }

    public static void tick(MinecraftServer server) {
        for (UUID uuid : new ArrayList<>(ACTIVE.keySet())) {

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            List<UUID> targets = ACTIVE.get(uuid);

            if (player == null || targets == null || targets.isEmpty()) {
                cleanup(uuid, server);
                continue;
            }

            PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
            float numEntities = Math.max(1, targets.size());

            int timer = MANA_TICK.getOrDefault(uuid, 0) + 1;

            if (timer >= MANA_TICK_RATE) {
                timer = 0;
                int cost = Math.round(numEntities/2);

                if (data.getMana() < 1) {
                    cleanup(uuid, server);
                    continue;
                }

                data.setMana(data.getMana() - cost);
                ManaHelper.applyGeneralExp(data, cost);
            }

            MANA_TICK.put(uuid, timer);

            Vec3d look = player.getRotationVec(1.0F);
            Vec3d basePos = player.getEyePos();

            Map<UUID, Vec3d> offsets = ENTITY_OFFSETS.get(uuid);
            Map<UUID, Double> distances = ENTITY_DISTANCES.get(uuid);

            ServerWorld world = player.getServerWorld();

            for (UUID targetId : new ArrayList<>(targets)) {
                Entity target = world.getEntity(targetId);

                if (target == null || !target.isAlive()) {
                    targets.remove(targetId);
                    continue;
                }

                Vec3d offset = offsets.getOrDefault(targetId, Vec3d.ZERO);

                double maxDistance = Math.max(1, data.getSpellLevel("General"));
                double holdDistance = Math.min(
                        distances.getOrDefault(targetId, 3.0),
                        maxDistance
                );

                Vec3d groupCenter = basePos.add(look.multiply(holdDistance));

                Vec3d holdPos = groupCenter.add(offset);

                Vec3d toTarget = holdPos.subtract(target.getPos());

                double distance = toTarget.length();

                double entityScale = 4.0 / (1.0 + (numEntities - 1) * 0.15);
                double distanceScale = 1.0 / (1.0 + distance * 0.15);

                double strength = BASE_STRENGTH * entityScale;

                if (target instanceof FallingBlockEntity && numEntities - 1 > 2) {
                    strength *= 1.4;
                }

                Vec3d cohesion = groupCenter.subtract(target.getPos()).multiply(0.05);
                toTarget = toTarget.add(cohesion);

                Vec3d currentVel = target.getVelocity();
                Vec3d desiredVel = toTarget.multiply(strength);
                Vec3d newVel = currentVel.multiply(0.7).add(desiredVel.multiply(0.3));
                boolean isSelf = target.getUuid().equals(uuid);

                target.fallDistance = 0;

                if (isSelf) {
                    if (target instanceof ServerPlayerEntity sp) {
                        sp.getAbilities().allowFlying = true;
                        sp.getAbilities().flying = true;

                        sp.getAbilities().setFlySpeed(0.05f/4f);
                        sp.sendAbilitiesUpdate();
                        continue;
                    }
                }

                target.setVelocity(newVel);
                target.velocityDirty = true;

                if (target instanceof PlayerEntity) {
                    target.velocityModified = true;
                }

                if (!target.hasNoGravity()) {
                    target.setNoGravity(true);
                }

                if (target instanceof FallingBlockEntity falling) {
                    Vec3d moveVec = holdPos.subtract(falling.getPos());
                    double blockStrength = BLOCK_BASE_STRENGTH * entityScale * distanceScale;

                    Vec3d blockVel = falling.getVelocity().multiply(0.7)
                            .add(moveVec.multiply(blockStrength * 0.3));

                    falling.setVelocity(blockVel);
                    falling.velocityDirty = true;

                    falling.setNoGravity(true);
                    falling.fallDistance = 0;
                }
            }
        }
    }

    private static void cleanup(UUID uuid, MinecraftServer server) {
        List<UUID> targets = ACTIVE.remove(uuid);
        ENTITY_OFFSETS.remove(uuid);
        ENTITY_DISTANCES.remove(uuid);
        MANA_TICK.remove(uuid);
        GROUP_CENTER.remove(uuid);

        if (targets == null) return;

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
        if (player == null) return;

        ServerWorld world = player.getServerWorld();

        for (UUID id : targets) {
            Entity target = world.getEntity(id);
            if (target != null) {
                target.setNoGravity(false);
            }
        }
    }

    public static void init() {
        SpellLoader.create(
                SPELL,
                ID, new String[]{
                        "lift", "lifted", "lifting", "lifts",
                        "levitate", "levitates", "levitating", "levitated", "levitation", "levity",
                        "telekinesis", "telekinetic"
                },
                new double[]{1.0, 1.0, 1.0, 1.0},
                SpellType.PURPLE_MAGIC,
                new SpellBehavior() {

                    @Override
                    public void onBlockHit(SpellBoltEntity bolt, String spell, BlockHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) return;

                        BlockPos pos = hit.getBlockPos();
                        World world = bolt.getWorld();
                        BlockState state = world.getBlockState(pos);

                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

                        float hardness = state.getHardness(world, pos) * 2;
                        if (data.getSpellLevel("General") < hardness || state.getBlock() == Blocks.BEDROCK || state.isReplaceable()) {
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

                        world.spawnEntity(falling);

                        falling.velocityDirty = true;

                        falling.setNoGravity(true);
                        falling.fallDistance = 0;

                        start(player, falling);

                        bolt.discard();
                    }

                    @Override
                    public void onEntityHit(SpellBoltEntity bolt, String spell, EntityHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) return;

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        start(player, hit.getEntity());
                        bolt.discard();
                    }
                },

                null
        );
    }
}