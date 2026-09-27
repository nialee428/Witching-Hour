package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Cleanse;

public class CleanseSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Cleansing ✦"));
        state.clearPages();

        state.addPage(Text.literal("The cleansing charm allows a witch or wizard to hastily dispel all standard " +
                "status effects placed upon a target. For example, if you were suddenly poisoned by a potion or the " +
                "poison hex, this charm would quickly remove the effect! It's important to note that this charm will " +
                "also clear any positive status effects, so be careful!\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the cleansing charm requires the following items:
                "4 Buckets of Milk,"
                "1 Potion of Grindstone,"
                "1 Potion of Weakness,"
                "1 Potion of Slowness,"
                "1 Potion of Harming,"
                "1 Potion of Poison,"
                
                You must be at least level 10 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the cleansing charm are as follows:
                "Cleanse,"
                "Cleansed,"
                "Cleanser,"
                "Cleansing,"
                "Dispel,"
                "Dispelled,"
                "Dispeller,"
                "Dispelling"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Cleanse.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}