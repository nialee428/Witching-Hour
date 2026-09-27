package net.nia.witchinghour.magic.spells.redmagic;

import net.nia.witchinghour.magic.spells.blackmagic.hexes.*;
import net.nia.witchinghour.magic.spells.bluemagic.*;
import net.nia.witchinghour.magic.spells.greenmagic.*;
import net.nia.witchinghour.magic.spells.purplemagic.*;
import net.nia.witchinghour.magic.spells.whitemagic.charms.*;

public class RedMagic {

    public static void register() {

        // Red Magic
        Destroy.init();
        Ignition.init();
        Evaporate.init();
        Collapse.init();
        Melt.init();
        Combustion.init();
        Lightning.init();

    }

}
