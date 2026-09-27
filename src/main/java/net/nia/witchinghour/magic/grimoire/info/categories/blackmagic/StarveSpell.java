package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Starve;

public class StarveSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Starvation ✦"));
        state.clearPages();

        state.addPage(Text.literal("The starvation hex inflicts the target with a strong hunger. This hex is the " +
                "opposite of the Saturate charm. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the starvation hex requires the following items:
                "1 Bowl,"
                "4 Pieces of Rotten Flesh,"
                "4 Lilies of the Valley"
                
                You must be at least level 23 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the starvation hex are as follows:
                All forms of the word "starve"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Starve.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}