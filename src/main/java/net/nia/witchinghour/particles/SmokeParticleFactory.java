package net.nia.witchinghour.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class SmokeParticleFactory implements ParticleFactory<SmokeParticleEffect> {

    private final SpriteProvider sprites;

    public SmokeParticleFactory(SpriteProvider sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SmokeParticleEffect effect, ClientWorld world,
                                   double x, double y, double z,
                                   double vx, double vy, double vz) {

        GlowSparkParticle p = new GlowSparkParticle(
                world, x, y, z, vx, vy, vz,
                effect.r(), effect.g(), effect.b(),
                this.sprites
        );

        return p;
    }
}