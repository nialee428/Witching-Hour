package net.nia.witchinghour.screenhandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PlayerInventoryViewScreenHandler extends ScreenHandler {

    private final PlayerInventory targetInv;
    private final PlayerInventory openerInv;

    private int targetStart, targetEnd;
    private int openerStart, openerEnd;

    public PlayerInventoryViewScreenHandler(
            int syncId,
            PlayerInventory openerInv,
            PlayerInventory targetInv
    ) {
        super(WitchingHourScreenHandlers.PLAYER_INVENTORY_VIEW, syncId);

        this.targetInv = targetInv;
        this.openerInv = openerInv;

        // --------------------------
        // TARGET INVENTORY (top)
        // --------------------------
        targetStart = this.slots.size();
        addFourRows(targetInv, 8, 25);
        targetEnd = this.slots.size();

        // --------------------------
        // OPENER INVENTORY (bottom)
        // --------------------------
        openerStart = this.slots.size();
        addFourRows(openerInv, 8, 160);
        openerEnd = this.slots.size();
    }

    private void addFourRows(PlayerInventory inv, int baseX, int baseY) {

        // Main 3 rows (indices 9–35)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv,
                        9 + col + row * 9,
                        baseX + col * 18,
                        baseY + 17 + row * 18));
            }
        }

        // Hotbar (indices 0–8)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv,
                    i,
                    baseX + i * 18,
                    baseY + 17 + 58)); // 17 + (18*3) + 5
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack original = slot.getStack();
        ItemStack copy = original.copy();

        if (index >= targetStart && index < targetEnd) {
            // Move from target → opener
            if (!this.insertItem(original, openerStart, openerEnd, false))
                return ItemStack.EMPTY;

        } else if (index >= openerStart && index < openerEnd) {
            // Move from opener → target
            if (!this.insertItem(original, targetStart, targetEnd, false))
                return ItemStack.EMPTY;
        }

        if (original.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return copy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}