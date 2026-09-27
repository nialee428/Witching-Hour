package net.nia.witchinghour.magic.spells.graymagic.spells.minion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.entities.Minion;
import net.nia.witchinghour.entities.WitchingHourCommand;
import net.nia.witchinghour.magic.spells.other.*;

public class FollowCMD {

    public static final Spell SPELL = new Spell();
    public static final String ID = "FollowCMD";

    public static void init() {

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "follow", "follows", "follower", "following", "followed"
                },

                new double[] {
                        1.0, 1.0, 1.0, 1.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.BLACK_MAGIC, // SPELL TYPE

                new SpellBehavior() { // SPELL BEHAVIORS

                    @Override
                    public void onBlockHit( // BLOCK HIT
                                            SpellBoltEntity bolt,
                                            String spell,
                                            BlockHitResult hit
                    ) {
                        bolt.discard();
                    }

                    @Override
                    public void onEntityHit( // ENTITY HIT
                                             SpellBoltEntity bolt,
                                             String spell,
                                             EntityHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        String[] words = spell
                                .toLowerCase()
                                .replaceAll("[^a-zA-Z ]", " ")
                                .trim()
                                .split("\\s+");

                        if (words.length == 0) {
                            return;
                        }

                        if (hit.getEntity() instanceof Minion minion) {

                            System.out.println("MINION HIT");

                            if (minion.getOwner() == null) {
                                System.out.println("MINION HAS NO OWNER");
                                return;
                            }

                            System.out.println("MINION OWNER: " + minion.getOwner().getName().getString());
                            System.out.println("CASTER: " + player.getName().getString());

                            if (minion.getOwner().getUuid().equals(player.getUuid())) {

                                System.out.println("OWNER MATCH!");

                                minion.setCommand(WitchingHourCommand.FOLLOW);
                                minion.setCommandTarget(player);

                                System.out.println("FOLLOW COMMAND SET");
                            }
                        }

                        if (hit.getEntity() instanceof Minion minion && minion.getOwner() != null && minion.getOwner().getUuid().equals(player.getUuid())) {
                            for (Entity entity : Names.getPlayersByNames(player.getServer(), spell.split("follow")[1])) {
                                if (entity instanceof LivingEntity livingEntity) {
                                    minion.setCommand(WitchingHourCommand.FOLLOW);
                                    minion.setCommandTarget(livingEntity);
                                    System.out.println(livingEntity);
                                }
                            }

                            for (String word : words) {
                                if (word.equals("me")) {
                                    minion.setCommand(WitchingHourCommand.FOLLOW);
                                    minion.setCommandTarget(player);
                                    System.out.println(player);
                                }
                            }
                        }



                        bolt.discard();

                    }
                },

                null // SPELL RECIPE
        );

    }
}