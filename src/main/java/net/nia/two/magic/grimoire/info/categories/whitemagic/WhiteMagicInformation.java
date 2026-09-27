package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class WhiteMagicInformation {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ White Magic Overview ✦"));
        state.clearPages();

        state.addPage(Text.literal("White magic is the polar opposite of black magic. Whereas black magic aims to " +
                "harm and ruin, white magic heals and restores. White magic is known for it's blatantly positive effects. " +
                "Those who specialize in white magic are experts at repairing magical damage and breaking any form of " +
                "ailments. White magic also represents the element of air, allowing white magic users to blow their " +
                "problems away. Furthermore, white magic manifests itself in the form of blessings and charms. These are " +
                "the opposite forces of black magic's curses and hexes.\nPG: 1 of 1"));
    }

}