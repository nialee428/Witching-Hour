package net.nia.witchinghour.magic.spells.redmagic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lightning {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Lightning";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound enchants = new NbtCompound();
        enchants.putInt("minecraft:channeling", 1);

        NbtCompound nbt = new NbtCompound();
        nbt.put("minecraft:stored_enchantments", enchants);

        RECIPE.put(new SpellIngredient(Items.ENCHANTED_BOOK, nbt), 4);
        RECIPE.put(new SpellIngredient(Items.LIGHTNING_ROD, null), 4);
        RECIPE.put(new SpellIngredient(Items.TRIDENT, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "lightning", "smite", "smitten", "smiting", "smit", "smote", "smites"
                },

                new double[] {
                        80.0, 35.0, 112.0, 1.0
                },

                SpellType.RED_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        BlockPos pos = hit.getBlockPos();

                        if (!bolt.getWorld().getBlockState(pos.up()).isReplaceable()) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create((ServerWorld) bolt.getWorld());
                        assert lightning != null;
                        lightning.refreshPositionAfterTeleport(
                                pos.getX() + 0.5,
                                pos.getY(),
                                pos.getZ() + 0.5
                        );

                        bolt.getWorld().spawnEntity(lightning);

                        bolt.discard();

                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }


                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        BlockPos pos = hit.getEntity().getBlockPos();

                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create((ServerWorld) bolt.getWorld());
                        assert lightning != null;
                        lightning.refreshPositionAfterTeleport(
                                pos.getX() + 0.5,
                                pos.getY(),
                                pos.getZ() + 0.5
                        );

                        bolt.getWorld().spawnEntity(lightning);

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}