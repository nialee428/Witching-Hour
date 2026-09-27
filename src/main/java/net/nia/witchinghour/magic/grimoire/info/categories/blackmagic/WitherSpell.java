package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Wither;

public class WitherSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Wither ✦"));
        state.clearPages();

        state.addPage(Text.literal("The withering hex causes the target to rapidly begin withering away.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the wither hex requires the following items:
                "1 Bowl,"
                "1 Potion of Poison,"
                "1 Brown Mushroom,"
                "1 Wither Skeleton Skull,"
                "4 Pieces of Rotten Flesh,"
                "4 Wither Roses"
                
                You must be at least level 35 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the withering hex are as follows:
                All forms of the word "Wither."
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Wither.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}