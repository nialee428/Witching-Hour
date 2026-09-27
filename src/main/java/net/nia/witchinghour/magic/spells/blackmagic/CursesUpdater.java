package net.nia.witchinghour.magic.spells.blackmagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.magic.spells.other.Spell;
import net.nia.witchinghour.magic.spells.blackmagic.hexes.Weakness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CursesUpdater {

    public static Map<String, Curse> curseRegistry = new HashMap<>();

    public static void registerCurses() {
        curseRegistry.put("Weakness", new Weakness());
    }

    public static void tick(PlayerEntity player) {
        EntityMagicData data = ModComponents.ENTITY_MAGIC.get(player);

        for (String curseName : data.getCurses()) {

            Curse curse = curseRegistry.get(curseName);
            if (curse == null) continue;

            curse.tick(player);
        }
    }

    public static boolean isProtected(String ID, PlayerEntity player, Spell SPELL, int power) {
        List<ItemStack> all = new ArrayList<>();

        PlayerInventory inv = player.getInventory();
        all.addAll(inv.main);
        all.addAll(inv.armor);
        all.addAll(inv.offHand);

        for (ItemStack stack : all) {
            NbtCompound nbt = stack.getNbt();

            if (nbt == null) {
                continue;
            }

            if (nbt.contains("witchinghour:protection")) {
                if (nbt.getString("witchinghour:protection").equalsIgnoreCase(ID)) {

                    if (stack.isDamageable()) {
                        // Apply durability damage
                        stack.setDamage(stack.getDamage() + power);

                        // If the item is now broken, remove it
                        if (stack.getDamage() >= stack.getMaxDamage()) {
                            stack.decrement(1); // remove the broken item
                        }

                        player.getWorld().playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ITEM_TOTEM_USE,
                                SoundCategory.AMBIENT,
                                0.5f,
                                0.3f
                        );

                        return true;
                    }


                    if (stack.getCount() >= power) {
                        stack.decrement(power);

                        player.getWorld().playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ITEM_TOTEM_USE,
                                SoundCategory.AMBIENT,
                                0.5f,
                                0.3f
                        );
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
