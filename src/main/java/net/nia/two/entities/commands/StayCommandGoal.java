package net.nia.witchinghour.entities.commands;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.entities.Minion;
import net.nia.witchinghour.entities.WitchingHourCommand;
import net.nia.witchinghour.entities.golems.icegolem.IceGolemEntity;

public class StayCommandGoal extends Goal {

    private final Minion golem;

    public StayCommandGoal(Minion golem) {
        this.golem = golem;
    }

    @Override
    public boolean canStart() {
        return golem.getCommand() == WitchingHourCommand.STAY;
    }

    @Override
    public boolean shouldContinue() {
        return golem.getCommand() == WitchingHourCommand.STAY;
    }

    @Override
    public void tick() {

        BlockPos stayPos = golem.getStayPosition();

        if (stayPos == null) {
            return;
        }

        double distance = golem.getMinion().squaredDistanceTo(
                stayPos.getX() + 0.5,
                stayPos.getY(),
                stayPos.getZ() + 0.5
        );

        if (distance > 4.0) {
            IceGolemEntity entity;

            golem.getMinion().getNavigation().startMovingTo(
                    stayPos.getX() + 0.5,
                    stayPos.getY(),
                    stayPos.getZ() + 0.5,
                    1.0
            );
        }
    }
}