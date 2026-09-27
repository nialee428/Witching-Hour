package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.bluemagic.Resistance;

public class ResistanceSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Resistance ✦"));
        state.clearPages();

        state.addPage(Text.literal("The resistance spell enhances the targets resistance. Many witches and wizards " +
                "have historically used this spell to survive various witch hunts and fool humans into believing they " +
                "lived through divine will.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the resistance spell requires the following items:
                "1 Potion of the Turtle Master,"
                "1 Shield,"
                "1 Enchanted Book (Protection IV)"
                
                You must be at least level 10 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the resistance spell are as follows:
                "Resistance,"
                "Resistant,"
                "Durable,"
                "Tough,"
                "Toughen,"
                "Tougher"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Resistance.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}