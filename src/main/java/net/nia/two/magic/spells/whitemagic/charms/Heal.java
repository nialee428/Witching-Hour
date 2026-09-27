package net.nia.witchinghour.magic.spells.whitemagic.charms;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.ArrayList;
import java.util.List;

public class Heal {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Heal";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "heal", "healed", "healer", "healing", "heals"
                },

                new double[] {
                        1.0, 1.0, 2.0, 1.0
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

                        float amt = 2.0f;

                        if (hit.getEntity() instanceof LivingEntity entity) {
                            entity.heal(amt);

                            bolt.getWorld().playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                    SoundCategory.AMBIENT,
                                    0.75f,
                                    0.45f
                            );
                        }

                        bolt.discard();

                    }
                },

                null
        );

    }
}