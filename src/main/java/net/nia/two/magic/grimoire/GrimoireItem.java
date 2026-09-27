package net.nia.witchinghour.magic.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;

public class GrimoireItem extends Item {
    public GrimoireItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        if (!world.isClient) {
            BlockPos spawnPos = context.getBlockPos().offset(context.getSide());
            ServerWorld serverWorld = (ServerWorld) world;

            assert context.getPlayer() != null;
            boolean hasGrimoire = false;

            var entities = serverWorld.getEntitiesByType(WitchingHourEntities.GRIMOIRE, Entity::isAlive);

            for (GrimoireEntity g : entities) {
                if (g.getOwnerUuid().equals(context.getPlayer().getUuid())) {
                    hasGrimoire = true;
                    break;
                }
            }

            if (!hasGrimoire) {
                GrimoireEntity grimoire = WitchingHourEntities.GRIMOIRE.create(
                        world
                );

                grimoire.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                serverWorld.spawnEntityAndPassengers(grimoire);

                grimoire.setOwner(context.getPlayer());
                grimoire.setOwnerUuid(context.getPlayer().getUuid());
                grimoire.setTamed(true);

                serverWorld.playSound(
                        null,
                        spawnPos.getX(),
                        spawnPos.getY(),
                        spawnPos.getZ(),
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                        SoundCategory.NEUTRAL,
                        1.0f,
                        3.75f
                );

                PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(context.getPlayer());

                data.unlockSpell("Damage");
                data.unlockSpell("Shield");
                data.unlockSpell("Animate");
                data.unlockSpell("Telekinesis");
                data.unlockSpell("Destroy");
                data.unlockSpell("Heal");

                data.unlockSpell("Entity");
                data.unlockSpell("General");
                data.unlockSpell("Item");
                data.unlockSpell("Self");

            }

            if (context.getPlayer() != null) {
                context.getStack().decrement(1);
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
}