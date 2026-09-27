package net.nia.witchinghour.magic.spells.targets;

import net.nia.witchinghour.magic.spells.other.Spell;
import net.nia.witchinghour.magic.spells.other.SpellBehavior;
import net.nia.witchinghour.magic.spells.other.SpellLoader;
import net.nia.witchinghour.magic.spells.other.SpellType;

public class self {
    public static final Spell SPELL = new Spell();
    public static final String ID = "Self";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "me", "myself", "i", "my", "us", "our"
                },

                new double[] {
                        2.0, 1.0, 1.0
                },

                SpellType.SELF,

                new SpellBehavior() {
                },

                null
        );

    }
}
