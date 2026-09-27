package net.nia.witchinghour.magic.spells.bluemagic;

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
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.magic.spells.targets.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resistance implements Curse {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Resistance";

    @Override
    public void add(LivingEntity entity, PlayerEntity caster) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.RESISTANCE, 15*20, 0, true, false, true);
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

        NbtCompound enchants = new NbtCompound();
        enchants.putInt("minecraft:protection", 4);

        NbtCompound nbt = new NbtCompound();
        nbt.put("minecraft:stored_enchantments", enchants);

        NbtCompound waterNbt = new NbtCompound();
        waterNbt.putString("Potion", "minecraft:turtle_master");

        RECIPE.put(new SpellIngredient(Items.POTION, waterNbt), 4); // WATER BOTTLE
        RECIPE.put(new SpellIngredient(Items.ENCHANTED_BOOK, nbt), 1);
        RECIPE.put(new SpellIngredient(Items.SHIELD, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[]{
                        "resistance", "resistant", "durable", "tough", "toughen", "tougher"
                },

                new double[]{40.0, 10.0, 21.0, 1.0, 1.0},

                SpellType.BLUE_MAGIC,

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

                        new Resistance().add(entity, player);

                        bolt.discard();
                    }
                },

                RECIPE
        );
    }
}