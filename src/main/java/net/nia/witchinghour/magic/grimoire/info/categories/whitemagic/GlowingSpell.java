package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Glowing;

public class GlowingSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Glowing ✦"));
        state.clearPages();

        state.addPage(Text.literal("The glowing charm causes the target to emit a bright aura around them that " +
                "can be seen through many obstacles.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the glowing charm requires the following items:
                "4 Spectral Arrows,"
                "1 Piece of Redstone Dust,"
                "4 Pieces of Glowstone Dust,"
                
                You must be at least level 17 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the glowing charm are as follows:
                All forms of the word "Glow" and "Shine."
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Glowing.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}