package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.bluemagic.Extinguish;

public class ExtinguishSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Extinguish ✦"));
        state.clearPages();

        state.addPage(Text.literal("The extinguish spell allows witches and wizards to quickly extinguish flames " +
                "using magic. This makes it especially useful in saving anything that may be burning.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the  requires the following items:
                "4 Water Bottles,"
                "1 Powder Snow Bucket"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the extinguish spell are as follows:
                All forms of the words "Extinguish" and "Snuff"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Extinguish.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}