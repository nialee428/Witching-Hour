package net.nia.witchinghour.magic.spells.purplemagic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.*;

public class Teleportation {
    public static final Spell SPELL = new Spell();
    public static final String ID = "Teleportation";

    public static BlockPos scrambleSpherical(BlockPos pos, double radius, Random random, World world) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * random.nextDouble() - 1);

        double minFraction = 0.75;
        double minR = minFraction * radius;

        double r = minR + (radius - minR) * Math.cbrt(random.nextDouble());

        double dx = r * Math.sin(phi) * Math.cos(theta);
        double dy = r * Math.cos(phi);
        double dz = r * Math.sin(phi) * Math.sin(theta);

        int minY = -60;
        int maxY = 1000;

        double newY = pos.getY() + dy / 4;

        // Clamp to world height limits
        if (newY < minY) newY = minY;
        if (newY > maxY) newY = maxY;

        return BlockPos.ofFloored(
                pos.getX() + dx,
                newY,
                pos.getZ() + dz
        );
    }

    public static void teleportEntities(List<Entity> entities, PlayerEntity caster, BlockPos hitPos, SpellBoltEntity bolt, ServerWorld world) {
        Vec3d targetVec = Vec3d.ofCenter(hitPos.up());
        for (Entity s : entities) {

            double x = hitPos.up().getX() + 0.5;
            double y = hitPos.up().getY();
            if (y < -61) {
                y = -61;
            }

            double z = hitPos.up().getZ() + 0.5;

            PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(caster);
            int mana = data.getMana();

            double distToPoint = s.getPos().distanceTo(targetVec);
            double distToCaster = s.getPos().distanceTo(caster.getPos());

            int manaCost = (int)Math.ceil((distToPoint + distToCaster) / 15.0);

            List<Spell> casted = new ArrayList<>();
            casted.add(SPELL);

            if (distToPoint < 1) {
                continue;
            }

            if (ManaHelper.consume(bolt, casted, false, manaCost)) {
                continue;
            }

            if (Shield.shieldedSpell(caster, bolt, new BlockHitResult(hitPos.toCenterPos(), Direction.UP, hitPos, false), caster.getWorld())
            || Shield.shieldedSpell(caster, bolt, new BlockHitResult(s.getPos(), Direction.UP, hitPos, false), caster.getWorld())) {
                bolt.discard();
                return;
            }

            if (s instanceof ServerPlayerEntity player) {
                player.teleport(world, x, y, z, player.getYaw(), player.getPitch());
            } else {
                if (s.getWorld() != world) {
                    s = caster.moveToWorld(world);
                }

                assert s != null;
                s.refreshPositionAndAngles(x, y, z, s.getYaw(), s.getPitch());
            }

            bolt.getWorld().playSound(
                    null,
                    s.getX(),
                    s.getY(),
                    s.getZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    SoundCategory.AMBIENT,
                    1.0f,
                    1.0f
            );

            bolt.discard();
        }

    }

    public static void attemptTeleport(SpellBoltEntity bolt, String spell, HitResult hit) {

        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
        if (player == null) {
            return;
        }

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

        MinecraftServer server = player.getServer();
        assert server != null;

        spell = spell.toLowerCase();

        String[] words = spell
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        String keyword = "";

        for (int i = words.length - 1; i >= 0; i--) {
            String w = words[i];

            if (Arrays.stream(Teleportation.SPELL.keywords).anyMatch(s -> s.equalsIgnoreCase(w))) {

                String[] parts = spell.split(w);

                if (parts.length > 1) {
                    spell = parts[1];
                    keyword = w;
                }
            }
        }

        if (!spell.contains(" to ")) {
            return;
        }

        // Separate subject(s) from target at "to"
        String[] split = spell.split(" to ", 2);
        if (split.length < 2) {
            return; // or handle error
        }

        String first = split[0];
        String second = split[1];

        var firstSelf = KeywordHelper.hasTargetWord(first, SpellType.SELF);
        var secondSelf = KeywordHelper.hasTargetWord(second, SpellType.SELF);

        var firstEntity = KeywordHelper.hasTargetWord(first, SpellType.ENTITY);
        var secondEntity = KeywordHelper.hasTargetWord(second, SpellType.ENTITY);

        var firstGeneral = KeywordHelper.hasTargetWord(first, SpellType.GENERAL);
        var secondGeneral = KeywordHelper.hasTargetWord(second, SpellType.GENERAL);

        boolean subject_Self = firstSelf != null && firstSelf.type == SpellType.SELF;
        boolean target_Self = secondSelf != null && secondSelf.type == SpellType.SELF;

        boolean subject_Entity = firstEntity != null && firstEntity.type == SpellType.ENTITY;
        boolean target_Entity = secondEntity != null && secondEntity.type == SpellType.ENTITY;

        boolean subject_General = firstGeneral != null && firstGeneral.type == SpellType.GENERAL;
        boolean target_General = secondGeneral != null && secondGeneral.type == SpellType.GENERAL;

        System.out.println("subject_Self = " + subject_Self);
        System.out.println("target_Self = " + target_Self);

        System.out.println("subject_Entity = " + subject_Entity);
        System.out.println("target_Entity = " + target_Entity);

        System.out.println("subject_General = " + subject_General);
        System.out.println("target_General = " + target_General);

        List<Entity> s = Names.getPlayersByNames(server, first);
        BlockPos t = null;
        String dim = player.getWorld().getRegistryKey().getValue().toString();

        if (subject_Self) {
            s.add(player);
        }

        if (subject_Entity || subject_General) {
            if (hit instanceof EntityHitResult entityHitResult) {
                if (!s.contains(entityHitResult.getEntity())) {
                    if (!subject_Self && entityHitResult.getEntity() != player) {
                        s.add(entityHitResult.getEntity());
                    }
                }
            }
        }

        if (target_Entity) {
            if (hit instanceof EntityHitResult entityHitResult) {
                if (!target_Self && entityHitResult.getEntity() != player) {
                    t = entityHitResult.getEntity().getBlockPos();
                }
            }
        } if (target_General) {
            if (hit instanceof EntityHitResult entityHitResult) {
                if (!target_Self && entityHitResult.getEntity() != player) {
                    t = entityHitResult.getEntity().getBlockPos();
                }
            }

            if (hit instanceof BlockHitResult blockHitResult) {
                t = blockHitResult.getBlockPos();
            }
        } if (target_Self) {
            t = player.getBlockPos();
        }


        Entity obtainedPlayer_ = Names.getFirstPlayerByName(server, second);
        if (obtainedPlayer_ != null) {
            t = obtainedPlayer_.getBlockPos();
            dim = obtainedPlayer_.getWorld().getRegistryKey().getValue().toString();
        }

        for (Map.Entry<String, PlayerMagicData.StoredLocation> entry : data.getAllPos()) {
            String key = entry.getKey();
            PlayerMagicData.StoredLocation loc = entry.getValue();

            if (spell.contains(key)) {
                t = loc.pos; // the BlockPos
                dim = loc.dimension; // the dimension string
            }
        }

        if (s.isEmpty() || t == null) {
            return;
        }

        if (SpellBoltEntity.hasRandomKeyword(spell)) {

            bolt.randomizeHit(hit, bolt, true);
            hit = bolt.randomizedHit;

            if (hit instanceof BlockHitResult blockHitResult) {
                t = blockHitResult.getBlockPos();
            }
        }

        Iterator<Entity> it = s.iterator();
        while (it.hasNext()) {
            Entity subject = it.next();
            if (subject.getBlockPos().equals(t)) {
                it.remove(); // safe
                continue;
            }

            if (subject instanceof PlayerEntity p) {
                if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                    SpellProtection.protect(bolt, spell, new EntityHitResult(subject, subject.getPos()), ID);
                    return;
                }

                if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                    return;
                }
            }
        }

        if (s.isEmpty()) {
            return;
        }

        ServerWorld world = server.getWorld(RegistryKey.of(
                RegistryKeys.WORLD,
                new Identifier(dim)
        ));

        teleportEntities(s, player, t, bolt, world);
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        NbtCompound waterNbt = new NbtCompound();
        waterNbt.putString("Potion", "minecraft:water");

        RECIPE.put(new SpellIngredient(Items.ENDER_PEARL, null), 4);
        RECIPE.put(new SpellIngredient(Items.SPLASH_POTION, waterNbt), 1); // WATER BOTTLE
        RECIPE.put(new SpellIngredient(Items.ENDER_EYE, null), 4);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "teleport", "teleported", "teleporting", "transport", "transported", "transporting", "take",
                        "taking", "taken", "took", "bring", "brought", "send", "sending", "sent", "takes", "sends", "brings",
                        "teleports", "transports"
                },


                new double[] {
                        10.0, 15.0, 10.0, 1.0
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

                        attemptTeleport(bolt, spell, hit);
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

                        attemptTeleport(bolt, spell, hit);
                    }
                },

                RECIPE
        );

    }
}