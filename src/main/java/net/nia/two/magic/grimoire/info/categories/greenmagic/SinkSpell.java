package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.greenmagic.Sink;

public class SinkSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Sink ✦"));
        state.clearPages();

        state.addPage(Text.literal("The sinking spell sinks the target into the ground. The target must be above " +
                "actual earth for this spell to work. If they're standing above anything besides earth, the spell will " +
                "not work. Furthermore, they must be close enough to the ground and have enough room below them to be " +
                "sank into. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the sinking spell requires the following items:
                "8 Blocks of Soul Sand,"
                "1 Block of Cobbled Deepslate"
                
                You must be at least level 20 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the sink spell are as follows:
                All forms of the words "Sink" and "Bury"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Sink.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}