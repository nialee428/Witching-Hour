package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.magic.spells.other.SpellContainer;
import net.nia.witchinghour.particles.SmokeParticleEffect;
import net.nia.witchinghour.particles.SoulParticleEffect;

import java.util.List;

public class ShieldHelper {

    public static void update(MinecraftClient client) {
        if (client == null) {
            return;
        }

        double PULSE_RADIUS = 1000; // blocks
        double PULSE_RADIUS_SQ = PULSE_RADIUS * PULSE_RADIUS;

        PlayerEntity player = client.player;
        if (player == null) return;

        World world = player.getWorld();

        int tick = (int)(world.getTime() % 100000);

        // Get all SpellContainers in a reasonable area
        List<SpellContainer> containers = world.getEntitiesByClass(
                SpellContainer.class,
                new Box(
                        player.getX() - PULSE_RADIUS, player.getY() - PULSE_RADIUS, player.getZ() - PULSE_RADIUS,
                        player.getX() + PULSE_RADIUS, player.getY() + PULSE_RADIUS, player.getZ() + PULSE_RADIUS
                ),
                e -> true
        );

        for (SpellContainer c : containers) {
            EntityMagicData data = ModComponents.ENTITY_MAGIC.get(c);
            String shieldState = data.get("Blue Magic: SHIELD");

            // Skip inactive shields
            if (shieldState == null || !shieldState.startsWith("ACTIVE_SHIELD")) continue;

            // Only animate if within range of the player
            double distSq = c.squaredDistanceTo(player);
            if (distSq > PULSE_RADIUS_SQ) continue;

            // Call the pulsate method, centered from the shield itself
            pulsateShield(c, tick);
        }
    }

    public static void pulsateShield(SpellContainer shield, int tick) {
        EntityMagicData data = ModComponents.ENTITY_MAGIC.get(shield);
        List<NbtCompound> storedBlocks = data.getStoredBlocks();

        if (storedBlocks.isEmpty()) return;

        World world = shield.getWorld();
        if (!world.isClient) {
            return;
        }

        // center calc (same as before)
        Vec3d center = Vec3d.ZERO;
        for (NbtCompound nbt : storedBlocks) {
            center = center.add(
                    nbt.getInt("X"),
                    nbt.getInt("Y"),
                    nbt.getInt("Z")
            );
        }
        center = center.multiply(1.0 / storedBlocks.size());

        double time = tick * 0.2;
        boolean lastSparkled = false;

        for (NbtCompound nbt : storedBlocks) {

            BlockPos pos = new BlockPos(
                    nbt.getInt("X"),
                    nbt.getInt("Y"),
                    nbt.getInt("Z")
            );

            if (!world.getBlockState(pos).isAir()) continue;
            if (world.random.nextFloat() > 0.01f/30 || lastSparkled) {
                lastSparkled = false;
                continue;
            }

            lastSparkled = true;
            double dx = pos.getX() + 0.5 - center.x;
            double dz = pos.getZ() + 0.5 - center.z;

            double dist = Math.sqrt(dx * dx + dz * dz);
            double angle = Math.atan2(dz, dx) + time;

            double radius = dist + Math.sin(time + dist * 0.5) * 0.5;

            double ox = Math.cos(angle) * radius * 0.2;
            double oz = Math.sin(angle) * radius * 0.2;
            double oy = Math.sin(time + dist) * 0.2;

            float r = 0.4f, g = 0.7f, b = 1.0f;

            world.addParticle(
                    new SmokeParticleEffect(r, g, b),
                    pos.getX() + 0.5 + ox,
                    pos.getY() + 0.5 + oy,
                    pos.getZ() + 0.5 + oz,
                    0, 0, 0
            );

            world.addParticle(
                    new SoulParticleEffect(r, g, b),
                    pos.getX() + 0.5 - ox,
                    pos.getY() + 0.5 - oy,
                    pos.getZ() + 0.5 - oz,
                    0, 0, 0
            );
        }
    }
}