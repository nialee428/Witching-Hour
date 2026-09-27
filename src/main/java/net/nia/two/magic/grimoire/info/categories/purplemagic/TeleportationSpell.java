package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.purplemagic.Teleportation;

public class TeleportationSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Teleportation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The teleportation spell is one of the most useful and versatile utility spells. " +
                "This spell is often very frequently used by many witches and wizards. You can teleport to the target, " +
                "to another person using their name, or even to waypoints. You can teleport things around, or even use it " +
                "as a means to quickly escape by taking you or someone else to a random, distant location. This spell has " +
                "a specific format that you must follow, however. You must specify who or what you want want to teleport, " +
                "and then to where. For example, \"Teleport me to this\" or \"Teleport Nia Lee to The Tavern.\"" +
                "\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the teleportation requires the following items:
                "4 Ender Pearls,"
                "4 Eyes of Ender,"
                "1 Splash Bottle of Water"
                
                You must be at least level 15 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the teleportation spell are as follows:
                All forms of the words "Teleport," "Transport", "Take", "Bring", "Send"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Teleportation.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}