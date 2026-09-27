package net.nia.witchinghour.magic.spells.whitemagic.charms;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gust {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Gust";

    public static void pushEntity(Entity entity, Vec3d direction, double strength) {
        // Normalize the direction so strength is consistent
        Vec3d normalized = direction.normalize();

        // Apply velocity
        entity.addVelocity(
                normalized.x * strength,
                normalized.y * strength,
                normalized.z * strength
        );

        // Sync velocity for players
        entity.velocityModified = true;

        entity.getWorld().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW,
                SoundCategory.AMBIENT,
                1.0f,
                0.45f
        );
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.STICKY_PISTON, null), 1);
        RECIPE.put(new SpellIngredient(Items.SLIME_BLOCK, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "gust", "gusts", "fling", "flung", "flinging", "flings", "launch", "launched", "launches", "launching",
                        "launcher", "flinger"
                },

                new double[] {
                        10.0, 5.0, 14.0, 1.0
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
                        if (player == null) {
                            return;
                        }


                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        // maybe fling items and any nearby creatures?

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

                        Entity target = hit.getEntity();

                        Vec3d direction = new Vec3d(0, 1, 0);
                        double strength = 1.0;

                        if (spell.contains(" back") || spell.contains(" away")) {
                            float yaw = player.getYaw(); // degrees
                            double rad = Math.toRadians(yaw - 90);
                            if (target != player) {
                                rad = Math.toRadians(yaw + 90);
                            }

                            direction = new Vec3d(Math.cos(rad), 0, Math.sin(rad));
                        } else if ((spell.contains(" toward") && spell.split( "toward")[1].contains(" me")) || spell.contains(" forward")) {
                            float yaw = player.getYaw(); // degrees
                            double rad = Math.toRadians(yaw + 90);
                            if (target != player) {
                                rad = Math.toRadians(yaw - 90);
                            }

                            direction = new Vec3d(Math.cos(rad), 0, Math.sin(rad));
                        } else if (spell.contains(" left")) {
                            float yaw = player.getYaw(); // degrees
                            double rad = Math.toRadians(yaw);

                            direction = new Vec3d(Math.cos(rad), 0, Math.sin(rad));
                        } else if (spell.contains(" right")) {
                            float yaw = player.getYaw(); // degrees
                            double rad = Math.toRadians(yaw - 180);

                            direction = new Vec3d(Math.cos(rad), 0, Math.sin(rad));
                        }

                        direction = new Vec3d(direction.x, 1+direction.y, direction.z);

                        if (spell.contains(" far")) {
                            strength += 1.0;
                        }

                        pushEntity(hit.getEntity(), direction, strength);

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}