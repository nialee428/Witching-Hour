package net.nia.witchinghour;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.create.SpellServer;
import net.nia.witchinghour.magic.spells.other.Spell;
import net.nia.witchinghour.magic.spells.other.SpellBoltEntity;
import net.nia.witchinghour.magic.spells.other.SpellRecipe;
import net.nia.witchinghour.magic.spells.other.SpellRegistry;
import net.nia.witchinghour.magic.spells.purplemagic.Telekinesis;
import net.nia.witchinghour.magic.spells.purplemagic.TelekinesisControl;

import java.util.List;
import java.util.UUID;

public class WitchingHourNetworking {
    public static final Identifier CAST_SPELL_PACKET =
            new Identifier(WitchingHour.MOD_ID, "cast_spell");
    public static final Identifier SNAP_PACKET =
            new Identifier(WitchingHour.MOD_ID, "snap");
    public static final Identifier CRAFT_SPELL_PACKET =
            new Identifier(WitchingHour.MOD_ID, "craft_spell");

    public static final Identifier FOLLOW_TOGGLE =
            new Identifier(WitchingHour.MOD_ID, "follow_toggle");
    public static final Identifier TELEKINESIS_CONTROL =
            new Identifier(WitchingHour.MOD_ID, "telekinesis_control");


    public static void sendTelekinesisControlPacket(TelekinesisControl action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(action);

        ClientPlayNetworking.send(TELEKINESIS_CONTROL, buf);
    }

    public static void sendFollowTogglePacket(int entityId, boolean follow) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeBoolean(follow);

        ClientPlayNetworking.send(FOLLOW_TOGGLE, buf);
    }

    public static void sendSnapPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(SNAP_PACKET, buf);
    }


    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                WitchingHourNetworking.CAST_SPELL_PACKET,
                (server, player, handler, buf, responseSender) -> {

                    String spell = buf.readString();
                    int power = buf.readInt();
                    Vec3d direction = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

                    server.execute(() -> {
                        SpellServer.createSpell(player, spell, power, direction);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(FOLLOW_TOGGLE, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            boolean follow = buf.readBoolean();

            server.execute(() -> {
                var entity = player.getWorld().getEntityById(id);
                if (entity instanceof GrimoireEntity book) {

                    // Only the owner can command it
                    if (book.isTamed() && book.getOwnerUuid().equals(player.getUuid())) {
                        book.setShouldFollow(follow);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(
                WitchingHourNetworking.TELEKINESIS_CONTROL,
                (server, player, handler, buf, responseSender) -> {

                    TelekinesisControl action = buf.readEnumConstant(TelekinesisControl.class);

                    server.execute(() -> {
                        List<UUID> targets = Telekinesis.getTargets(player);
                        if (targets == null || targets.isEmpty()) return;

                        switch (action) {

                            case PUSH -> {
                                // increase distance of all entities
                                Telekinesis.adjustDistance(player, 0.5); // 0.5 blocks forward
                            }

                            case PULL -> {
                                // decrease distance of all entities
                                Telekinesis.adjustDistance(player, -0.5); // 0.5 blocks closer
                            }

                            case RELEASE -> {
                                // release all entities
                                Telekinesis.stop(player, false);
                            }

                            case PLACE -> {
                                // release all entities
                                Telekinesis.stop(player, true);
                            }
                        }
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(SNAP_PACKET, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                SpellBoltEntity.popAll(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CRAFT_SPELL_PACKET, (server, player, handler, buf, responseSender) -> {
            UUID bookId = buf.readUuid();
            String spellId = buf.readString();

            server.execute(() -> {
                ServerWorld world = player.getServerWorld();
                Entity e = world.getEntity(bookId);

                if (e instanceof GrimoireEntity book) {
                    Spell spell = SpellRegistry.getFromId(spellId);
                    SpellRecipe.craft(book, player, world, spell);
                }
            });
        });

    }

}