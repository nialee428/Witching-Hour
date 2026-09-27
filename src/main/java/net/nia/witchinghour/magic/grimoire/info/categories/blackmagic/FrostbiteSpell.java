package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Frostbite;

public class FrostbiteSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Frostbite ✦"));
        state.clearPages();

        state.addPage(Text.literal("The frostbite hex inflicts severe frostbite upon the target for a while. The " +
                "origins of this hex are unknown, but it's believed this hex was drawn from the art of blue magic. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the frostbite hex requires the following items:
                "1 Powder Snow Bucket,"
                "8 Blocks of Blue Ice"
                
                You must be at least level 21 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the frostbite hex are as follows:
                "Frostbite"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Frostbite.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}