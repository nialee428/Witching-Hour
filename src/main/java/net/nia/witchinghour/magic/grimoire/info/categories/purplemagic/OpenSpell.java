package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.purplemagic.Open;

public class OpenSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Open ✦"));
        state.clearPages();

        state.addPage(Text.literal("The opening spell allows a witch or wizard to open any inventory from a " +
                "distance. This spell can also be used with waypoints, allowing a witch or wizard to easily open any " +
                "inventory they have created a waypoint to.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the opening spell requires the following items:
                "1 Chest,"
                "1 Hopper"
                "1 Spyglass"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the opening spell are as follows:
                All forms of the word "Open"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Open.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}