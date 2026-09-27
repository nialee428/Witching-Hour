package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.greenmagic.Harvest;

public class HarvestSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Harvest ✦"));
        state.clearPages();

        state.addPage(Text.literal("The harvest spell allows witches and wizards to quickly harvest plants. Unlike " +
                "the destroy spell, any harvested plants will be safely preserved and automatically brought to you." +
                "\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the harvest spell requires the following items:
                "1 Pair of Shears,"
                "1 Iron Hoe,"
                
                You must be at least level 5 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the harvest spell are as follows:
                All forms of the word "Harvest"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Harvest.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}