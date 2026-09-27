package net.nia.witchinghour.magic.spells.redmagic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.*;

public class Combustion {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Combustion";

    public static void triggerExplosion(SpellBoltEntity bolt, BlockPos pos) {

        float finalPower = 1.5f;

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        // Run the explosion but don't apply it yet
        Explosion explosion = new Explosion(
                bolt.getWorld(),
                bolt,
                null,
                null,
                centerX,
                centerY,
                centerZ,
                finalPower,
                false,
                Explosion.DestructionType.DESTROY
        );
        explosion.collectBlocksAndDamageEntities();

        EntityMagicData data = ModComponents.ENTITY_MAGIC.get(bolt.container);

        List<BlockPos> affectedBlocks = explosion.getAffectedBlocks();
        Set<BlockPos> affectedSet = new HashSet<>(affectedBlocks);
        Set<BlockPos> savedBlocks = new HashSet<>();

        for (BlockPos pos_ : affectedSet) {

            // Capture the block state BEFORE the explosion destroys it
            BlockState state_ = bolt.getWorld().getBlockState(pos_);

            // Skip air blocks
            if (state_.isAir() || state_.getBlock() == Blocks.BEDROCK) continue;

            // Skip if already saved
            if (savedBlocks.contains(pos_)) continue;

            // Save the block
            data.addBlock(bolt.getWorld(), pos_, false);

            // Mark as saved
            savedBlocks.add(pos_);
        }

        bolt.getWorld().playSound(
                null,
                centerX, centerY, centerZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.BLOCKS,
                0.2F,
                1.0F
        );

        ((ServerWorld)bolt.getWorld()).spawnParticles(
                ParticleTypes.EXPLOSION,
                centerX, centerY, centerZ,
                1,
                0.0, 0.0, 0.0,
                0.0
        );
        explosion.affectWorld(true);

        Box box = new Box(
                centerX - finalPower * 2,
                centerY - finalPower * 2,
                centerZ - finalPower * 2,
                centerX + finalPower * 2,
                centerY + finalPower * 2,
                centerZ + finalPower * 2
        );

        for (ItemEntity item : bolt.getWorld().getEntitiesByClass(ItemEntity.class, box, e -> true)) {
            item.discard();
        }
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.TNT, null), 4);
        RECIPE.put(new SpellIngredient(Items.GUNPOWDER, null), 4);
        RECIPE.put(new SpellIngredient(Items.FLINT_AND_STEEL, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "explode", "explosion", "exploding", "explosive", "detonate", "blast", "burst", "boom", "detonation",
                        "detonating", "bomb", "bombs", "bombing", "bombed", "combust", "combustion", "combusting", "combusted",
                        "blasted", "blasting", "blaster", "blasts", "explodes", "detonates", "bursts", "combusts"
                },

                new double[] {
                        10.0, 20.0, 31.0, 1.0
                },

                SpellType.RED_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {

                        BlockPos pos = hit.getBlockPos();
                        World world = bolt.getWorld();
                        BlockState state = world.getBlockState(pos);

                        if (state.isReplaceable() || !state.getFluidState().isEmpty() || state.isAir()) {
                            return;
                        }

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        triggerExplosion(bolt, pos);

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

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
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

                        if (hit.getEntity() != null) {
                            BlockPos pos = hit.getEntity().getBlockPos();

                            triggerExplosion(bolt, pos);

                            bolt.discard();
                        }
                    }
                },

                RECIPE
        );

    }
}