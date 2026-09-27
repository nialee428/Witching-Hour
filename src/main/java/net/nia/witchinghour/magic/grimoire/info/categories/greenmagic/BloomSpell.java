package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.greenmagic.Bloom;

public class BloomSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Bloom ✦"));
        state.clearPages();

        state.addPage(Text.literal("The bloom spell causes plants to bloom in the surrounding area, making this " +
                "spell useful for farming. many green witches and wizards use this spell alongside other spells for " +
                "various purposes. \nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the bloom spell requires the following items:
                "8 Pieces of Bonemeal,"
                "1 Bottle of Honey"
                
                You must be at least level 15 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the bloom spell are as follows:
                All forms of the words "Bloom," "Sprout," and "Flourish"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Bloom.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}