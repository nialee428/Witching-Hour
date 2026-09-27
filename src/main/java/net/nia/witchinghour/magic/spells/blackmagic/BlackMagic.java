package net.nia.witchinghour.magic.spells.blackmagic;

import net.nia.witchinghour.magic.spells.blackmagic.hexes.*;

public class BlackMagic {

    public static void register() {

        // Black Magic
        Damage.init();
        Weakness.init();
        Poison.init();
        Frostbite.init();
        Blindness.init();
        Nausea.init();
        Slowness.init();
        Starve.init();
        Wither.init();
        Aggravate.init();

    }

}
