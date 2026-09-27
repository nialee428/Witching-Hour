package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Saturation;

public class SaturateSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Saturation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The saturation charm allows a witch to sacrifice a large amount of mana in " +
                "order to convert it into a source of food-like energy. This energy will be consumed by the target and " +
                "turned into food points.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the saturation charm requires the following items:
                "1 Bowl,"
                "4 Dandelions,"
                "4 Blue Orchids,"
                "1 Cooked Rabbit,"
                "4 Cooked Cod,"
                "4 Cooked Beef"
                
                You must be at least level 23 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the saturation charm are as follows:
                All forms of the word "saturate"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Saturation.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}