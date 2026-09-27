package net.nia.witchinghour.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.nia.witchinghour.entities.mobtarget.MobTargetPrefs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(
            method = "setTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void witchinghour$preventIgnoredTarget(
            LivingEntity target,
            CallbackInfo ci
    ) {
        if (target == null) {
            return;
        }

        MobEntity mob = (MobEntity) (Object) this;

        if (MobTargetPrefs.isIgnored(mob, target)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tickMovement",
            at = @At("HEAD")
    )
    private void witchinghour$clearIgnoredTarget(
            CallbackInfo ci
    ) {
        MobEntity mob = (MobEntity) (Object) this;

        LivingEntity currentTarget = mob.getTarget();

        if (currentTarget != null
                && MobTargetPrefs.isIgnored(mob, currentTarget)) {

            mob.setTarget(null);
        }
    }
}