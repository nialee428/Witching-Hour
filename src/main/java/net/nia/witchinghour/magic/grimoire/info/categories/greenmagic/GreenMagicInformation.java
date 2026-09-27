package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class GreenMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Green Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("Green magic represents the natural world. It is the element of earth. Green " +
                "magic spells often relate to manipulating the physical aspects of the world around you. Green magic users " +
                "can easily manipulate terrain and possess a natural affinity with living creatures. Green magic is " +
                "essentially the most physical form of magic.\nPG: 1 of 1"));
    }

}
