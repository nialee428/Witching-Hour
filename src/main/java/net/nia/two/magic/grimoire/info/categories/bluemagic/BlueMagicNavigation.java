package net.nia.witchinghour.magic.grimoire.info.categories.bluemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class BlueMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("blue magic")) {
            BlueMagicInformation.display(book, player);
        } else if (command.contains("spell protection spell") || command.contains("protection spell")) {
            if (craft) {
                SpellProtectionSpell.craft(book, player);
                return;
            }
            SpellProtectionSpell.display(book, player);
        } else if (command.contains("condensation spell")) {
            if (craft) {
                CondensationSpell.craft(book, player);
                return;
            }
            CondensationSpell.display(book, player);
        } else if (command.contains("extinguish spell")) {
            if (craft) {
                ExtinguishSpell.craft(book, player);
                return;
            }
            ExtinguishSpell.display(book, player);
        } else if (command.contains("resistance spell")) {
            if (craft) {
                ResistanceSpell.craft(book, player);
                return;
            }
            ResistanceSpell.display(book, player);
        } else if (command.contains("shield spell")) {
            if (craft) {
                ShieldSpell.craft(book, player);
                return;
            }
            ShieldSpell.display(book, player);
        } else if (command.contains("freezing spell")) {
            if (craft) {
                FreezeSpell.craft(book, player);
                return;
            }
            FreezeSpell.display(book, player);
        }

    }

}
