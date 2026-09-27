package net.nia.witchinghour.magic.grimoire.info.categories.redmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class RedMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("red magic")) {
            RedMagicInformation.display(book, player);
        } else if (command.contains("collapse spell")) {
            if (craft) {
                CollapseSpell.craft(book, player);
                return;
            }
            CollapseSpell.display(book, player);
        } else if (command.contains("combustion spell")) {
            if (craft) {
                CombustionSpell.craft(book, player);
                return;
            }
            CombustionSpell.display(book, player);
        } else if (command.contains("destroy spell") || command.contains("destruction spell")) {
            if (craft) {
                DestroySpell.craft(book, player);
                return;
            }
            DestroySpell.display(book, player);
        } else if (command.contains("ignition spell")) {
            if (craft) {
                IgnitionSpell.craft(book, player);
                return;
            }
            IgnitionSpell.display(book, player);
        } else if (command.contains("evaporation spell")) {
            if (craft) {
                EvaporationSpell.craft(book, player);
                return;
            }
            EvaporationSpell.display(book, player);
        } else if (command.contains("lightning spell")) {
            if (craft) {
                LightningSpell.craft(book, player);
                return;
            }
            LightningSpell.display(book, player);
        } else if (command.contains("melting spell")) {
            if (craft) {
                MeltSpell.craft(book, player);
                return;
            }
            MeltSpell.display(book, player);
        }

    }

}
