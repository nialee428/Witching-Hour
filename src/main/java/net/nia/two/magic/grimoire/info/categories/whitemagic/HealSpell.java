package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Heal;

public class HealSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Charms ~ Healing ✦"));
        state.clearPages();

        state.addPage(Text.literal("The healing charm is one of the purest forms of white magic. This charm heals " +
                "the target for exactly one heart.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this spell, so there's no need to craft it.
                
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the healing charm are as follows:
                "Heal,"
                "Heals,"
                "Healed,"
                "Healing,"
                "Healer,"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(Heal.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}