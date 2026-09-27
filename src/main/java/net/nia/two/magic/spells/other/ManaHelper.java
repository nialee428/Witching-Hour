package net.nia.witchinghour.magic.spells.other;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.passives.PassivesHandler;

import java.util.List;

public class ManaHelper {

    public static boolean consume(SpellBoltEntity bolt, List<Spell> casted, boolean canFizzle, int manaCost) {
        PlayerEntity player = (PlayerEntity) bolt.getOwner();
        assert player != null;
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        int divider = 1;

        int pLevel = data.getSpellLevel("General");

        // 1. Compute total mana + general EXP
        ComputedCost cost = computeTotalCost(casted, bolt);

        int mana = data.getMana();

        // 2. Determine hardest spell
        Spell hardest = getHardestSpell(casted);
        double requiresFizzleCheck = hardest.data[3];

        int sLevel = data.getSpellLevel(hardest.getClass().getSimpleName());

        // 3. Fizzle chance
        boolean fizzle = FizzleHandler.fizzleChance(pLevel, sLevel, hardest.data);

        // 4. Fail conditions
        if ((fizzle || mana < cost.totalCost / divider) && requiresFizzleCheck != 0.0 && canFizzle) {

            if (mana < cost.totalCost / divider) {
                bolt.discard();
            }

            if (fizzle && mana >= cost.totalCost / divider) {

                if (manaCost != 0) {
                    data.setMana(data.getMana() - (manaCost/divider));

                    int addedDelay = (manaCost/divider) * 2;
                    int newDelay = Math.max(data.getRegenDelay() + addedDelay, 20);
                    newDelay = Math.min(newDelay, 2400 * 5);

                    data.setRegenDelay(newDelay);
                }

            }

            return true;
        }

        if (canFizzle) {
            return false;
        }

        if (mana < cost.totalCost / divider) {
            return true;
        }

        if (manaCost != 0) {
            applyManaCost(data, manaCost, cost.generalExp, casted, divider, player);
            return false;
        }

        // Success path
        applyManaCost(data, cost.totalCost, cost.generalExp, casted, divider, player);

        return false;
    }

    // -----------------------------
    //        Helper Methods
    // -----------------------------

    public static void applyManaCost(PlayerMagicData data, int totalCost, double exp, List<Spell> casted, int divider, PlayerEntity player) {

        totalCost /= divider;
        exp /= divider;

        if (totalCost <= 0) {
            totalCost = 1;
        }

        if (exp <= 0) {
            exp = 1;
        }

        data.setMana(data.getMana() - totalCost);

        int addedDelay = totalCost;
        int newDelay = Math.max(data.getRegenDelay() + addedDelay, 20);
        newDelay = Math.min(newDelay, 2400);

        data.setRegenDelay(newDelay);

        applySpellExp(data, casted);
        applyGeneralExp(data, exp);

        for (Spell spell : casted) {

            if (spell.type == SpellType.WHITE_MAGIC) {
                PassivesHandler.whiteMagicTrain(player);

            } else if (spell.type == SpellType.RED_MAGIC) {
                PassivesHandler.redMagicTrain(player);

            } else if (spell.type == SpellType.PURPLE_MAGIC) {
                PassivesHandler.purpleMagicTrain(player);

            } else if (spell.type == SpellType.GREEN_MAGIC) {
                PassivesHandler.greenMagicTrain(player);

            } else if (spell.type == SpellType.BLUE_MAGIC) {
                PassivesHandler.blueMagicTrain(player);

            } else if (spell.type == SpellType.BLACK_MAGIC) {
                PassivesHandler.blackMagicTrain(player);

            }

        }
    }

    public static void applySpellExp(PlayerMagicData data, List<Spell> casted) {
        for (Spell sp : casted) {
            String key = sp.getClass().getSimpleName();
            int level = data.getSpellLevel(key);
            int exp = data.getSpellExp(key) + 1;

            data.addSpellExp(key, 1);

            if (exp >= level) {
                data.setSpellLevel(key, level + 1);
            }
        }
    }

    public static void applyCategoryExp(String category, PlayerMagicData data) {
        int level = data.getSpellLevel(category);
        int exp = 1;

        if (exp >= 3 * level) {
            data.setSpellLevel(category, level + 1);
        }

        data.addSpellExp(category, exp);
    }

    public static void applyGeneralExp(PlayerMagicData data, double gained) {
        int level = data.getSpellLevel("General");
        int exp = data.getSpellExp("General");

        if (exp + gained >= 10 * level) {
            data.setSpellLevel("General", level + 1);
        }

        data.addSpellExp("General", (int) gained);
    }

    public static Spell getHardestSpell(List<Spell> casted) {
        Spell hardest = casted.get(0);

        for (Spell sp : casted) {
            if (sp.data[1] > hardest.data[1]) {
                hardest = sp;
            }
        }

        return hardest;
    }

    private static ComputedCost computeTotalCost(List<Spell> casted, SpellBoltEntity bolt) {
        int[] totalCost = {0};
        double[] generalExp = {0};

        casted.removeIf(sp -> {
            boolean ok = sp.behavior.checkCustomRequirements(bolt.spell);
            if (ok) {
                totalCost[0] += (int) sp.data[0];
                generalExp[0] += sp.data[2];
            }
            return !ok;
        });

        return new ComputedCost(totalCost[0], generalExp[0]);
    }

    private record ComputedCost(int totalCost, double generalExp) {
    }
}
