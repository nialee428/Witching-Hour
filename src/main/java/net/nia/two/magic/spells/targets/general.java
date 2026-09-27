package net.nia.witchinghour.magic.spells.targets;

import net.nia.witchinghour.magic.spells.other.Spell;
import net.nia.witchinghour.magic.spells.other.SpellBehavior;
import net.nia.witchinghour.magic.spells.other.SpellLoader;
import net.nia.witchinghour.magic.spells.other.SpellType;


public class general {

    public static final Spell SPELL = new Spell();
    public static final String ID = "General";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "this", "it", "these", "there", "here", "the", "a", "an", "that", "some", "somewhere", "something"
                },

                new double[] {
                        2.0, 1.0, 1.0
                },

                SpellType.GENERAL,

                new SpellBehavior() {
                },

                null
        );

    }

}
