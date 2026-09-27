package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class BlueMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Blue Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("Blue magic is the manifestation of protective energies. Often representing " +
                "fortitude, protection, and stability. Blue magic is also an elemental magic. It represents water in all " +
                "of its forms. Those experienced in blue magic are often difficult for most witches and wizards to deal " +
                "with due to their immense defensive capabilities. Blue magic most often takes shape as water, ice, or " +
                "other forces of cold and warding, manifesting as protective tides and freezing energies.\nPG: 1 of 1"));
    }

}
