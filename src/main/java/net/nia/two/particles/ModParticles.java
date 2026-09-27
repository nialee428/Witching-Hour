package net.nia.witchinghour.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final ParticleType<SoulParticleEffect> SOUL =
            Registry.register(
                    Registries.PARTICLE_TYPE,
                    new Identifier("witching-hour", "soul"),
                    FabricParticleTypes.complex(SoulParticleEffect.PARAMETERS_FACTORY)
            );

    public static final ParticleType<SmokeParticleEffect> SMOKE =
            Registry.register(
                    Registries.PARTICLE_TYPE,
                    new Identifier("witching-hour", "smoke"),
                    FabricParticleTypes.complex(SmokeParticleEffect.PARAMETERS_FACTORY)
            );

    public static void register() {
        ParticleFactoryRegistry.getInstance().register(
                ModParticles.SOUL,
                SoulParticleFactory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                ModParticles.SMOKE,
                SmokeParticleFactory::new
        );

    }

}
