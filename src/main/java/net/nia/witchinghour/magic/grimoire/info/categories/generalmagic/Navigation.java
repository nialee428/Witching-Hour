package net.nia.witchinghour.magic.grimoire.info.categories.generalmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.categories.blackmagic.BlackMagicInformation;
import net.nia.witchinghour.magic.grimoire.info.categories.blackmagic.WeaknessSpell;

public class Navigation {

    private static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("black magic")) {
            BlackMagicInformation.display(book, player);
        } else if (command.contains("weakness spell") || command.contains("weakness hex")) {
            if (craft) {
                WeaknessSpell.craft(book, player);
                return;
            }
            WeaknessSpell.display(book, player);
        }

    }

}
