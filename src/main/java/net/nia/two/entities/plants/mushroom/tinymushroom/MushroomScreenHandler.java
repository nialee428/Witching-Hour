package net.nia.witchinghour.entities.plants.mushroom.tinymushroom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.nia.witchinghour.screenhandlers.WitchingHourScreenHandlers;

public class MushroomScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    // Server-side constructor
    public MushroomScreenHandler(
            int syncId,
            PlayerInventory playerInventory,
            Inventory inventory
    ) {
        super(WitchingHourScreenHandlers.MUSHROOM_SCREEN_HANDLER, syncId);

        this.inventory = inventory;

        // Mushroom inventory — 27 slots
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {

                int index = column + row * 9;

                this.addSlot(
                        new Slot(
                                inventory,
                                index,
                                8 + column * 18,
                                18 + row * 18
                        )
                );
            }
        }

        // Player inventory — 3 rows
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {

                this.addSlot(
                        new Slot(
                                playerInventory,
                                column + row * 9 + 9,
                                8 + column * 18,
                                84 + row * 18
                        )
                );
            }
        }

        // Player hotbar
        for (int column = 0; column < 9; column++) {

            this.addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            8 + column * 18,
                            142
                    )
            );
        }
    }

    // Client-side constructor
    public MushroomScreenHandler(
            int syncId,
            PlayerInventory playerInventory,
            int mushroomId
    ) {
        this(
                syncId,
                playerInventory,
                new net.minecraft.inventory.SimpleInventory(27)
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {

        ItemStack newStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {

            ItemStack originalStack = slot.getStack();

            newStack = originalStack.copy();

            // Mushroom inventory: slots 0-8
            if (slotIndex < 27) {

                // Move mushroom item -> player inventory
                if (!this.insertItem(
                        originalStack,
                        27,
                        this.slots.size(),
                        true
                )) {
                    return ItemStack.EMPTY;
                }

            } else {

                // Move player item -> mushroom inventory
                if (!this.insertItem(
                        originalStack,
                        0,
                        27,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}