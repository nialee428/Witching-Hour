package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Gust;

public class GustSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Gust ✦"));
        state.clearPages();

        state.addPage(Text.literal("The gust charm is one of the most basic wind spells any witch or wizard could " +
                "learn. This charm launches the target upwards. The caster can also specify several directions to launch " +
                "the target in. The directions you can say are: \"toward me,\" \"forward,\" \"back,\" \"left,\" \"right,\"" +
                " and the power of the spell can be amplified by simply saying the word \"far\" or other variants of the " +
                "exact word.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the gust charm requires the following items:
                "1 Sticky Piston,"
                "1 Slime Block"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the gust charm are as follows:
                "Fling,"
                "Flinger,"
                "Flinging,"
                "Flings,"
                "Flung,"
                "Gust,"
                "Gusts,"
                "Launch,"
                "Launched,"
                "Launcher,"
                "Launches,"
                "Launching"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Gust.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}