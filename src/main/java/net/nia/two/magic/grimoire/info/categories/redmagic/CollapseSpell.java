package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.redmagic.Collapse;

public class CollapseSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Collapse ✦"));
        state.clearPages();

        state.addPage(Text.literal("The collapse spell causes the targeted blocks to suddenly collapse. This spell " +
                "is useful for trapping enemies in enclosed spaces such as caves- a space in which red witches are already " +
                "lethal in.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the collapse spell requires the following items:
                "4 Blocks of Sand,"
                "4 Blocks of Gravel,"
                "1 Anvil"
                
                You must be at least level 10 in General Magic to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the collapse spell are as follows:
                All forms of the words "Collapse," "Plummet," and "Crumble"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Collapse.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}