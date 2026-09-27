package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Nausea;

public class NauseaSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Nauseation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The nauseation hex inflicts the target with a constant dizziness. This spell " +
                "is useful for messing with another witches aim.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the nauseation hex requires the following items:
                "1 Bowl,"
                "1 Piece of Rotten Flesh,"
                "4 Pufferfish,"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the nauseation hex are as follows:
                All forms of the word "Nauseate"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Nausea.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}