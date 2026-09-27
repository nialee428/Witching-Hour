package net.nia.witchinghour.magic.grimoire.info.categories.magicalobjects;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.misc.EmptySpell;

public class Runes {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Magical Objects ~ Runes ✦"));
        state.clearPages();

        state.addPage(Text.literal("\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the  requires the following items:
                All witches instinctually know how to use this spell, so there's no need to craft it.
                ""
                
                You must be at least level  in  to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the  are as follows:
                ","
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(EmptySpell.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}