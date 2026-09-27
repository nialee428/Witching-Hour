package net.nia.witchinghour.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface Minion {
    boolean isMinion();

    void setCommand(WitchingHourCommand command);
    void setOwner(PlayerEntity player);
    void setStayPosition(BlockPos pos);
    void setCommandTarget(LivingEntity entity);

    WitchingHourCommand getCommand();
    BlockPos getStayPosition();
    PlayerEntity getOwner();
    LivingEntity getCommandTarget();
    MobEntity getMinion();
}
