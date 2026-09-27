package net.nia.witchinghour.magic.spells.whitemagic.charms;

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

public class Pacify {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Pacify";

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.COOKIE, null), 4);
        RECIPE.put(new SpellIngredient(Items.CARVED_PUMPKIN, null), 1);
        RECIPE.put(new SpellIngredient(Items.GOLDEN_HELMET, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "pacify", "pacification", "pacified", "pacifying", "pacifies", "pacifier",
                        "befriend", "befriends", "befriending", "befriended", "befriender",
                        "friend", "friendly", "friendlier", "friends", "friended",
                        "calm", "calms", "calmed", "calming",
                        "soothe", "soothes", "soothed", "soothing",
                        "placate", "placates", "placated", "placating",
                        "appease", "appeases", "appeased", "appeasing",
                        "peace", "peaceful", "peacefully"
                },

                new double[] {
                        125.0, 27.0, 122.0, 1.0
                },

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

                        if (hit.getEntity() instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        if (hit.getEntity() instanceof LivingEntity entity) {

                            List<Entity> players = Names.getPlayersByNames(player.getServer(), spell);
                            if (!players.isEmpty()) {
                                for (Entity p : players) {
                                    if (p instanceof LivingEntity p_) {
                                        MobTargetPrefs.ignore(entity, p_);
                                    }
                                }
                            } else {
                                for (PlayerEntity p : player.getWorld().getPlayers()) {
                                    MobTargetPrefs.ignore(entity, p);
                                }
                            }

                            bolt.getWorld().playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM,
                                    SoundCategory.AMBIENT,
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