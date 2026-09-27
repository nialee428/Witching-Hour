package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Combustion;

public class CombustionSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Combustion ✦"));
        state.clearPages();

        state.addPage(Text.literal("The combustion spell causes the target to spontaneously explode. The damages " +
                "done from this spell can be repaired by the repairing spell, but it still proves to be a problematic " +
                "mess to deal with.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the combustion spell requires the following items:
                "4 Blocks of TNT,"
                "4 Pieces of Gunpowder,"
                "1 Flint and Steel"
                
                You must be at least level 20 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the combustion spell are as follows:
                All forms of the words "Blast," "Burst," "Combust," "Detonate," and "Explode"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Combustion.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}