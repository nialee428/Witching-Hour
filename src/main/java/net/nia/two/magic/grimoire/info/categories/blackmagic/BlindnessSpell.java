package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Blindness;

public class BlindnessSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Blindness ✦"));
        state.clearPages();

        state.addPage(Text.literal("The blindness hex inflicts the target with heavy blindness. This hex was " +
                "historically used by many witches and wizards as a means to scare and/or sneak up on their " +
                "enemies.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the blindness hex requires the following items:
                "1 Bowl,"
                "1 Water Bottle,"
                "1 Brown Mushroom,"
                "2 Ink Sacs,"
                "4 Azure Bluets,"
                
                You must be at least level 25 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the blindness hex are as follows:
                All forms of the word "Blindness"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Blindness.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}