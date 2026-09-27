package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.bluemagic.Condensation;

public class CondensationSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Condensation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The condensation spell allows witches and wizards to manifest water at the " +
                "target location. This spell is often used for many of the more creative and expressive elemental blue " +
                "magic spells.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the condensation spell requires the following items:
                "1 Cauldron,"
                "4 Water Buckets"
                
                You must be at least level 20 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the condensation spell are as follows:
                All forms of the words "Condensation," "Soak," "Douse," and "Drench"
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Condensation.ID);
    }

}