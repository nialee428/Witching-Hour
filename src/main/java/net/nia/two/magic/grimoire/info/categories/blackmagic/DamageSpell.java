package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class DamageSpell {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Hexes ~ Damage ✦"));
        state.clearPages();

        state.addPage(Text.literal("The damaging hex is the most basic form of a black magic spell. This hex " +
                "simply inflicts a small amount of pain upon the target.\nPG: 1 of 3"));

        state.addPage(Text.literal("""
                All witches instinctually know how to use this hex, so there's no need to craft it.
                PG: 2 of 3"""));

        state.addPage(Text.literal("""
                The keywords for the damage hex are as follows:
                All forms of the words "Damage" and "Hurt"
                
                PG: 3 of 3"""));
    }

    public static void craft(GrimoireEntity book, PlayerEntity player) {
    }

}