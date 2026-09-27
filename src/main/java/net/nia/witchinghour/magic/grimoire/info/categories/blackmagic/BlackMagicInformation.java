package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class BlackMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Black Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("Black magic is an inherently malicious form of magic. Where white magic seeks" +
                "to cleanse, black magic seeks to corrupt. Black magic spells often harm and corrupt the target. Many " +
                "black magic spells manifest in the form of curses and hexes. These two forms of spells often function " +
                "differently from your standard spells. Curses often manifest over time, acting as a sort of slow burn. " +
                "Curses will ramp up in strength over time, and are very difficult to break. In contrast, hexes work " +
                "more closely to standard spells, however, they typically apply an over time effect as well and are " +
                "easier to dispel.\nPG: 1 of 1"));
    }

}
