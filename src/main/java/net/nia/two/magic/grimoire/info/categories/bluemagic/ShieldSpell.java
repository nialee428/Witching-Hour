package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class ShieldSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Shield ✦"));
        state.clearPages();

        state.addPage(Text.literal("The shield spell is the foundation of blue magic. Even those not experienced " +
                "in this type of magic are able to create powerful shields. Shields will block all incoming spells from " +
                "anyone besides the person who created the shield. They do not cost mana to maintain." +
                "\n DEVELOPER NOTE: IN THE FUTURE, YOU WILL BE ABLE TO HAVE MORE CONTROL OVER THE WAY THE SPELL BLOCKING " +
                "WORKS AND SPECIFY SPECIFIC PEOPLE WHO CAN BYPASS IT AND SIMILAR CRITERIA" +
                "\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this spell, so there's no need to craft it.
                
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the shield spell are as follows:
                "Shield,"
                "Shields,"
                "Shielded,"
                "Shielding,"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
    }

}