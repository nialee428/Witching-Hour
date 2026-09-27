package net.nia.witchinghour.magic.grimoire.info.categories.blackmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

public class BlackMagicNavigation {

    public static void navigate(String command, GrimoireEntity book, PlayerEntity player, boolean craft) {

        if (command.contains("black magic")) {
            BlackMagicInformation.display(book, player);
        } else if (command.contains("blindness spell") || command.contains("blindness hex")) {
            if (craft) {
                BlindnessSpell.craft(book, player);
                return;
            }
            BlindnessSpell.display(book, player);
        } else if (command.contains("damage spell") || command.contains("damage hex")) {
            DamageSpell.display(book, player);
        } else if (command.contains("frostbite spell") || command.contains("frostbite hex")) {
            if (craft) {
                FrostbiteSpell.craft(book, player);
                return;
            }
            FrostbiteSpell.display(book, player);
        } else if (command.contains("poison spell") || command.contains("poison hex")) {
            if (craft) {
                PoisonSpell.craft(book, player);
                return;
            }
            PoisonSpell.display(book, player);
        } else if (command.contains("weakness spell") || command.contains("weakness hex")) {
            if (craft) {
                WeaknessSpell.craft(book, player);
                return;
            }
            WeaknessSpell.display(book, player);
        } else if (command.contains("nausea spell") || command.contains("nausea hex")) {
            if (craft) {
                NauseaSpell.craft(book, player);
                return;
            }
            NauseaSpell.display(book, player);
        } else if (command.contains("starvation spell") || command.contains("starvation hex")) {
            if (craft) {
                StarveSpell.craft(book, player);
                return;
            }
            StarveSpell.display(book, player);
        } else if (command.contains("withering spell") || command.contains("witheirng hex")) {
            if (craft) {
                WitherSpell.craft(book, player);
                return;
            }
            WitherSpell.display(book, player);
        }

    }

}
