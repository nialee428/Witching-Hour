package net.nia.witchinghour.magic.spells.blackmagic.hexes;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.magic.spells.blackmagic.Curse;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.magic.spells.targets.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blindness implements Curse {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Blindness";

    @Override
    public void add(LivingEntity entity, PlayerEntity caster) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.BLINDNESS, 15*20, 1, true, false, false);
        entity.addStatusEffect(effect, entity);
    }

    @Override
    public void tick(LivingEntity entity) {
    }

    @Override
    public void remove(LivingEntity entity) {
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound waterNbt = new NbtCompound();
        waterNbt.putString("Potion", "minecraft:water");

        RECIPE.put(new SpellIngredient(Items.BOWL, null), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, waterNbt), 1); // WATER BOTTLE
        RECIPE.put(new SpellIngredient(Items.BROWN_MUSHROOM, null), 1);
        RECIPE.put(new SpellIngredient(Items.INK_SAC, null), 2);
        RECIPE.put(new SpellIngredient(Items.AZURE_BLUET, null), 4);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID,

                new String[]{
                        "blind", "blindness", "blinded", "blinding", "blinder"
                },

                new double[]{50.0, 25.0, 52.0, 1.0, 1.0},

                SpellType.BLACK_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {
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

                        if (!(hit.getEntity() instanceof LivingEntity entity)) return;

                        if (bolt.spellInst.contains(item.SPELL)) {
                            return;
                        }

                        new Blindness().add(entity, player);

                        bolt.discard();
                    }
                },

                RECIPE
        );
    }
}