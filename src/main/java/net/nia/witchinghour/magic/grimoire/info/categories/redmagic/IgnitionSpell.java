package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Ignition;

public class IgnitionSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Ignition ✦"));
        state.clearPages();

        state.addPage(Text.literal("The ignition spell lights the target on fire. The fire lacks any special " +
                "properties, but it's still a useful spell nonetheless.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the ignition spell requires the following items:
                "1 Flint and Steel,"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the ignition spell are as follows:
                All forms of the words "Aflame," "Burn," "Fire," "Flame," and "Ignite"
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Ignition.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}