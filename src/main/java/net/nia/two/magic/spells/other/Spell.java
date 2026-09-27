package net.nia.witchinghour.magic.spells.other;

import java.util.HashMap;
import java.util.Map;

public class Spell {
    public String ID;
    public String[] keywords;
    public double[] data;
    public Class<?> behaviorClass;
    public SpellType type;
    public SpellBehavior behavior;
    public Map<SpellIngredient, Integer> recipe = new HashMap<>();
}
