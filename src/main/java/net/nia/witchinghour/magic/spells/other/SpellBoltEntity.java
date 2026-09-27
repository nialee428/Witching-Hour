package net.nia.witchinghour.magic.spells.other;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.nia.witchinghour.Scheduler;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.create.GenerateSpellContainer;
import net.nia.witchinghour.magic.spells.misc.EmptySpell;
import net.nia.witchinghour.magic.spells.purplemagic.Teleportation;
import net.nia.witchinghour.magic.spells.redmagic.Combustion;
import net.nia.witchinghour.magic.spells.whitemagic.charms.Repair;
import net.nia.witchinghour.particles.SmokeParticleEffect;
import net.nia.witchinghour.particles.SoulParticleEffect;

import java.util.*;

public class SpellBoltEntity extends PersistentProjectileEntity {
    private static final TrackedData<Float> COLOR_R =
            DataTracker.registerData(SpellBoltEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> COLOR_G =
            DataTracker.registerData(SpellBoltEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> COLOR_B =
            DataTracker.registerData(SpellBoltEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> DOES_FIZZLE =
            DataTracker.registerData(SpellBoltEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final double MULTI_RADIUS = 2.5;
    private static final double MERGE_RADIUS = 12.0;

    public float colorR;
    public float colorG;
    public float colorB;
    public String spell;
    public ServerPlayerEntity owner;
    public String hasMS = null;
    public boolean canCallSelf = true;
    public boolean doesFizzle = false;
    public boolean hasChecked = false;
    public List<Spell> casted = new ArrayList<>();
    public List<Spell> spellInst = new ArrayList<>();
    public List<HitResult> subjectsList = new ArrayList<>();
    public HitResult randomizedHit = null;
    public SpellContainer container = null;
    public List<Spell> destructiveActions = new ArrayList<>();
    public List<Entity> entitiesAffected = new ArrayList<>();
    public List<Block> blocksAffected = new ArrayList<>();
    public boolean hasHit = false;
    private boolean popped = false;
    private boolean popRequested = false;
    public boolean downwards = false;
            
    public SpellBoltEntity(EntityType<? extends SpellBoltEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    public SpellBoltEntity(EntityType<? extends SpellBoltEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
        this.setNoGravity(true);
    }

    public float getColorR() { return this.dataTracker.get(COLOR_R); }
    public float getColorG() { return this.dataTracker.get(COLOR_G); }
    public float getColorB() { return this.dataTracker.get(COLOR_B); }

    public void setColor(float r, float g, float b) {
        this.dataTracker.set(COLOR_R, r);
        this.dataTracker.set(COLOR_G, g);
        this.dataTracker.set(COLOR_B, b);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR_R, 0f);
        this.dataTracker.startTracking(COLOR_G, 0f);
        this.dataTracker.startTracking(COLOR_B, 0f);
        this.dataTracker.startTracking(DOES_FIZZLE, false);
    }

    public static boolean isTwoBlockTallSpace(World world, BlockPos pos) {
        // Player feet position
        BlockState feet = world.getBlockState(pos);
        // Player head position
        BlockState head = world.getBlockState(pos.up());

        return feet.isAir() && head.isAir();
    }

    public static BlockPos findGround(World world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        while (mutable.getY() > world.getBottomY() + 1) {
            BlockState state = world.getBlockState(mutable);

            // Found solid ground
            if (state.isSolidBlock(world, mutable)) {
                BlockPos feet = mutable.up();

                if (isTwoBlockTallSpace(world, feet)) {
                    return feet;
                }
            }

            mutable.move(Direction.DOWN);
        }

        return null;
    }

    public static boolean hasRandomKeyword(String spell) {
        return (spell.contains("away") || spell.contains("somewhere") || spell.contains("random location") || spell.contains("random place") || spell.contains("random spot") || spell.contains("near") || spell.contains("distant") || spell.contains("distance"));
    }

    public void randomizeHit(HitResult hitResult, SpellBoltEntity bolt, boolean ignoreCheck) {

        String t = bolt.spell;
        if (hasRandomKeyword(t) && (randomizedHit == null || ignoreCheck)) {

            PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
            if (player == null) {
                return;
            }

            PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
            int mana = data.getMana();

            double radius = 100.0;

            if (t.contains("close by") || t.contains("near")) {
                radius /= 3;
                if (!t.contains("away")) {
                    radius /= 6;
                }
            } if (t.contains("far") || t.contains("distant") || t.contains("distance")) {
                radius *= 3;
            }

            BlockPos scrambledPos = null;

            if (hitResult instanceof EntityHitResult entityHitResult) {
                scrambledPos = Teleportation.scrambleSpherical(
                        entityHitResult.getEntity().getBlockPos(),
                        radius,
                        bolt.getWorld().random,
                        bolt.getWorld()
                );
            } else if (hitResult instanceof BlockHitResult blockHitResult){
                scrambledPos = Teleportation.scrambleSpherical(
                        blockHitResult.getBlockPos(),
                        radius,
                        bolt.getWorld().random,
                        bolt.getWorld()
                );
            }

            if (scrambledPos == null) {
                return;
            }

            BlockPos topPos = findGround(bolt.getWorld(), scrambledPos);
            if (topPos == null) {
                randomizeHit(hitResult, bolt, false);
                return;
            }

            Vec3d hitVec = Vec3d.ofCenter(topPos);

            if (hitVec.y < -60) {
                hitVec = new Vec3d(hitVec.x, -60, hitVec.z);
            }

            double distance = hitResult.getPos().distanceTo(topPos.toCenterPos());
            int manaCost = (int)Math.ceil(distance / 25.0);

            if (manaCost > mana) {
                randomizedHit = hitResult;
                return;
            }

            List<Spell> casted = new ArrayList<>();
            casted.add(EmptySpell.SPELL);

            ManaHelper.applyManaCost(data, manaCost, 5, casted, 1, player);

            randomizedHit = new BlockHitResult(
                    hitVec,
                    Direction.UP,
                    topPos.down(),
                    false
            );

            System.out.println(topPos);
            return;
        }

        randomizedHit = hitResult;
    }

    public void loadDestructiveActions() {
        destructiveActions.add(Combustion.SPELL);
    }

    public void runSpell(HitResult hitResult, SpellBoltEntity bolt, List<Spell> casted) {

        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
        if (player == null) {
            return;
        }

        World world = bolt.getWorld();
        if (world == null || world.isClient) return;


        if (destructiveActions.isEmpty()) {
            loadDestructiveActions();
        }

        if (!bolt.spellInst.contains(Teleportation.SPELL)) {
            randomizeHit(hitResult, bolt, false);
            hitResult = randomizedHit;
        }

        // Scan to check for nearby containers first
        if (container == null) {

            SpellContainer closestContainer = null;

            BlockPos hitPos;

            if (hitResult instanceof EntityHitResult trg) {
                hitPos = trg.getEntity().getBlockPos();
            } else if (hitResult instanceof BlockHitResult trg) {
                hitPos = trg.getBlockPos();
            } else {
                return;
            }

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

                for (NbtCompound blockData : magicData.getStoredBlocks()) {

                    BlockPos storedPos = new BlockPos(
                            blockData.getInt("X"),
                            blockData.getInt("Y"),
                            blockData.getInt("Z")
                    );

                    if (storedPos.getSquaredDistance(hitPos) <= MERGE_RADIUS * MERGE_RADIUS) {
                        closestContainer = c;
                    }
                }
            }

            if (closestContainer == null) {
                for (Spell sp : casted) {
                    if (destructiveActions.contains(sp)) {
                        closestContainer = GenerateSpellContainer.create(
                                player,
                                hitPos.getX(),
                                hitPos.getY(),
                                hitPos.getZ()
                        );
                        break;
                    }
                }
            }

            container = closestContainer;
        }

        int delay = 1;

        if (hitResult instanceof EntityHitResult entityHitResult) {

            for (Spell sp2 : casted) {
                if (
                        sp2.type == SpellType.ACTION
                                || sp2.type == SpellType.MODIFIER
                                || sp2.type == SpellType.BLACK_MAGIC
                                || sp2.type == SpellType.BLUE_MAGIC
                                || sp2.type == SpellType.GREEN_MAGIC
                                || sp2.type == SpellType.PURPLE_MAGIC
                                || sp2.type == SpellType.RED_MAGIC
                                || sp2.type == SpellType.WHITE_MAGIC
                ) {
                    Scheduler.schedule(delay, () -> sp2.behavior.onEntityHit(bolt, bolt.spell, entityHitResult));
                    delay += 3;
                }
            }

        } else if (hitResult instanceof BlockHitResult blockHitResult) {

            for (Spell sp2 : casted) {
                if (
                        sp2.type == SpellType.ACTION
                                || sp2.type == SpellType.MODIFIER
                                || sp2.type == SpellType.BLACK_MAGIC
                                || sp2.type == SpellType.BLUE_MAGIC
                                || sp2.type == SpellType.GREEN_MAGIC
                                || sp2.type == SpellType.PURPLE_MAGIC
                                || sp2.type == SpellType.RED_MAGIC
                                || sp2.type == SpellType.WHITE_MAGIC
                ) {
                    Scheduler.schedule(delay, () -> sp2.behavior.onBlockHit(bolt, bolt.spell, blockHitResult));
                    delay++;
                }
            }

        }

        hasHit = true;

    }

    public boolean checkCustomRequirements(String spell) {
        return true;
    }

    @Override
    public void onCollision(HitResult hitResult) {
        if (this.spell == null || spellInst.isEmpty() || !hasChecked || doesFizzle){
            return;
        }

        PlayerEntity player = this.getOwner() instanceof PlayerEntity p ? p : null;
        if (player == null) {
            return;
        }

        if (hitResult instanceof EntityHitResult entityHit) {

            boolean canContinue = false;

            for (Spell sp : spellInst) {
                if (sp.type == SpellType.GENERAL || sp.type == SpellType.ENTITY) {
                    canContinue = true;
                } if (sp.type == SpellType.ITEM) {
                    canContinue = true;

                    // get all entities in sphere
                    List<Entity> entities = getEntitiesInSphere(this.getWorld(), hitResult.getPos(), MULTI_RADIUS);

                    // remove projectile
                    entities.remove(this);
                    entities.remove(player);

                    // convert to HitResults
                    List<HitResult> results = convertEntities(entities);

                    for (HitResult h : results) {
                        if (h instanceof EntityHitResult r) {
                            if (r.getEntity() instanceof ItemEntity) {
                                hitResult = r;
                                break;
                            }
                        }
                    }

                } if (sp.type == SpellType.SELF && canCallSelf) {

                    canCallSelf = false;
                    Vec3d hitPos = player.getPos().add(0, player.getHeight() / 2.0, 0); // middle of body

                    EntityHitResult simulatedHit = new EntityHitResult(player, hitPos);

                    if (hasMS.equalsIgnoreCase("us") || hasMS.equalsIgnoreCase("our")) {
                        handleMS(simulatedHit, player);
                    } else {
                        runSpell(simulatedHit, this, casted);
                    }

                }
            }

            if (!canContinue) {
                this.discard();
                return;
            }

            handleMS(hitResult, player);
            runSpell(hitResult, this, casted);
            this.discard();

        }

        if (hitResult instanceof BlockHitResult blockHit) {

            boolean canContinue = false;

            for (Spell sp : spellInst) {

                if (sp.type == SpellType.GENERAL) {
                    canContinue = true;
                } if (sp.type == SpellType.ITEM) {

                    canContinue = true;
                    // get all entities in sphere
                    List<Entity> entities = getEntitiesInSphere(this.getWorld(), hitResult.getPos(), MULTI_RADIUS);

                    // remove projectile
                    entities.remove(this);
                    entities.remove(player);

                    // convert to HitResults
                    List<HitResult> results = convertEntities(entities);

                    for (HitResult h : results) {
                        if (h instanceof EntityHitResult r) {
                            if (r.getEntity() instanceof ItemEntity) {
                                hitResult = r;
                                break;
                            }
                        }
                    }

                } if (sp.type == SpellType.SELF && canCallSelf) {

                    canCallSelf = false;
                    Vec3d hitPos = player.getPos().add(0, player.getHeight() / 2.0, 0); // middle of body

                    EntityHitResult simulatedHit = new EntityHitResult(player, hitPos);

                    if (hasMS.equalsIgnoreCase("us") || hasMS.equalsIgnoreCase("our")) {
                        handleMS(simulatedHit, player);
                    } else {
                        runSpell(simulatedHit, this, casted);
                    }

                }

            }

            if (!canContinue && !this.popped) {
                if (!this.getWorld().getBlockState(blockHit.getBlockPos()).isAir()) {
                    this.discard();
                }
                return;
            }

            if (spellInst.contains(Repair.SPELL)) {
                Repair.SPELL.behavior.onBlockHit(this, spell, blockHit);
            }

            if (this.getWorld().getBlockState(blockHit.getBlockPos()).isAir() && !this.popped) {
                return;
            }

            handleMS(hitResult, player);
            runSpell(hitResult, this, casted);
            this.discard();

        }
    }

    public boolean doesFizzle() {
        return this.dataTracker.get(DOES_FIZZLE);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void pop() {
        if (this.isRemoved()) {
            return;
        }

        this.popRequested = true;
    }

    public static void popAll(PlayerEntity player) {
        if (player == null) {
            return;
        }

        ServerWorld world = Objects.requireNonNull(player.getServer()).getWorld(player.getWorld().getRegistryKey());

        assert world != null;
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof SpellBoltEntity bolt && bolt.getOwner() == player) {
                bolt.pop();
            }
        }
    }

    private void spawnParticles(boolean fizzles) {
        float r = getColorR();
        float g = getColorG();
        float b = getColorB();

        if (fizzles) {
            r = (r < 0.5f) ? Math.min(r * 1.5f, 1f) : r * 0.5f;
            g = (g < 0.5f) ? Math.min(g * 1.5f, 1f) : g * 0.5f;
            b = (b < 0.5f) ? Math.min(b * 1.5f, 1f) : b * 0.5f;
        }

        Random rand = this.getWorld().random;

        double ox = (rand.nextDouble() - 0.5) * 0.1;
        double oy = (rand.nextDouble() - 0.5) * 0.1;
        double oz = (rand.nextDouble() - 0.5) * 0.1;

        this.getWorld().addParticle(
                new SmokeParticleEffect(r, g, b),
                this.getX() + ox, this.getY() + oy, this.getZ() + oz,
                0, 0, 0
        );

        this.getWorld().addParticle(
                new SoulParticleEffect(r, g, b),
                this.getX() - ox, this.getY() - oy, this.getZ() - oz,
                0, 0, 0
        );
    }

    public int lifeTime = 0;

    @Override
    public void tick() {
        super.tick();

        if (spellInst.isEmpty()) {
            spellInst = KeywordHelper.isKeyword(this.spell);

            if (!spellInst.isEmpty()) {
                for (Spell sp : spellInst) {

                    if (this.getOwner() instanceof PlayerEntity player) {
                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
                        if (!data.knowsSpell(sp.ID)) {
                            continue;
                        }
                    }

                    if ((
                            sp.type == SpellType.ACTION
                            || sp.type == SpellType.MODIFIER
                            || sp.type == SpellType.BLACK_MAGIC
                            || sp.type == SpellType.BLUE_MAGIC
                            || sp.type == SpellType.GREEN_MAGIC
                            || sp.type == SpellType.PURPLE_MAGIC
                            || sp.type == SpellType.RED_MAGIC
                            || sp.type == SpellType.WHITE_MAGIC
                    ) && !casted.contains(sp)) {
                        casted.add(sp);
                    }
                }

                if (casted.isEmpty()) {
                    lifeTime = 76;
                    this.discard();
                    return;
                }
            }
        }

        if (!hasChecked && !casted.isEmpty()) {
            hasChecked = true;
            doesFizzle = ManaHelper.consume(this, casted, true, 0);
            hasMS = KeywordHelper.checkMS(this.spell);
            this.dataTracker.set(DOES_FIZZLE, doesFizzle);
        }

        if (popRequested && hasChecked) {
            popRequested = false;

            BlockHitResult hit = new BlockHitResult(
                    this.getPos(),
                    Direction.UP,
                    this.getBlockPos(),
                    false
            );

            this.popped = true;
            this.onCollision(hit);

            return;
        }

        if (this.getWorld().isClient) {
            spawnParticles(doesFizzle());
        }

        lifeTime++;

        if (lifeTime >= 75) {
            this.discard();
        }

    }

    /*
     * ============================================================
     * MULTI-SUBJECT / SHAPE SYSTEM
     * ============================================================
     *
     * All shapes are defined in LOCAL SPACE:
     *
     *      X = right
     *      Y = world-up
     *      Z = forward
     *
     * Shapes rotate ONLY horizontally.
     *
     * The projectile's vertical velocity is intentionally ignored.
     *
     * This means:
     *
     *  - A wall rotates left/right but never tilts.
     *  - A plane rotates left/right but stays horizontal.
     *  - A cube rotates around the Y axis only.
     *  - A dome stays upright.
     *  - A pillar always remains vertically aligned.
     *  - Entities and blocks use the same orientation.
     *
     * ============================================================
     */

    private static final double SHAPE_EPSILON = 0.001D;

    /**
     * Stores the three axes used by the local shape system.
     *
     * forward = horizontal direction of the projectile
     * right   = local X axis
     * up      = always world-up
     */
    private static class ShapeBasis {

        final Vec3d forward;
        final Vec3d right;
        final Vec3d up;

        ShapeBasis(Vec3d forward, Vec3d right, Vec3d up) {
            this.forward = forward;
            this.right = right;
            this.up = up;
        }
    }

    /**
     * Creates a horizontal-only orientation from the projectile velocity.
     *
     * IMPORTANT:
     *
     * Vertical projectile velocity is completely ignored.
     *
     * This means shooting a spell upward or downward does NOT tilt
     * the shape.
     */
    private ShapeBasis getShapeBasis() {

        Vec3d forward = this.getVelocity();

        /*
         * Remove vertical velocity.
         *
         * This is the important part that makes all shapes yaw-only.
         */
        forward = new Vec3d(
                forward.x,
                0,
                forward.z
        );

        /*
         * If the projectile is moving almost perfectly vertically,
         * its horizontal velocity will be almost zero.
         *
         * Fall back to its rotation vector.
         */
        if (forward.lengthSquared() < 1.0E-8D) {

            Vec3d rotation = this.getRotationVector();

            forward = new Vec3d(
                    rotation.x,
                    0,
                    rotation.z
            );
        }

        /*
         * Final fallback.
         */
        if (forward.lengthSquared() < 1.0E-8D) {
            forward = new Vec3d(0, 0, 1);
        }

        forward = forward.normalize();

        /*
         * Horizontal right vector.
         */
        Vec3d right = new Vec3d(
                -forward.z,
                0,
                forward.x
        ).normalize();

        /*
         * Y is ALWAYS world vertical.
         */
        Vec3d up = new Vec3d(
                0,
                1,
                0
        );

        return new ShapeBasis(
                forward,
                right,
                up
        );
    }

    /**
     * Converts a point from shape-local coordinates to world coordinates.
     */
    private Vec3d rotateLocalToWorld(
            double x,
            double y,
            double z
    ) {

        ShapeBasis basis = getShapeBasis();

        return this.getPos()
                .add(basis.right.multiply(x))
                .add(basis.up.multiply(y))
                .add(basis.forward.multiply(z));
    }

    /**
     * Converts a world position into the projectile's local shape space.
     *
     * This is the inverse of rotateLocalToWorld().
     */
    private Vec3d worldToLocal(
            Vec3d worldPosition,
            Vec3d center
    ) {

        ShapeBasis basis = getShapeBasis();

        Vec3d relative =
                worldPosition.subtract(center);

        return new Vec3d(
                relative.dotProduct(basis.right),
                relative.dotProduct(basis.up),
                relative.dotProduct(basis.forward)
        );
    }

    /**
     * Creates a BlockHitResult at a world-space point.
     */
    private BlockHitResult createBlockHit(Vec3d pos) {

        BlockPos blockPos =
                BlockPos.ofFloored(pos);

        return new BlockHitResult(
                pos,
                Direction.UP,
                blockPos,
                false
        );
    }

    /**
     * Calculates a conservative world-space bounding box around a
     * local-space shape.
     *
     * This is only used to find candidate entities.
     * Candidates are then tested again in local space.
     */
    private Box getRotatedBounds(
            Vec3d center,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {

        Vec3d[] corners = new Vec3d[] {

                rotateAroundCenter(
                        center,
                        minX,
                        minY,
                        minZ
                ),

                rotateAroundCenter(
                        center,
                        minX,
                        minY,
                        maxZ
                ),

                rotateAroundCenter(
                        center,
                        minX,
                        maxY,
                        minZ
                ),

                rotateAroundCenter(
                        center,
                        minX,
                        maxY,
                        maxZ
                ),

                rotateAroundCenter(
                        center,
                        maxX,
                        minY,
                        minZ
                ),

                rotateAroundCenter(
                        center,
                        maxX,
                        minY,
                        maxZ
                ),

                rotateAroundCenter(
                        center,
                        maxX,
                        maxY,
                        minZ
                ),

                rotateAroundCenter(
                        center,
                        maxX,
                        maxY,
                        maxZ
                )
        };

        double minWorldX =
                Double.POSITIVE_INFINITY;

        double minWorldY =
                Double.POSITIVE_INFINITY;

        double minWorldZ =
                Double.POSITIVE_INFINITY;

        double maxWorldX =
                Double.NEGATIVE_INFINITY;

        double maxWorldY =
                Double.NEGATIVE_INFINITY;

        double maxWorldZ =
                Double.NEGATIVE_INFINITY;

        for (Vec3d corner : corners) {

            minWorldX =
                    Math.min(
                            minWorldX,
                            corner.x
                    );

            minWorldY =
                    Math.min(
                            minWorldY,
                            corner.y
                    );

            minWorldZ =
                    Math.min(
                            minWorldZ,
                            corner.z
                    );

            maxWorldX =
                    Math.max(
                            maxWorldX,
                            corner.x
                    );

            maxWorldY =
                    Math.max(
                            maxWorldY,
                            corner.y
                    );

            maxWorldZ =
                    Math.max(
                            maxWorldZ,
                            corner.z
                    );
        }

        return new Box(
                minWorldX,
                minWorldY,
                minWorldZ,
                maxWorldX,
                maxWorldY,
                maxWorldZ
        );
    }

    /**
     * Same as rotateLocalToWorld(), but allows an arbitrary center.
     */
    private Vec3d rotateAroundCenter(
            Vec3d center,
            double x,
            double y,
            double z
    ) {

        ShapeBasis basis =
                getShapeBasis();

        return center
                .add(basis.right.multiply(x))
                .add(basis.up.multiply(y))
                .add(basis.forward.multiply(z));
    }

    /**
     * Tests whether an entity's bounding box intersects a local-space
     * sphere.
     */
    private boolean entityIntersectsSphere(
            Entity entity,
            Vec3d center,
            double radius
    ) {

        Box box =
                entity.getBoundingBox();

        double minX =
                Double.POSITIVE_INFINITY;

        double minY =
                Double.POSITIVE_INFINITY;

        double minZ =
                Double.POSITIVE_INFINITY;

        double maxX =
                Double.NEGATIVE_INFINITY;

        double maxY =
                Double.NEGATIVE_INFINITY;

        double maxZ =
                Double.NEGATIVE_INFINITY;

        double[] xs = {
                box.minX,
                box.maxX
        };

        double[] ys = {
                box.minY,
                box.maxY
        };

        double[] zs = {
                box.minZ,
                box.maxZ
        };

        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {

                    Vec3d local =
                            worldToLocal(
                                    new Vec3d(x, y, z),
                                    center
                            );

                    minX =
                            Math.min(
                                    minX,
                                    local.x
                            );

                    minY =
                            Math.min(
                                    minY,
                                    local.y
                            );

                    minZ =
                            Math.min(
                                    minZ,
                                    local.z
                            );

                    maxX =
                            Math.max(
                                    maxX,
                                    local.x
                            );

                    maxY =
                            Math.max(
                                    maxY,
                                    local.y
                            );

                    maxZ =
                            Math.max(
                                    maxZ,
                                    local.z
                            );
                }
            }
        }

        double closestX =
                Math.max(
                        minX,
                        Math.min(0, maxX)
                );

        double closestY =
                Math.max(
                        minY,
                        Math.min(0, maxY)
                );

        double closestZ =
                Math.max(
                        minZ,
                        Math.min(0, maxZ)
                );

        return closestX * closestX
                + closestY * closestY
                + closestZ * closestZ
                <= radius * radius;
    }

    /**
     * Tests whether an entity intersects a local-space box.
     */
    private boolean entityIntersectsBox(
            Entity entity,
            Vec3d center,
            double halfX,
            double halfY,
            double halfZ
    ) {

        Box box =
                entity.getBoundingBox();

        double minX =
                Double.POSITIVE_INFINITY;

        double minY =
                Double.POSITIVE_INFINITY;

        double minZ =
                Double.POSITIVE_INFINITY;

        double maxX =
                Double.NEGATIVE_INFINITY;

        double maxY =
                Double.NEGATIVE_INFINITY;

        double maxZ =
                Double.NEGATIVE_INFINITY;

        double[] xs = {
                box.minX,
                box.maxX
        };

        double[] ys = {
                box.minY,
                box.maxY
        };

        double[] zs = {
                box.minZ,
                box.maxZ
        };

        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {

                    Vec3d local =
                            worldToLocal(
                                    new Vec3d(x, y, z),
                                    center
                            );

                    minX =
                            Math.min(
                                    minX,
                                    local.x
                            );

                    minY =
                            Math.min(
                                    minY,
                                    local.y
                            );

                    minZ =
                            Math.min(
                                    minZ,
                                    local.z
                            );

                    maxX =
                            Math.max(
                                    maxX,
                                    local.x
                            );

                    maxY =
                            Math.max(
                                    maxY,
                                    local.y
                            );

                    maxZ =
                            Math.max(
                                    maxZ,
                                    local.z
                            );
                }
            }
        }

