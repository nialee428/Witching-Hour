package net.nia.witchinghour.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public record SoulParticleEffect(float r, float g, float b) implements ParticleEffect {

    public static final ParticleEffect.Factory<SoulParticleEffect> PARAMETERS_FACTORY =
            new ParticleEffect.Factory<SoulParticleEffect>() {
                @Override
                public SoulParticleEffect read(ParticleType<SoulParticleEffect> type, StringReader reader) throws CommandSyntaxException {
                    float r = reader.readFloat();
                    reader.expect(' ');
                    float g = reader.readFloat();
                    reader.expect(' ');
                    float b = reader.readFloat();
                    return new SoulParticleEffect(r, g, b);
                }

                @Override
                public SoulParticleEffect read(ParticleType<SoulParticleEffect> type, PacketByteBuf buf) {
                    return new SoulParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat());
                }
            };

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SOUL;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
    }

    @Override
    public String asString() {
        return r + " " + g + " " + b;
    }
}
