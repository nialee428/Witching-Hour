package net.nia.witchinghour.magic.spells.other;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpellContainer extends PersistentProjectileEntity {

    public SpellContainer(EntityType<? extends SpellContainer> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    public SpellContainer(EntityType<? extends SpellContainer> type, LivingEntity owner, World world) {
        super(type, owner, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    // --- IMMUNITY ---

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return true;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void setOnFireFor(int seconds) {
        // Do nothing
    }

    @Override
    public void setFireTicks(int ticks) {
        // Do nothing
    }

    @Override
    public void setOnFire(boolean onFire) {
        // Do nothing
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    // --- PHYSICS DISABLE ---

    @Override
    public void tick() {
        // Prevent physics, collisions, gravity, etc.
        this.noClip = true;
        this.setVelocity(0, 0, 0);

        super.tick();
    }
}
