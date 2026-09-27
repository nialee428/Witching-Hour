package net.nia.witchinghour.magic.spells.whitemagic.charms;

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

public class Swiftness implements Curse {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Swiftness";

    @Override
    public void add(LivingEntity entity, PlayerEntity caster) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED, 25*20, 1, true, false, false);
        entity.addStatusEffect(effect, entity);
    }

    @Override
    public void tick(LivingEntity entity) {
    }

    @Override
    public void remove(LivingEntity entity) {
    }

    public static void init() {

        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound potionNbt = new NbtCompound();
        potionNbt.putString("Potion", "minecraft:swiftness");

        RECIPE.put(new SpellIngredient(Items.BOWL, null), 1);
        RECIPE.put(new SpellIngredient(Items.POTION, potionNbt), 1); // POTION
        RECIPE.put(new SpellIngredient(Items.FEATHER, null), 1);
        RECIPE.put(new SpellIngredient(Items.SUGAR, null), 2);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID,

                new String[]{
                        "speed", "speeds", "speeding", "speedy", "speedster", "speeded", "swift", "swiftness", "swiftly"
                },

                new double[]{14.0, 12.0, 16.0, 1.0, 1.0},

                SpellType.WHITE_MAGIC,

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

                        new Swiftness().add(entity, player);

                        bolt.discard();
                    }
                },

                RECIPE
        );
    }
}