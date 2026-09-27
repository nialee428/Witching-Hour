package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Revelation;

public class RevelationSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Revelation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The revelation charm allows a witch or wizard to reveal requested information " +
                "about a target. In order to use this spell, you must hold a Book & Quill in your main hand, then specify " +
                "what you would like to reveal. You can reveal the name of another person, their location, the name of " +
                "their coven, any curses or blessings they may currently have, and just about anything else. You can even " +
                "use this charm to reveal other types of information. (DEVELOPER NOTE: THIS SPELL IS STILL IN HEAVY W.I.P. " +
                "SO MANY FEATURES YOU MAY HAVE HEARD ME TALK ABOUT OR READ HERE MAY NOT EXIST OR FUNCTION PROPERLY.)\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the revelation requires the following items:
                "4 Torches,"
                "4 Blocks of Glowstone,"
                "1 Book & Quill"
                
                You must be at least level  in  to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the revelation charm are as follows:
                All forms of the word reveal.
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Revelation.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}