package net.nia.witchinghour.magic.spells.other;

import net.nia.witchinghour.magic.spells.blackmagic.BlackMagic;
import net.nia.witchinghour.magic.spells.bluemagic.BlueMagic;
import net.nia.witchinghour.magic.spells.graymagic.GrayMagic;
import net.nia.witchinghour.magic.spells.greenmagic.GreenMagic;
import net.nia.witchinghour.magic.spells.misc.Accept;
import net.nia.witchinghour.magic.spells.purplemagic.PurpleMagic;
import net.nia.witchinghour.magic.spells.redmagic.RedMagic;
import net.nia.witchinghour.magic.spells.targets.entity;
import net.nia.witchinghour.magic.spells.targets.general;
import net.nia.witchinghour.magic.spells.targets.item;
import net.nia.witchinghour.magic.spells.targets.self;
import net.nia.witchinghour.magic.spells.whitemagic.WhiteMagic;

import java.util.Map;

public class SpellLoader {

    public static void create(Spell SPELL, String id, String[] keywords, double[] data, SpellType type, SpellBehavior behavior, Map<SpellIngredient, Integer> recipe) {

        SPELL.ID = id;

        SPELL.keywords = keywords;
        SPELL.data = data;
        SPELL.type = type;

        SPELL.behavior = behavior;

        SPELL.recipe = recipe;

        SpellRegistry.register(SPELL);
    }

    public static void register() {

        // Targets
        entity.init();
        general.init();
        item.init();
        self.init();

        // Misc.
        Accept.init();

        BlackMagic.register();
        BlueMagic.register();
        GreenMagic.register();
        PurpleMagic.register();
        RedMagic.register();
        WhiteMagic.register();
        GrayMagic.register();

    }

}
