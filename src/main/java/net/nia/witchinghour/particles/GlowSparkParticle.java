package net.nia.witchinghour.particles;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlowSparkParticle extends SpriteBillboardParticle {

    private static final Logger log = LoggerFactory.getLogger(GlowSparkParticle.class);
    private final SpriteProvider spriteProvider;

    protected GlowSparkParticle(
            ClientWorld world, double x, double y, double z,
            double vx, double vy, double vz,
            float r, float g, float b,
            SpriteProvider spriteProvider
    ) {
        super(world, x, y, z, vx, vy, vz);

        this.spriteProvider = spriteProvider;

        this.red = r;
        this.green = g;
        this.blue = b;

        this.scale = .5f;
        this.maxAge = 18 + world.random.nextInt(6);

        this.setVelocity(this.velocityX/2.5,this.velocityY/2.5,this.velocityZ/2.5);

        this.alpha = 0f;
        this.collidesWithWorld = false;

        // Start on frame 0
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        float life = (float) age / maxAge;

        // Fade in then fade out
        if (life < 0.1f) {
            alpha = life / 0.1f;
        } else {
            alpha = 1f - ((life - 0.1f) / 0.9f);
        }

        // Slight shrink
        scale *= 0.98f;

        // Animate based on age
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tickDelta) {
        return 0xF000F0; // full brightness (same as torches)
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }
}