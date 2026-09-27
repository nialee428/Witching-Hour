package net.nia.witchinghour.events;

import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class FoolMoon {

    public static int FULL_MOON_COUNT = 0;
    public static boolean isFull = false;
    public static boolean isFool = false;
    private static int ambienceTimer = 0;
    private static int dropTimer = 0;

    public static void toggle(ServerPlayerEntity player, boolean force) {
        World world = player.getWorld();

        if (force) {
            if (isFool) {
                FULL_MOON_COUNT = 0;
                isFool = false;
                announce(world.getServer(), "§4☽ The Fool Moon rests... §r");
            } else {
                FULL_MOON_COUNT = 3;
                isFool = true;
                announce(world.getServer(), "§4☽ The Fool Moon rises... §r");
                dropTimer = 1200 + player.getRandom().nextInt(6000);
            }
            return;
        }

        if (!isFool) {
            FULL_MOON_COUNT = 3;
            isFool = true;
            announce(world.getServer(), "§4☽ The Fool Moon rises... §r");
            ambienceTimer = 120 + player.getRandom().nextInt(2400);
            dropTimer = 1200 + player.getRandom().nextInt(6000);
        } else {
            FULL_MOON_COUNT = 0;
            isFool = false;
            announce(world.getServer(), "§4☽ The Fool Moon rests... §r");
        }
    }

    private static void announce(MinecraftServer server, String message) {
        server.getPlayerManager().broadcast(Text.literal(message), false);
    }

    public static void createParticles(ServerPlayerEntity p) {
        ServerWorld world = p.getServerWorld();

        for (ServerPlayerEntity player : world.getPlayers()) {
            Vec3d pos = player.getPos();
            for (int i = 0; i < 2; i++) {
                double dx = world.random.nextGaussian() * 0.3;
                double dy = world.random.nextDouble() - 0.15;
                double dz = world.random.nextGaussian() * 0.3;

                world.spawnParticles(
                        new DustParticleEffect(new Vector3f(1.0f, 0.1f, 0.1f), 1.0f),
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, 0, 0, 0, 0
                );
            }
        }
    }

    private static void playCreepySound(ServerPlayerEntity player) {
        if (ambienceTimer > 0) {
            ambienceTimer--;
            return;
        }

        // 1. Roll chance to even attempt a sound
        if (player.getRandom().nextFloat() > 0.02f) { // 2% chance
            return;
        }

        // 2. Reset timer to a random delay
        ambienceTimer = 120 + player.getRandom().nextInt(2400);

        World world = player.getWorld();

        SoundEvent[] scarySounds = {
                SoundEvents.AMBIENT_CAVE.value(),
                SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK,
                SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
                SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE,
                SoundEvents.ENTITY_PHANTOM_SWOOP,
                SoundEvents.ENTITY_ENDERMAN_STARE,
                SoundEvents.ENTITY_GHAST_SCREAM,
                SoundEvents.ENTITY_GHAST_WARN,
                SoundEvents.ENTITY_WITHER_AMBIENT,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundEvents.ENTITY_WITHER_SPAWN,
                SoundEvents.ENTITY_WITHER_HURT,
                SoundEvents.ENTITY_WITHER_DEATH,
                SoundEvents.ENTITY_WITHER_SHOOT,
                SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
                SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                SoundEvents.BLOCK_END_PORTAL_SPAWN
        };

        SoundEvent chosen = scarySounds[player.getRandom().nextInt(scarySounds.length)];

        float volume = 0.6f + player.getRandom().nextFloat() * 0.4f;
        float pitch  = 0.6f + player.getRandom().nextFloat() * 0.4f;

        double dx = (player.getRandom().nextDouble() - 0.5) * 8;
        double dz = (player.getRandom().nextDouble() - 0.5) * 8;
        double dy = player.getRandom().nextDouble() * 4;

        world.playSound(
                null,
                player.getX() + dx,
                player.getY() + dy,
                player.getZ() + dz,
                chosen,
                SoundCategory.MASTER,
                volume,
                pitch
        );
    }

    private static void dropCurrentItem(ServerPlayerEntity player) {
        if (dropTimer > 0) {
            dropTimer--;
            return;
        }

        // 1. Roll chance to even attempt a sound
        if (player.getRandom().nextFloat() > 0.02f) { // 2% chance
            return;
        }

        // 2. Reset timer to a random delay
        dropTimer = 1200 + player.getRandom().nextInt(6000);

        player.dropSelectedItem(true);
        player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY); // bugfix: no more glitched item dropping
    }

    private static long lastProcessedDay = -1;

    public static void update(ServerPlayerEntity player) {
        World world = player.getWorld();

        long day = world.getTimeOfDay() / 24000;
        int phase = world.getMoonPhase();

        // Process only once per Minecraft day
        if (day != lastProcessedDay) {
            lastProcessedDay = day;

            // ---------- FULL MOON ----------
            if (phase == 0) {
                FULL_MOON_COUNT++;

                System.out.println("[FoolMoon] Full Moon #" + FULL_MOON_COUNT);

                if (FULL_MOON_COUNT >= 3 && !isFool) {
                    toggle(player, false);
                }
            }

            // ---------- NEW MOON ----------
            if (phase == 4 && isFool) {
                toggle(player, false);

                FULL_MOON_COUNT = 0;

                System.out.println("[FoolMoon] Cycle Reset");
            }
        }

        // ---------- ACTIVE EFFECTS ----------
        if (isFool) {
            createParticles(player);
            playCreepySound(player);
            dropCurrentItem(player);
        }
    }

}
