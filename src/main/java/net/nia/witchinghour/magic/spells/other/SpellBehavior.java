package net.nia.witchinghour.magic.spells.other;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public interface SpellBehavior {
    default boolean checkCustomRequirements(
            String spell
    ) {
        return true;
    }

    default void onBlockHit(
            SpellBoltEntity bolt,
            String spellName,
            BlockHitResult hit
    ) {}

    default void onEntityHit(
            SpellBoltEntity bolt,
            String spellName,
            EntityHitResult hit
    ) {}


}