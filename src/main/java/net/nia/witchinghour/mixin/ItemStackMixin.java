package net.nia.witchinghour.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void witching_hour$forceGlint(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;

        // Example: glow if NBT flag is set
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains("witchinghour:protection", NbtElement.STRING_TYPE)) {
                cir.setReturnValue(true);
            }
        }
    }
}