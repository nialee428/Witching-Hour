package net.nia.witchinghour.magic.grimoire.info.categories.greenmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class GreenMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("green magic")) {
            GreenMagicInformation.display(book, player);
        } else if (command.contains("bloom spell")) {
            if (craft) {
                BloomSpell.craft(book, player);
                return;
            }
            BloomSpell.display(book, player);
        } else if (command.contains("harvest spell")) {
            if (craft) {
                HarvestSpell.craft(book, player);
                return;
            }
            HarvestSpell.display(book, player);
        } else if (command.contains("plant spell") || command.contains("planting spell")) {
            if (craft) {
                PlantSpell.craft(book, player);
                return;
            }
            PlantSpell.display(book, player);
        } else if (command.contains("plow spell")) {
            if (craft) {
                PlowSpell.craft(book, player);
                return;
            }
            PlowSpell.display(book, player);
        } else if (command.contains("sink spell") || command.contains("sinking spell")) {
            if (craft) {
                SinkSpell.craft(book, player);
                return;
            }
            SinkSpell.display(book, player);
        }

    }

}
