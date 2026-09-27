package net.nia.witchinghour.magic.grimoire.info.categories;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.categories.blackmagic.BlackMagicNavigation;
import net.nia.witchinghour.magic.grimoire.info.categories.bluemagic.BlueMagicNavigation;
import net.nia.witchinghour.magic.grimoire.info.categories.greenmagic.GreenMagicNavigation;
import net.nia.witchinghour.magic.grimoire.info.categories.purplemagic.PurpleMagicNavigation;
import net.nia.witchinghour.magic.grimoire.info.categories.redmagic.RedMagicNavigation;
import net.nia.witchinghour.magic.grimoire.info.categories.whitemagic.WhiteMagicNavigation;

public class CategoryNavigation {

    private static void general(String command, GrimoireEntity book, PlayerEntity player) {

        if (command.contains("commands")) {
            CommandsDisplay.display(book, player);
        } else if (command.contains("categories") || command.contains("entries")) {
            CategoriesDisplay.display(book, player);
        }

    }

    public static void reveal(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        // GENERAL
        general(command, book, player);

        // BLACK MAGIC
        BlackMagicNavigation.navigate(command, book, player, craft);

        // BLUE MAGIC
        BlueMagicNavigation.navigate(command, book, player, craft);

        // GREEN MAGIC
        GreenMagicNavigation.navigate(command, book, player, craft);

        // PURPLE MAGIC
        PurpleMagicNavigation.navigate(command, book, player, craft);

        // RED MAGIC
        RedMagicNavigation.navigate(command, book, player, craft);

        // WHITE MAGIC
        WhiteMagicNavigation.navigate(command, book, player, craft);

    }
}