        return maxX >= -halfX
                && minX <= halfX
                && maxY >= -halfY
                && minY <= halfY
                && maxZ >= -halfZ
                && minZ <= halfZ;
    }

    /**
     * Tests whether an entity intersects the hollow dome shell.
     *
     * The dome is now a COMPLETE spherical shell.
     *
     * It is NOT restricted to the upper hemisphere.
     */
    private boolean entityIntersectsDome(
            Entity entity,
            Vec3d center,
            double radius
    ) {

        Box box =
                entity.getBoundingBox();

        double outerRadiusSq =
                radius * radius;

        /*
         * Keep the same shell thickness as the block dome.
         */
        double shellThickness = 3.0D;

        double innerRadius =
                Math.max(
                        0,
                        radius - shellThickness
                );

        double innerRadiusSq =
                innerRadius * innerRadius;

        /*
         * Check the entity center first.
         */
        Vec3d localCenter =
                worldToLocal(
                        entity.getPos(),
                        center
                );

        double centerDistSq =
                localCenter.x * localCenter.x
                        + localCenter.y * localCenter.y
                        + localCenter.z * localCenter.z;

        if (centerDistSq <= outerRadiusSq
                && centerDistSq >= innerRadiusSq) {

            return true;
        }

        /*
         * Then check bounding-box corners.
         */
        double[] xs = {
                box.minX,
                box.maxX
        };

        double[] ys = {
                box.minY,
                box.maxY
        };

        double[] zs = {
                box.minZ,
                box.maxZ
        };

        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {

                    Vec3d local =
                            worldToLocal(
                                    new Vec3d(x, y, z),
                                    center
                            );

                    double distSq =
                            local.x * local.x
                                    + local.y * local.y
                                    + local.z * local.z;

                    if (distSq <= outerRadiusSq
                            && distSq >= innerRadiusSq) {

                        return true;
                    }
                }
            }
        }

        return false;
    }


    /*
     * ============================================================
     * SPHERE
     * ============================================================
     */

    private List<BlockHitResult> getBlocksInSphere(
            Vec3d center,
            double radius
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        int r =
                (int) Math.ceil(radius);

        double radiusSq =
                radius * radius;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {

                    double distanceSq =
                            x * x
                                    + y * y
                                    + z * z;

                    if (distanceSq > radiusSq) {
                        continue;
                    }

                    Vec3d pos =
                            rotateAroundCenter(
                                    center,
                                    x,
                                    y,
                                    z
                            );

                    results.add(
                            createBlockHit(pos)
                    );
                }
            }
        }

        return results;
    }

    public List<Entity> getEntitiesInSphere(
            World world,
            Vec3d center,
            double radius
    ) {

        Box searchBox =
                getRotatedBounds(
                        center,
                        -radius,
                        -radius,
                        -radius,
                        radius,
                        radius,
                        radius
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsSphere(
                                entity,
                                center,
                                radius
                        )
        );
    }


    /*
     * ============================================================
     * WALL
     * ============================================================
     */

    public List<BlockHitResult> getWall(
            World world,
            Vec3d center,
            int width,
            int height
            
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        for (int y = 0; y < height; y++) {

            int localY =
                    downwards ? -y : y;

            for (int x = -width; x <= width; x++) {

                Vec3d pos =
                        rotateAroundCenter(
                                center,
                                x,
                                localY,
                                0
                        );

                results.add(
                        createBlockHit(pos)
                );
            }
        }

        return results;
    }

    public List<Entity> getEntitiesInWall(
            World world,
            Vec3d center,
            double width,
            double height
            
    ) {

        double minY =
                downwards ? -height : 0;

        double maxY =
                downwards ? 0 : height;

        Box searchBox =
                getRotatedBounds(
                        center,
                        -width - 0.5,
                        minY,
                        -0.5,
                        width + 0.5,
                        maxY,
                        0.5
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsBox(
                                entity,
                                center,
                                width + 0.5,
                                Math.max(
                                        Math.abs(minY),
                                        Math.abs(maxY)
                                ),
                                0.5
                        )
        );
    }


    /*
     * ============================================================
     * PLANE
     * ============================================================
     */

    public List<BlockHitResult> getPlane(
            World world,
            Vec3d center,
            int radius
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                Vec3d pos =
                        rotateAroundCenter(
                                center,
                                x,
                                0,
                                z
                        );

                results.add(
                        createBlockHit(pos)
                );
            }
        }

        return results;
    }

    public List<Entity> getEntitiesInPlane(
            World world,
            Vec3d center,
            double radius
    ) {

        Box searchBox =
                getRotatedBounds(
                        center,
                        -radius,
                        -0.5,
                        -radius,
                        radius,
                        0.5,
                        radius
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsBox(
                                entity,
                                center,
                                radius,
                                0.5,
                                radius
                        )
        );
    }


    /*
     * ============================================================
     * DOME
     *
     * Complete hollow spherical shell.
     *
     * It is NOT a hemisphere.
     *
     * shellThickness controls how thick the shell is.
     * ============================================================
     */

    public List<BlockHitResult> getDome(
            World world,
            Vec3d center,
            double radius
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        /*
         * Thick shell.
         *
         * 3 blocks gives a substantial shell while leaving
         * the interior completely hollow.
         */
        double shellThickness = 3.0D;

        double innerRadius =
                Math.max(
                        0,
                        radius - shellThickness
                );

        double outerRadiusSq =
                radius * radius;

        double innerRadiusSq =
                innerRadius * innerRadius;

        int r =
                (int) Math.ceil(radius);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {

                    double distanceSq =
                            x * x
                                    + y * y
                                    + z * z;

                    /*
                     * Outside the sphere.
                     */
                    if (distanceSq > outerRadiusSq) {
                        continue;
                    }

                    /*
                     * Completely inside the hollow area.
                     */
                    if (distanceSq < innerRadiusSq) {
                        continue;
                    }

                    /*
                     * Everything that remains belongs
                     * to the thick outer shell.
                     */
                    Vec3d pos =
                            rotateAroundCenter(
                                    center,
                                    x,
                                    y,
                                    z
                            );

                    results.add(
                            createBlockHit(pos)
                    );
                }
            }
        }

        return results;
    }

    public List<Entity> getEntitiesInDome(
            World world,
            Vec3d center,
            double radius
    ) {

        Box searchBox =
                getRotatedBounds(
                        center,
                        -radius,
                        -radius,
                        -radius,
                        radius,
                        radius,
                        radius
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsDome(
                                entity,
                                center,
                                radius
                        )
        );
    }


    /*
     * ============================================================
     * CUBE
     * ============================================================
     */

    public List<BlockHitResult> getCube(
            World world,
            Vec3d center,
            int radius
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Vec3d pos =
                            rotateAroundCenter(
                                    center,
                                    x,
                                    y,
                                    z
                            );

                    results.add(
                            createBlockHit(pos)
                    );
                }
            }
        }

        return results;
    }

    public List<Entity> getEntitiesInCube(
            World world,
            Vec3d center,
            double radius
    ) {

        Box searchBox =
                getRotatedBounds(
                        center,
                        -radius,
                        -radius,
                        -radius,
                        radius,
                        radius,
                        radius
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsBox(
                                entity,
                                center,
                                radius,
                                radius,
                                radius
                        )
        );
    }


    /*
     * ============================================================
     * PILLAR
     *
     * Because LOCAL Y is now always world Y, the pillar remains
     * vertically upright.
     * ============================================================
     */

    public List<BlockHitResult> getPillar(
            World world,
            Vec3d center,
            int height
            
    ) {

        List<BlockHitResult> results =
                new ArrayList<>();

        for (int y = 0; y < height; y++) {

            int localY =
                    downwards ? -y : y;

            Vec3d pos =
                    rotateAroundCenter(
                            center,
                            0,
                            localY,
                            0
                    );

            results.add(
                    createBlockHit(pos)
            );
        }

        return results;
    }

    public List<Entity> getEntitiesInPillar(
            World world,
            Vec3d center,
            double height
            
    ) {

        double minY =
                downwards ? -height : 0;

        double maxY =
                downwards ? 0 : height;

        double halfWidth =
                0.5D;

        Box searchBox =
                getRotatedBounds(
                        center,
                        -halfWidth,
                        minY,
                        -halfWidth,
                        halfWidth,
                        maxY,
                        halfWidth
                );

        double halfHeight =
                Math.max(
                        Math.abs(minY),
                        Math.abs(maxY)
                );

        return world.getEntitiesByClass(
                Entity.class,
                searchBox,
                entity ->
                        entityIntersectsBox(
                                entity,
                                center,
                                halfWidth,
                                halfHeight,
                                halfWidth
                        )
        );
    }


    /*
     * ============================================================
     * ENTITY -> HIT RESULT
     * ============================================================
     */

    public static List<HitResult> convertEntities(
            List<Entity> entities
    ) {

        List<HitResult> results =
                new ArrayList<>();

        for (Entity entity : entities) {

            Vec3d hitPos =
                    entity.getPos()
                            .add(
                                    0,
                                    entity.getHeight() / 2.0D,
                                    0
                            );

            results.add(
                    new EntityHitResult(
                            entity,
                            hitPos
                    )
            );
        }

        return results;
    }


    /*
     * ============================================================
     * RESULT PROCESSING
     *
     * Results are processed in their generated order.
     *
     * There is:
     *
     *  - no shuffle
     *  - no target skipping
     *  - no progressively increasing delay
     *
     * Every spell gets the same short delay.
     * ============================================================
     */

    private void processResults(
            List<? extends HitResult> results,
            PlayerEntity player
    ) {

        if (results == null || results.isEmpty()) {
            this.discard();
            return;
        }

        List<HitResult> orderedResults =
                new ArrayList<>(results);

        // Shuffle results
        Collections.shuffle(orderedResults);

        int total = orderedResults.size();

        for (int i = 0; i < total; i++) {

            HitResult result = orderedResults.get(i);

            int speed = 1 + (i / 30);
            int delay = speed + 2;

            int index = i;

            Scheduler.schedule(
                    delay,
                    () -> {

                        if (!player.isSneaking() && index > 3) {
                            return;
                        }

                        runSpell(
                                result,
                                this,
                                casted
                        );

                        if (index == total - 1) {
                            this.discard();
                        }
                    }
            );
        }

        this.discard();
    }


    /*
     * ============================================================
     * WALL
     * ============================================================
     */

    public void wall(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            Vec3d center =
                    hitResult.getPos();

            List<BlockHitResult> results =
                    getWall(
                            getWorld(),
                            center,
                            (int) MULTI_RADIUS,
                            (int) (MULTI_RADIUS * 2)
                    );

            processResults(
                    results,
                    player
            );

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInWall(
                            getWorld(),
                            center,
                            MULTI_RADIUS,
                            MULTI_RADIUS * 2
                    );

            entities.remove(this);

            if (!hasMS.equalsIgnoreCase("us")
                    && !hasMS.equalsIgnoreCase("our")) {

                entities.remove(player);
            }

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * PLANE
     * ============================================================
     */

    public void plane(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            Vec3d center =
                    hitResult.getPos();

            processResults(
                    getPlane(
                            getWorld(),
                            center,
                            (int) MULTI_RADIUS
                    ),
                    player
            );

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInPlane(
                            getWorld(),
                            center,
                            MULTI_RADIUS
                    );

            entities.remove(this);

            if (!hasMS.equalsIgnoreCase("us")
                    && !hasMS.equalsIgnoreCase("our")) {

                entities.remove(player);
            }

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * SPHERE
     * ============================================================
     */

    public void sphere(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            Vec3d center =
                    hitResult.getPos();

            List<BlockHitResult> results =
                    getBlocksInSphere(
                            center,
                            Math.round(
                                    MULTI_RADIUS * 1.5F
                            )
                    );

            if (!results.isEmpty()) {

                results.sort(
                        Comparator.comparingDouble(
                                result ->
                                        result.getPos()
                                                .squaredDistanceTo(center)
                        )
                );

                processResults(
                        results,
                        player
                );

            } else {

                this.discard();
            }

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInSphere(
                            getWorld(),
                            center,
                            Math.round(
                                    MULTI_RADIUS * 1.5F
                            )
                    );

            entities.remove(this);

            if (!hasMS.equalsIgnoreCase("us")
                    && !hasMS.equalsIgnoreCase("our")) {

                entities.remove(player);
            }

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * DOME
     * ============================================================
     */

    public void dome(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            Vec3d center =
                    hitResult.getPos();

            processResults(
                    getDome(
                            getWorld(),
                            center,
                            Math.round(
                                    MULTI_RADIUS * 1.5F
                            )
                    ),
                    player
            );

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInDome(
                            getWorld(),
                            center,
                            Math.round(
                                    MULTI_RADIUS * 1.5F
                            )
                    );

            entities.remove(this);
            entities.remove(player);

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * CUBE
     * ============================================================
     */

    public void cube(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            processResults(
                    getCube(
                            getWorld(),
                            hitResult.getPos(),
                            (int) MULTI_RADIUS
                    ),
                    player
            );

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInCube(
                            getWorld(),
                            center,
                            MULTI_RADIUS
                    );

            entities.remove(this);

            if (!hasMS.equalsIgnoreCase("us")
                    && !hasMS.equalsIgnoreCase("our")) {

                entities.remove(player);
            }

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * PILLAR
     * ============================================================
     */

    public void pillar(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hitResult instanceof BlockHitResult) {

            Vec3d center =
                    hitResult.getPos();

            List<BlockHitResult> results =
                    getPillar(
                            getWorld(),
                            center,
                            (int) (MULTI_RADIUS * 2)
                    );

            processResults(
                    results,
                    player
            );

        } else if (hitResult instanceof EntityHitResult) {

            Vec3d center =
                    hitResult.getPos();

            if (hasMS.equalsIgnoreCase("us")
                    || hasMS.equalsIgnoreCase("our")) {

                center =
                        player.getPos();
            }

            List<Entity> entities =
                    getEntitiesInPillar(
                            getWorld(),
                            center,
                            MULTI_RADIUS * 2
                    );

            entities.remove(this);

            if (!hasMS.equalsIgnoreCase("us")
                    && !hasMS.equalsIgnoreCase("our")) {

                entities.remove(player);
            }

            processResults(
                    convertEntities(entities),
                    player
            );
        }
    }


    /*
     * ============================================================
     * MULTI-SHAPE DISPATCH
     * ============================================================
     */

    public void handleMS(
            HitResult hitResult,
            PlayerEntity player
    ) {

        if (hasMS == null || hasMS.isEmpty()) {
            return;
        }

        if (Arrays.stream(KeywordHelper.wallKeywords)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            wall(
                    hitResult,
                    player
            );

            return;
        }

        if (Arrays.stream(KeywordHelper.sphereKeyword)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            sphere(
                    hitResult,
                    player
            );

            return;
        }

        if (Arrays.stream(KeywordHelper.domeKeyword)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            dome(
                    hitResult,
                    player
            );

            return;
        }

        if (Arrays.stream(KeywordHelper.squareKeyword)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            cube(
                    hitResult,
                    player
            );

            return;
        }

        if (Arrays.stream(KeywordHelper.planeKeyword)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            plane(
                    hitResult,
                    player
            );

            return;
        }

        if (Arrays.stream(KeywordHelper.pillarKeywords)
                .anyMatch(
                        k -> k.equalsIgnoreCase(hasMS)
                )) {

            pillar(
                    hitResult,
                    player
            );

            return;
        }

        /*
         * Unknown multi-subject keyword:
         * retain the previous fallback behavior.
         */
        sphere(
                hitResult,
                player
        );
    }

}