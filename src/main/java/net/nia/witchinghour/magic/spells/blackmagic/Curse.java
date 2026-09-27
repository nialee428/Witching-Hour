package net.nia.witchinghour.magic.spells.blackmagic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface Curse {

    void tick(LivingEntity entity);

    void add(LivingEntity entity, PlayerEntity player);

    void remove(LivingEntity entity);
}
