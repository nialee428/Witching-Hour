package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class PlantSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Planting ✦"));
        state.clearPages();

        state.addPage(Text.literal("The planting spell allows a witch or wizard to quickly plant any crop they are " +
                "currently holding. This spell is mostly used for farming, however, creative green witches and wizards " +
                "can find this spell has many more applications than it initially seems.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this spell, so there's no need to craft it.
                
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the plant spell are as follows:
                All forms of the word "Plant"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
    }

}