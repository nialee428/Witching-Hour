package net.nia.witchinghour.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.nia.witchinghour.entities.mobtarget.MobTargetPrefs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetPredicate.class)
public class TargetPredicateMixin {

    @Inject(
            method = "test",
            at = @At("HEAD"),
            cancellable = true
    )
    private void witchinghour$preventIgnoredTarget(
            LivingEntity baseEntity,
            LivingEntity targetEntity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (MobTargetPrefs.isIgnored(baseEntity, targetEntity)) {
            cir.setReturnValue(false);
        }
    }
}