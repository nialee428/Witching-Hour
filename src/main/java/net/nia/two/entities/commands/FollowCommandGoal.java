package net.nia.witchinghour.entities.commands;

import net.minecraft.entity.ai.goal.Goal;
import net.nia.witchinghour.entities.Minion;
import net.nia.witchinghour.entities.WitchingHourCommand;

public class FollowCommandGoal extends Goal {

    private final Minion golem;
    private final double speed;


    public FollowCommandGoal(Minion golem, double speed) {
        this.golem = golem;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        if (golem.getCommand() != WitchingHourCommand.FOLLOW) {
            return false;
        }

        return golem.getOwner() != null;
    }

    @Override
    public boolean shouldContinue() {
        if (golem.getCommand() != WitchingHourCommand.FOLLOW) {
            return false;
        }

        return golem.getOwner() != null;
    }

    @Override
    public void tick() {
        super.tick();

        if (golem.getCommandTarget() == null) {
            return;
        }

        golem.getMinion().getNavigation().startMovingTo(
                golem.getCommandTarget(),
                speed
        );
    }
}