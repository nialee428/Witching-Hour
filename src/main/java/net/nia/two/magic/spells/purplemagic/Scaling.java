package net.nia.witchinghour.magic.spells.purplemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scaling {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Scaling";

    public static final List<String> shrinkKeywords = List.of(
            "shrink", "shrinks", "shrinker", "shrinking", "shrunk", "shrunken"
    );

    public static final List<String> growKeywords = List.of(
            "grow", "grown", "grew", "growing", "grower", "enlarged", "enlarging", "enlarges", "enlarge", "enlarger"
    );

    public static final List<String> scaleKeywords = List.of(
            "scale", "scaling", "scales", "scaled", "size", "sized", "sizes", "sizing", "sizer", "scaler"
    );

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.BONE_MEAL, null), 8);
        RECIPE.put(new SpellIngredient(Items.ARMOR_STAND, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "scale", "scaling", "scales", "scaled", "size", "sized", "sizes", "sizing", "sizer", "scaler",
                        "shrink", "shrinks", "shrinker", "shrinking", "shrunk", "shrunken", "grow", "grown", "grew",
                        "growing", "grower", "enlarged", "enlarging", "enlarges", "enlarge", "enlarger"
                },

                new double[] {
                        10.0, 20.0, 17.0, 1.0
                },

                SpellType.PURPLE_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {
                        bolt.discard();
                        return;
                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        float scale = 0.0f;

                        if (shrinkKeywords.stream().anyMatch(w -> spell.matches(".*\\b" + w + "\\b.*"))) {
                            scale = 0.75f;
                        } else if (growKeywords.stream().anyMatch(w -> spell.matches(".*\\b" + w + "\\b.*"))) {
                            scale = 1.25f;
                        }

                        if (spell.contains(" original")) {
                            String t = spell.split(" original")[1];

                            if (scaleKeywords.stream().anyMatch(w -> t.matches(".*\\b" + w + "\\b.*"))) {
                                scale = 1.0f;
                            }
                        }

                        if (scale == 0.0f) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }


                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        ScaleData baseScale = ScaleTypes.BASE.getScaleData(hit.getEntity());
                        baseScale.setScale(baseScale.getScale());

                        if (scale == 1.0f) {
                            baseScale.setTargetScale(scale);
                            return;
                        }

                        if (baseScale.getScale() < .15f || baseScale.getScale() > 2.0f) {
                            return;
                        }

                        baseScale.setTargetScale(baseScale.getScale() * scale);
                        baseScale.setPersistence(true);

                        bolt.discard();
                    }
                },

                RECIPE
        );

    }
}