package net.nia.witchinghour.magic.spells.other;

import net.minecraft.item.Item;

import java.util.Map;

public record SpellCraftResult(String result, Map<Item, Integer> consumed) {}