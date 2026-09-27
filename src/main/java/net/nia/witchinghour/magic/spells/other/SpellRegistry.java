package net.nia.witchinghour.magic.spells.other;

import java.util.ArrayList;
import java.util.List;

public class SpellRegistry {
    public static final List<Spell> SPELLS = new ArrayList<>();

    public static Spell getFromId(String id) {
        for (Spell spell : SPELLS) {
            if (spell.ID.equals(id)) {
                return spell;
            }
        }

        return null;
    }

    public static void register(Spell spell) {
        SPELLS.add(spell);
    }
}