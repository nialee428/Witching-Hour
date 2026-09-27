package net.nia.witchinghour.magic.spells.targets;

import net.nia.witchinghour.magic.spells.other.Spell;
import net.nia.witchinghour.magic.spells.other.SpellBehavior;
import net.nia.witchinghour.magic.spells.other.SpellLoader;
import net.nia.witchinghour.magic.spells.other.SpellType;

public class entity {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Entity";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "animal", "entity", "creature", "creatures", "player", "person", "people", "you", "your", "you're", "you'll", "her", "him", "them", "his", "hers", "they"
                },

                new double[] {
                        2.0, 1.0, 1.0
                },

                SpellType.ENTITY,

                new SpellBehavior() {
                },

                null
        );

    }

}
