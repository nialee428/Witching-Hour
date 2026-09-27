package net.nia.witchinghour.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public record SmokeParticleEffect(float r, float g, float b) implements ParticleEffect {

    public static final Factory<SmokeParticleEffect> PARAMETERS_FACTORY =
            new Factory<SmokeParticleEffect>() {
                @Override
                public SmokeParticleEffect read(ParticleType<SmokeParticleEffect> type, StringReader reader) throws CommandSyntaxException {
                    float r = reader.readFloat();
                    reader.expect(' ');
                    float g = reader.readFloat();
                    reader.expect(' ');
                    float b = reader.readFloat();
                    return new SmokeParticleEffect(r, g, b);
                }

                @Override
                public SmokeParticleEffect read(ParticleType<SmokeParticleEffect> type, PacketByteBuf buf) {
                    return new SmokeParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat());
                }
            };

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SMOKE;
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
