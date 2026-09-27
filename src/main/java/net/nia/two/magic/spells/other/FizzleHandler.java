package net.nia.witchinghour.magic.spells.other;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class FizzleHandler {

    public static boolean
    fizzleChance(int pLevel, int sLevel, double[] sData) {
        double minLvl = sData[1];

        // Hard gate: below min level, always fizzle
        if (pLevel < minLvl) {
            return true;
        }
        double k = 0.17;
        double fizzleChance = 0.01 + 0.98 * Math.exp(-k * sLevel);

        if (sLevel >= 30) {
            fizzleChance = 0.01;
        }

        return Math.random() < fizzleChance;
    }

    public static void spawnFizzleCloud(World world, Vector3f color, double x, double y, double z) {
        if (!world.isClient) return;

        // Extract original color
        float r = color.x;
        float g = color.y;
        float b = color.z;

        // Brighten or darken each channel independently
        r = (r < 0.5f) ? Math.min(r * 1.5f, 1f) : r * 0.5f;
        g = (g < 0.5f) ? Math.min(g * 1.5f, 1f) : g * 0.5f;
        b = (b < 0.5f) ? Math.min(b * 1.5f, 1f) : b * 0.5f;

        Vector3f adjusted = new Vector3f(r, g, b);

        for (int i = 0; i < 20; i++) {
            double dx = (world.random.nextDouble() - 0.5) * 0.2;
            double dy = (world.random.nextDouble() - 0.5) * 0.2;
            double dz = (world.random.nextDouble() - 0.5) * 0.2;

            world.addParticle(
                    new DustParticleEffect(adjusted, 2.75f),
                    x, y, z,
                    0,0,0
            );
        }
    }

}