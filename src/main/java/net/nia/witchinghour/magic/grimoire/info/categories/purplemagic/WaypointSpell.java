package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.purplemagic.Waypoint;

public class WaypointSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Waypoint ✦"));
        state.clearPages();

        state.addPage(Text.literal("The waypoint spell allows you to manifest a well of magic that only the caster " +
                "can tap into. This waypoint can be used to cast certain spells at its location. It's important to note " +
                "that not all spells can be cast from a waypoint. In order to properly make a waypoint, you must say " +
                "something along the lines of \"... [waypoint keyword] named [waypoint name].\" Be careful about what " +
                "is said after saying \"named\" as anything after this will be counted as the waypoints name. For example," +
                "one could say \"... make a waypoint named the tavern\"\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the waypoint requires the following items:
                "1 Bed,"
                "1 Respawn Anchor,"
                "1 Eye of Ender"
                
                You must be at least level 10 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the waypoint spell are as follows:
                "Waypoint"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Waypoint.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}