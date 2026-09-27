package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.purplemagic.Scaling;

public class ScalingSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Scaling ✦"));
        state.clearPages();

        state.addPage(Text.literal("The scaling spell allows a witch or wizard to manipulate the size of a creature. " +
                "There are many different ways to scale the target. If you would like to return a creature to their " +
                "original size, simply say \"original [general size keyword]\" somewhere in the spell. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the scaling spell requires the following items:
                "8 Pieces of Bonemeal,"
                "1 Armor Stand,"
                
                You must be at least level 20 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the scaling spell are as follows:
                General Size: All forms of the words "Scale" and "Size"
                Growth: All forms of the words "Grow" and "Enlarge"
                Shrink: All forms of the word "Shrink"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Scaling.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}