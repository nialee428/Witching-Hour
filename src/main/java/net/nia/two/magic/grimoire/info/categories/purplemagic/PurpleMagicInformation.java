package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class PurpleMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Purple Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("Purple magic is the manifestation of true magic, essentially meaning standard " +
                "science cannot explain this form of magic. Purple magic spells are often mana intensive but versatile, " +
                "often being applicable in any situation you could possibly wind up in. Those experienced in purple magic " +
                "typically have a deeper connection with the astral plane and the metaphysical.\nPG: 1 of 1"));
    }

}
