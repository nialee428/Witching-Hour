package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Lightning;

public class LightningSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Lightning ✦"));
        state.clearPages();

        state.addPage(Text.literal("The lightning spell strikes the target with lightning. While expensive, it's " +
                "an extremely powerful spell that should be used cautiously, but any witch or wizard learning this spell " +
                "may not be seeking such a thing." +
                "g.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the  requires the following items:
                "4 Lightning Rods,"
                "4 Enchanted Books (Channeling),"
                "1 Trident,"
                
                You must be at least level 35 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the lightning spell are as follows:
                "Lightning,"
                "smite,"
                "smitten,"
                "smiting,"
                "smit,"
                "smote,"
                "smites,"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Lightning.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}