package net.nia.witchinghour.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.nia.witchinghour.magic.spells.purplemagic.Open;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerMixin {
    @Inject(
            method = "canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void alwaysUsable(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Open.IGNORE_DISTANCE) cir.setReturnValue(true);
    }
}

