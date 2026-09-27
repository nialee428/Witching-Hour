package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Repair;

public class RepairSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Reparation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The reparation charm allows a witch or wizard to repair certain types of damage done " +
                "to the physical world caused by magic. The most common forms of damage being repaired are from the " +
                "destruction and explosion spells.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the reparation charm requires the following items:
                "1 Enchanted Book (Mending),"
                "1 Potion of Healing,"
                "1 Crafting Table,"
                "1 Anvil,"
                "1 Diamond,"
                
                You must be at least level 15 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the reparation charm are as follows:
                All forms of the words: "Fix," "Mend," "Repair," "Reverse," and "Undo"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Repair.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}