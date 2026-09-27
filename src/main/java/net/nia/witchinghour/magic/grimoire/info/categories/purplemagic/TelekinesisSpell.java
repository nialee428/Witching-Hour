package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class TelekinesisSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Telekinesis ✦"));
        state.clearPages();

        state.addPage(Text.literal("The telekinesis spell is one of the most basic, purest forms of purple magic. " +
                "This spell allows witches and wizards to levitate blocks, creatures, and even items.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this spell, so there's no need to craft it.
                
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the telekinesis spell are as follows:
                All forms of the words "Levitate," "Lift," and "Telekinesis"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
    }

}