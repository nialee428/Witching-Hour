package net.nia.witchinghour.magic.grimoire;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

public class GrimoireEntity extends TameableEntity {

    public GrimoireEntity(EntityType<? extends TameableEntity> type, World world) {
        super(type, world);
        this.setHealth(10000.0f);
        this.setInvulnerable(true);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new BirdNavigation(this, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 4000.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6);
    }

    private boolean shouldFollow = false;

    public void setShouldFollow(boolean value) {
        this.shouldFollow = value;
    }

    public boolean shouldFollow() {
        return shouldFollow;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ConditionalFollowGoal(this));
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        return null;
    }
    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ITEM_BOOK_PAGE_TURN;
    }

    @Override
    public void tick() {
        super.tick();
        this.setInvulnerable(true);
        this.setNoGravity(true);
        this.setHealth(10000.0f);
        this.heal(10000);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
}