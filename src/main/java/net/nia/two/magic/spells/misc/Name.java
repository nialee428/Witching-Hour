package net.nia.witchinghour.magic.spells.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.other.*;

public class Name {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Name";

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "name", "title"
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
                        PlayerEntity player = (PlayerEntity) bolt.getOwner();
                        assert player != null;
                        // Names.setName(player, spell);
                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {

                        PlayerEntity player = (PlayerEntity) bolt.getOwner();
                        assert player != null;
                        PlayerMagicData pData = ModComponents.PLAYER_MAGIC.get(player);

                        if (spell.contains("name of my coven shall be")) {
                            if (hit.getEntity() instanceof GrimoireEntity) {
                                EntityMagicData data = ModComponents.ENTITY_MAGIC.get(hit.getEntity());
                                if (data.get("Coven Name").isEmpty() && pData.getCovenName().isEmpty() && ((GrimoireEntity) hit.getEntity()).getOwnerUuid().equals(player.getUuid())) {
                                    data.set("Coven Name", spell.split("name of my coven shall be")[1]);
                                    pData.setCovenName(data.get("Coven Name"));
                                }
                            } else if (spell.contains("title of my coven shall be")) {
                                EntityMagicData data = ModComponents.ENTITY_MAGIC.get(hit.getEntity());
                                if (data.get("Coven Name").isEmpty() && pData.getCovenName().isEmpty() && ((GrimoireEntity) hit.getEntity()).getOwnerUuid().equals(player.getUuid())) {
                                    data.set("Coven Name", spell.split("title of my coven shall be")[1]);
                                    pData.setCovenName(data.get("Coven Name"));
                                }
                            }
                        } else if (spell.contains("my name") || spell.contains("name me")) {
                            // Names.setName(player, spell);
                        }
                    }
                },

                null
        );

    }
}