package net.nia.witchinghour.magic.spells.whitemagic.charms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repair {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Repair";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound enchants = new NbtCompound();
        enchants.putInt("minecraft:mending", 1);

        NbtCompound nbt = new NbtCompound();
        nbt.put("minecraft:stored_enchantments", enchants);

        NbtCompound healingNbt = new NbtCompound();
        healingNbt.putString("Potion", "minecraft:healing");

        RECIPE.put(new SpellIngredient(Items.ENCHANTED_BOOK, nbt), 4);

        RECIPE.put(new SpellIngredient(Items.CRAFTING_TABLE, null), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, healingNbt), 1);
        RECIPE.put(new SpellIngredient(Items.DIAMOND, null), 1);
        RECIPE.put(new SpellIngredient(Items.ANVIL, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "repair", "fix", "mend", "repairing", "repaired", "fixed", "fixing", "undo", "undoing", "undone",
                        "reverse", "reversed", "reversing", "reversal", "repairs", "reverses", "undoes", "fixes", "mends",
                        "mending", "mended", "mender", "reversals"
                },


                new double[] {
                        25.0, 15.0, 30.0, 1.0
                },

                SpellType.WHITE_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {
                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) return;

                        World world = bolt.getWorld();
                        if (world == null || world.isClient) return;

                        BlockPos hitPos = hit.getBlockPos();

                        List<SpellContainer> containers = world.getEntitiesByClass(
                                SpellContainer.class,
                                new Box(
                                        hitPos.getX() - 30, hitPos.getY() - 30, hitPos.getZ() - 30,
                                        hitPos.getX() + 30, hitPos.getY() + 30, hitPos.getZ() + 30
                                ),
                                e -> true
                        );

                        if (containers.isEmpty()) return;

                        for (SpellContainer container : containers) {

                            EntityMagicData magicData = ModComponents.ENTITY_MAGIC.get(container);

                            if (!magicData.get("Blue Magic: SHIELD").isEmpty()) {
                                continue;
                            }

                            for (NbtCompound blockData : magicData.getStoredBlocks()) {

                                BlockPos storedPos = new BlockPos(
                                        blockData.getInt("X"),
                                        blockData.getInt("Y"),
                                        blockData.getInt("Z")
                                );

                                if (storedPos.equals(hitPos)) {

                                    List<Spell> casted = new ArrayList<>();
                                    casted.add(SPELL);
                                    if (ManaHelper.consume(bolt, casted, false, 0)) break;

                                    magicData.restoreAllBlocks(world);
                                    bolt.discard();
                                    container.discard();

                                    break; // only break inner loop
                                }
                            }
                        }
                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {
                        bolt.discard();
                    }
                },

                RECIPE
        );

    }
}