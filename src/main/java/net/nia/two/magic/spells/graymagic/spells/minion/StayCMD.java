package net.nia.witchinghour.magic.spells.graymagic.spells.minion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.entities.Minion;
import net.nia.witchinghour.entities.WitchingHourCommand;
import net.nia.witchinghour.magic.spells.other.*;

public class StayCMD {

    public static final Spell SPELL = new Spell();
    public static final String ID = "StayCMD";

    public static void init() {

        // Create Spell
        SpellLoader.create(
                SPELL, // SPELL instance

                ID, new String[] { // ID & KEYWORDS LIST
                        "stay", "stays", "stayed", "staying"
                },

                new double[] {
                        1.0, 1.0, 1.0, 1.0 // MANA COST, LEVEL REQ, EXP REWARD
                },

                SpellType.WHITE_MAGIC, // SPELL TYPE

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

                        if (hit.getEntity() instanceof Minion minion && minion.getOwner() == player) {
                            minion.setCommand(WitchingHourCommand.STAY);
                            minion.setCommandTarget(null);
                            minion.setStayPosition(hit.getEntity().getBlockPos());
                        }



                        bolt.discard();

                    }
                },

                null // SPELL RECIPE
        );

    }
}