package net.nia.witchinghour.magic.spells.other;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public record SpellIngredient(Item item, NbtCompound nbt) {}