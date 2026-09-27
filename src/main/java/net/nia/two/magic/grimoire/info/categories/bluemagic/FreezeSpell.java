package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.bluemagic.Freeze;

public class FreezeSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Freezing ✦"));
        state.clearPages();

        state.addPage(Text.literal("The freezing spell allows a witch or wizard to hastily freeze water into snow " +
                "or ice. Using this spell on ice will cause it to become packed ice, and then packed ice can become blue " +
                "ice.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the freezing spell requires the following items:
                "8 Blocks of Ice,"
                "4 Blocks of Packed Ice,"
                "2 Blocks of Blue Ice,"
                "1 Water Bucket"
                
                You must be at least level 15 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the freezing spell are as follows:
                All forms of the word "Freeze"
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Freeze.ID);
    }

}