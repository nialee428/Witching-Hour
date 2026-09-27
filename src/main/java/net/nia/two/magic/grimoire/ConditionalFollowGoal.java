package net.nia.witchinghour.magic.grimoire;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;

public class ConditionalFollowGoal extends FollowOwnerGoal {

    private final GrimoireEntity book;

    public ConditionalFollowGoal(GrimoireEntity book) {
        super(book, 1.0, 3.0f, 2.0f, false);
        this.book = book;
    }

    @Override
    public boolean canStart() {
        return book.shouldFollow() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return book.shouldFollow() && super.shouldContinue();
    }
}
