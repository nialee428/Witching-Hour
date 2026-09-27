package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Melt;

public class MeltSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Melting ✦"));
        state.clearPages();

        state.addPage(Text.literal("The melting spell allows a witch or wizard to hastily melt stone into lava " +
                "or ice into water. Using this spell on blue ice will cause it to become packed ice, then packed ice " +
                "can be melted down into normal ice.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the melting spell requires the following items:
                "4 Lava Buckets,"
                "4 Fire Charges,"
                "1 Block of Stone"
                
                You must be at least level 15 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the melting spell are as follows:
                All forms of the words "Melt," "Molten," and "Disintegrate"
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Melt.ID);
    }

}