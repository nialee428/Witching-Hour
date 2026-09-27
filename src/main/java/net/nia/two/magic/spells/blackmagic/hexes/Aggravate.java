package net.nia.witchinghour.magic.spells.blackmagic.hexes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.entities.mobtarget.MobTargetPrefs;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggravate {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Aggravate";
    public static final String CATEGORY = "Black Magic";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.ROTTEN_FLESH, null), 4);
        RECIPE.put(new SpellIngredient(Items.DIAMOND_HELMET, null), 1);
        RECIPE.put(new SpellIngredient(Items.JUKEBOX, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "aggravate", "aggravation", "aggravated", "aggravating", "aggravates",
                        "anger", "angers", "angered", "angry", "angering",
                        "enrage", "enrages", "enraged", "enraging",
                        "provoke", "provokes", "provoked", "provoking",
                        "incite", "incites", "incited", "inciting",
                        "hostile", "hostility"
                },

                new double[] {
                        125.0, 27.0, 122.0, 1.0
                },

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
                            EntityHitResult hit
                    ) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(
                                        bolt,
                                        spell,
                                        new EntityHitResult(p, p.getPos()),
                                        ID
                                );
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        if (hit.getEntity() instanceof LivingEntity entity) {

                            List<Entity> players = Names.getPlayersByNames(player.getServer(), bolt.spell);

                            if (!players.isEmpty()) {
                                for (Entity p : players) {
                                    if (p instanceof LivingEntity p_) {
                                        MobTargetPrefs.prefer(entity, p_);
                                    }
                                }
                            } else {
                                for (PlayerEntity p : player.getWorld().getPlayers()) {
                                    MobTargetPrefs.prefer(entity, p);
                                }
                            }

                            bolt.getWorld().playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    SoundEvents.ENTITY_ALLAY_HURT,
                                    SoundCategory.HOSTILE,
                                    0.75f,
                                    1.2f
                            );
                        }

                        bolt.discard();
                    }
                },

                RECIPE
        );
    }
}
