package net.nia.witchinghour.magic.grimoire.info.categories.purplemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class PurpleMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("purple magic")) {
            PurpleMagicInformation.display(book, player);
        } else if (command.contains("open spell") || command.contains("opening spell")) {
            if (craft) {
                OpenSpell.craft(book, player);
                return;
            }
            OpenSpell.display(book, player);
        } else if (command.contains("scaling spell")) {
            if (craft) {
                ScalingSpell.craft(book, player);
                return;
            }
            ScalingSpell.display(book, player);
        } else if (command.contains("telekinesis spell")) {
            if (craft) {
                TelekinesisSpell.craft(book, player);
                return;
            }
            TelekinesisSpell.display(book, player);
        } else if (command.contains("teleportation spell")) {
            if (craft) {
                TeleportationSpell.craft(book, player);
                return;
            }
            TeleportationSpell.display(book, player);
        } else if (command.contains("waypoint spell")) {
            if (craft) {
                WaypointSpell.craft(book, player);
                return;
            }
            WaypointSpell.display(book, player);
        }

    }

}
