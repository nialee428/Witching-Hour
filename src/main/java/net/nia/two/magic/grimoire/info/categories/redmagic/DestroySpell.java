package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class DestroySpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Spells ~ Destroy ✦"));
        state.clearPages();

        state.addPage(Text.literal("The destruction spell is one of the rawest forms of red magic. This spell can " +
                "be used to destroy virtually any block. Weaker witches and wizards may not be able to destroy tougher " +
                "blocks, but as they grow stronger they will gain the ability to break more blocks.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this spell, so there's no need to craft it.
                
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the destruction spell are as follows:
                All forms of the word "Break" and "Destroy"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
    }

}