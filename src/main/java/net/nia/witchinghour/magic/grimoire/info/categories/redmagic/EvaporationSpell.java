package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Evaporate;

public class EvaporationSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Evaporation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The evaporation spell allows a witch or wizard to hastily evaporate a body of " +
                "water. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the melting spell requires the following items:
                "1 Water Bucket,"
                "8 Fire Charges"
                
                You must be at least level 7 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the melting spell are as follows:
                All forms of the words "Evaporate," "Dry," and "Vaporize"
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Evaporate.ID);
    }

}