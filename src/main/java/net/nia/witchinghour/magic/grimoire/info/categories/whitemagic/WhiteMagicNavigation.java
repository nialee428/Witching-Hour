package net.nia.witchinghour.magic.grimoire.info.categories.whitemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class WhiteMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("white magic")) {
            WhiteMagicInformation.display(book, player);
        } else if (command.contains("cleansing spell")|| command.contains("cleansing charm")) {
            if (craft) {
                CleanseSpell.craft(book, player);
                return;
            }
            CleanseSpell.display(book, player);
        } else if (command.contains("gust spell") || command.contains("gust charm")) {
            if (craft) {
                GustSpell.craft(book, player);
                return;
            }
            GustSpell.display(book, player);
        } else if (command.contains("heal spell") || command.contains("heal charm")) {
            if (craft) {
                HealSpell.craft(book, player);
                return;
            }
            HealSpell.display(book, player);
        } else if (command.contains("reparation spell") || command.contains("reparation charm")) {
            if (craft) {
                RepairSpell.craft(book, player);
                return;
            }
            RepairSpell.display(book, player);
        } else if (command.contains("revelation spell") || command.contains("revelation charm")) {
            if (craft) {
                RevelationSpell.craft(book, player);
                return;
            }
            RevelationSpell.display(book, player);
        } else if (command.contains("glowing spell") || command.contains("glowing charm")) {
            if (craft) {
                GlowingSpell.craft(book, player);
                return;
            }
            GlowingSpell.display(book, player);
        } else if (command.contains("regeneration spell") || command.contains("regeneration charm")) {
            if (craft) {
                RegenerationSpell.craft(book, player);
                return;
            }
            RegenerationSpell.display(book, player);
        } else if (command.contains("swiftness spell") || command.contains("swiftness charm")) {
            if (craft) {
                SwiftnessSpell.craft(book, player);
                return;
            }
            SwiftnessSpell.display(book, player);
        } else if (command.contains("saturation spell") || command.contains("saturation charm")) {
            if (craft) {
                SaturateSpell.craft(book, player);
                return;
            }
            SaturateSpell.display(book, player);
        }

    }

}
