package net.nia.witchinghour.magic.spells.bluemagic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.create.GenerateSpellContainer;
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.magic.spells.redmagic.Destroy;

import java.util.ArrayList;
import java.util.List;

public class Shield {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Shield";

    public static SpellContainer attemptCreation(PlayerEntity player, SpellBoltEntity bolt, HitResult hitResult, World world) {
        SpellContainer closestContainer = null;

        BlockPos hitPos = null;

        if (hitResult instanceof EntityHitResult trg) {
            hitPos = trg.getEntity().getBlockPos();
        } else if (hitResult instanceof BlockHitResult trg) {
            hitPos = trg.getBlockPos();
        }

        assert hitPos != null;
        List<SpellContainer> containers = world.getEntitiesByClass(
                SpellContainer.class,
                new Box(
                        hitPos.getX() - 750, hitPos.getY() - 750, hitPos.getZ() - 750,
                        hitPos.getX() + 750, hitPos.getY() + 750, hitPos.getZ() + 750
                ),
                e -> true
        );

        for (SpellContainer c : containers) {

            EntityMagicData magicData = ModComponents.ENTITY_MAGIC.get(c);

            if (magicData.get("Blue Magic: SHIELD").isEmpty() || magicData.get("Blue Magic: SHIELD").contains("INACTIVE_SHIELD")) {
                continue;
            }

            for (NbtCompound blockData : magicData.getStoredBlocks()) {

                BlockPos storedPos = new BlockPos(
                        blockData.getInt("X"),
                        blockData.getInt("Y"),
                        blockData.getInt("Z")
                );

                if (storedPos.getSquaredDistance(hitPos) <= 12 * 12) {
                    closestContainer = c;

                    PlayerMagicData atkData = ModComponents.PLAYER_MAGIC.get(player);

                    int defLvl = Integer.parseInt(magicData.get("Blue Magic: SHIELD").split("LEVEL: ")[1]);
                    int atkLvl = atkData.getSpellLevel("General");

                    if (atkLvl < defLvl) {
                        return null;
                    }
                }
            }
        }

        if (closestContainer == null) {
            closestContainer = GenerateSpellContainer.create(
                    player,
                    hitPos.getX(),
                    hitPos.getY(),
                    hitPos.getZ()
            );
        }


        if (bolt.casted.contains(Destroy.SPELL)) {
            closestContainer.discard();
        }

        List<Spell> casted = new ArrayList<>();
        casted.add(SPELL);
        if (ManaHelper.consume(bolt, casted, false, 0)) {
            return null;
        }
        closestContainer.setOwner(player);

        return closestContainer;
    }

    public static boolean shieldedSpell(PlayerEntity caster, SpellBoltEntity bolt, HitResult hitResult, World world) {

        BlockPos hitPos = null;

        if (hitResult instanceof EntityHitResult trg) {
            hitPos = trg.getEntity().getBlockPos();
        } else if (hitResult instanceof BlockHitResult trg) {
            hitPos = trg.getBlockPos();
        }

        assert hitPos != null;
        List<SpellContainer> containers = world.getEntitiesByClass(
                SpellContainer.class,
                new Box(
                        hitPos.getX() - 750, hitPos.getY() - 750, hitPos.getZ() - 750,
                        hitPos.getX() + 750, hitPos.getY() + 750, hitPos.getZ() + 750
                ),
                e -> true
        );

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(caster);
        for (SpellContainer c : containers) {

            EntityMagicData magicData = ModComponents.ENTITY_MAGIC.get(c);

            if (magicData.get("Blue Magic: SHIELD").isEmpty() || (!magicData.get("Blue Magic: SHIELD").contains("ACTIVE_SHIELD") || magicData.get("Blue Magic: SHIELD").contains(caster.getUuidAsString()) || (!data.getCovenName().isEmpty() && magicData.get("Blue Magic: SHIELD").contains(data.getCovenName())))) {
                continue;
            }

            for (NbtCompound blockData : magicData.getStoredBlocks()) {

                BlockPos storedPos = new BlockPos(
                        blockData.getInt("X"),
                        blockData.getInt("Y"),
                        blockData.getInt("Z")
                );

                if (storedPos.equals(hitPos)) {

                    return true;

                }
            }
        }

        return false;
    }

    public static void init() {
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "shield", "shields", "shielded", "shielding"
                },

                new double[] {
                        1.0, 1.0, 3.0, 1.0
                },

                SpellType.BLUE_MAGIC,

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

                        SpellContainer container = attemptCreation(player, bolt, hit, bolt.getWorld());

                        if (container == null) {
                            bolt.discard();
                            return;
                        }

                        EntityMagicData data = ModComponents.ENTITY_MAGIC.get(container);
                        PlayerMagicData pData = ModComponents.PLAYER_MAGIC.get(player);

                        BlockPos pos = hit.getBlockPos();

                        BlockPos center = hit.getBlockPos();

                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                for (int dz = -1; dz <= 1; dz++) {

                                    BlockPos targetPos = center.add(dx, dy, dz);

                                    BlockState state = bolt.getWorld().getBlockState(targetPos);
                                    Block block = state.getBlock();

                                    data.addBlock(bolt.getWorld(), targetPos, true);
                                }
                            }
                        }

                        data.set("Blue Magic: SHIELD", "ACTIVE_SHIELD "+player.getUuidAsString()+" "+pData.getCovenName()+" LEVEL: "+pData.getSpellLevel("General"));

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

                null
        );

    }
}