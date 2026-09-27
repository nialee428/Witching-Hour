package net.nia.witchinghour.magic.spells.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.other.*;

public class Accept {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Accept";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "accept", "agree", "allow"
                },

                new double[] {
                        0.0, 1.0, 0.0, 0.0
                },

                SpellType.ACTION,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {
                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {

                        if (spell.contains("into my coven")) {
                            if (hit.getEntity() instanceof PlayerEntity player) {
                                PlayerEntity owner = (PlayerEntity) bolt.getOwner();
                                if (owner == null) return;

                                ServerWorld world = owner.getServer().getWorld(player.getWorld().getRegistryKey());

                                boolean hasInviteInOwnersGrimoire = false;

                                PlayerMagicData mData = ModComponents.PLAYER_MAGIC.get(owner);
                                PlayerMagicData pData = ModComponents.PLAYER_MAGIC.get(player);

                                for (GrimoireEntity grimoire : world.getEntitiesByType(
                                        WitchingHourEntities.GRIMOIRE,
                                        entity -> true
                                )) {
                                    EntityMagicData gData = ModComponents.ENTITY_MAGIC.get(grimoire);

                                    boolean isOwnersGrimoire =
                                            mData.getCovenName().equals(gData.get("Coven Name"));

                                    boolean hasInvite =
                                            gData.hasPendingInvite(pData.getPlayerName());

                                    if (isOwnersGrimoire && hasInvite) {
                                        hasInviteInOwnersGrimoire = true;
                                    }
                                    gData.removePendingInvite(pData.getPlayerName());
                                }

                                if (hasInviteInOwnersGrimoire) {
                                    // Player joins the coven
                                    pData.setCovenName(mData.getCovenName());
                                }

                            }
                        }

                    }
                },

                null
        );

    }
}