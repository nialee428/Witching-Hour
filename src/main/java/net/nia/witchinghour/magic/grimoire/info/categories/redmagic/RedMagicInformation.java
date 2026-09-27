package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class RedMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Red Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("Red magic represents chaos and destruction. Those specialized in this form of " +
                "magic often leave behind devastating amounts of damage and value raw power over controlled strength. " +
                "It's not uncommon for many red magic spells to have potential to be excessively destructive or painful. " +
                "This form of magic also represents the element of fire, with many spells having to do with some form of " +
                "fire or heat.\nPG: 1 of 1"));
    }

}
