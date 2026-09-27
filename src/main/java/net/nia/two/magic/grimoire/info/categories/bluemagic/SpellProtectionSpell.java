package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;

public class SpellProtectionSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Spell Protection ✦"));
        state.clearPages();

        state.addPage(Text.literal("This protection spell allows witches and wizards to avoid being affected by " +
                "spells by using items as a sort of talisman. When successfully protecting you from a spell, the item " +
                "will be either consumed or damaged. Some spells are stronger than others, requiring more talismans " +
                "blocking the spell to be held or to have sufficient durability on an existing talisman. This is a must-" +
                "know spell for any blue witches and wizards." +
                "\n DEVELOPER NOTE: IN THE FUTURE, LIKE SHIELD SPELLS, YOU WILL BE ABLE TO HAVE MORE CONTROL OVER THE WAY" +
                "THE SPELL BLOCKING WORKS AND SPECIFY SPECIFIC PEOPLE WHO CAN BYPASS IT AND SIMILAR CRITERIA" +
                "\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                Crafting the spell protection spell requires the following items:
                "1 Shield",
                "1 Grindstone,"
                "1 set of iron armor."
                "4 Enchanted Books (Protection IV),"
                
                You must be at least level  in  to craft this spell.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the spell protection spell are as follows:
                "Protect,"
                "Protects,"
                "Protected,"
                "Protection,"
                "Protecting,"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(book.getUuid());
        buf.writeString(SpellProtection.ID);

        ClientPlayNetworking.send(WitchingHourNetworking.CRAFT_SPELL_PACKET, buf);
    }

}