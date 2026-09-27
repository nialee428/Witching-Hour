package net.nia.witchinghour.magic.spells.purplemagic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.magic.spells.redmagic.Destroy;

import java.util.*;

public class Waypoint {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Waypoint";

    public static String getWaypointName(String spell) {
        String name = "";
        String separator = "";

        String[] words = spell
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        boolean afterWaypoint = false;

        for (String w : words) {

            if (Arrays.stream(Waypoint.SPELL.keywords).anyMatch(s -> s.contains(w)) && !afterWaypoint) {
                afterWaypoint = true;
            } else if (w.contains("name") && afterWaypoint) {
                separator = w;
                break;
            }
        }

        name = spell.split(separator)[1];

        return name;
    }

    public static boolean createWaypoint(PlayerEntity player, SpellBoltEntity bolt, String spell, BlockPos pos) {
        String name = getWaypointName(spell);

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        String dimension = player.getWorld().getRegistryKey().getValue().toString();

        if (name.isEmpty()) {
            return true;
        } else if (bolt.casted.contains(Destroy.SPELL)) {
            data.removePos(name);

            bolt.getWorld().playSound(
                    null,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    SoundEvents.BLOCK_BEACON_DEACTIVATE,
                    SoundCategory.AMBIENT,
                    1.0f,
                    1.0f
            );
            return true; // bugfix: fixes waypoints not being removed! whoopsies
        }

        data.setPos(name, dimension, pos);

        bolt.getWorld().playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.AMBIENT,
                1.0f,
                1.0f
        );

        return false;
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.RESPAWN_ANCHOR, null), 1);
        RECIPE.put(new SpellIngredient(Items.WHITE_BED, null), 1);
        RECIPE.put(new SpellIngredient(Items.ENDER_EYE, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "waypoint", "waypoints"
                },

                new double[] {
                        1.0, 10.0, 10.0, 1.0
                },

                SpellType.PURPLE_MAGIC,

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

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        if (createWaypoint(player, bolt, spell, hit.getBlockPos())) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

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

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}