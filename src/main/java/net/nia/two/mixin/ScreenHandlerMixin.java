package net.nia.witchinghour.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.nia.witchinghour.magic.spells.purplemagic.Open;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(
            method = "canUse(Lnet/minecraft/screen/ScreenHandlerContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/Block;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void alwaysUsableStatic(ScreenHandlerContext context, PlayerEntity player, Block block, CallbackInfoReturnable<Boolean> cir) {
        if (Open.IGNORE_DISTANCE) cir.setReturnValue(true);
    }
}

