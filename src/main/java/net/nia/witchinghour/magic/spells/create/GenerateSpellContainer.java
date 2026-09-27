package net.nia.witchinghour.magic.spells.create;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.magic.spells.other.SpellContainer;

public class GenerateSpellContainer {

    public static SpellContainer create(PlayerEntity player, double x, double y, double z) {
        if (player == null) {
            return null; // or throw, or skip spawning
        }

        SpellContainer container = new SpellContainer(
                WitchingHourEntities.SPELL_CONTAINER,
                player,
                player.getWorld()
        );

        container.setPos(x, y, z);
        container.setVelocity(0, 0, 0, 0, 0);

        player.getWorld().spawnEntity(container);

        return container;
    }

}