package net.nia.witchinghour.magic.spells.graymagic;

import net.nia.witchinghour.magic.spells.graymagic.spells.minion.FollowCMD;
import net.nia.witchinghour.magic.spells.graymagic.spells.minion.StayCMD;

public class GrayMagic {

    public static void register() {

        FollowCMD.init();
        StayCMD.init();

    }
}
