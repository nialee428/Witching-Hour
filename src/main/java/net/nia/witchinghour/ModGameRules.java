package net.nia.witchinghour;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {

    public static void init() {
        // Intentionally empty
    }

    public static final GameRules.Key<GameRules.BooleanRule> FIRST_NAMES_ONLY =
            GameRuleRegistry.register(
                    "firstNamesOnly",
                    GameRules.Category.PLAYER,
                    GameRuleFactory.createBooleanRule(false)
            );
}