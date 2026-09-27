package net.nia.witchinghour.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.nia.witchinghour.Scheduler;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.magic.GestureHandler;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.ShieldHelper;
import net.nia.witchinghour.magic.spells.purplemagic.Telekinesis;
import net.nia.witchinghour.world.dimensions.ModDimensions;
import net.nia.witchinghour.world.dimensions.tomb.LaneRegistry;
import net.nia.witchinghour.world.dimensions.tomb.RitualGenerationManager;
import net.nia.witchinghour.world.dimensions.tomb.TrialRules;

public class EventsHandler {

    public static void update(MinecraftClient client) {
        // Client
        GestureHandler.update(client);
        ShieldHelper.update(client);
    }

    public static void update2(ServerPlayerEntity player) {
        // Server
        ManaRegeneration.update(player);
        ModDimensions.update(player);

        TimeChecker.update(player.getServer());
        FoolMoon.update(player);
        RitualGenerationManager.tick(player.getServerWorld());

    }


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                EventsHandler.update2(player);
                Telekinesis.tick(server);
                CursesUpdater.tick(player);
            }
            Scheduler.tick();
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player) {

                BlockPos deathPos = player.getBlockPos();
                String dimension = player.getWorld().getRegistryKey().getValue().toString();

                ModComponents.PLAYER_MAGIC.get(player).setPos("die", dimension, deathPos);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            Telekinesis.stop(player, false);
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {

            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            if (player.getWorld().getRegistryKey() != ModDimensions.TOMBS_LEVEL_KEY) {
                return ActionResult.PASS;
            }

            BlockState state = world.getBlockState(pos);
            TrialRules rules = LaneRegistry.getRules(serverPlayer.getUuid());

            return rules.canBreak(state.getBlock())
                    ? ActionResult.PASS
                    : ActionResult.FAIL;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            // Let the client pass so the server gets the packet
            if (world.isClient()) return ActionResult.PASS;

            // Dimension Check
            if (player.getWorld().getRegistryKey() != ModDimensions.TOMBS_LEVEL_KEY) {
                return ActionResult.PASS;
            }

            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            BlockState clickedState = world.getBlockState(hit.getBlockPos());
            TrialRules rules = LaneRegistry.getRules(player.getUuid());

            if (rules.canInteract(clickedState.getBlock()) || clickedState.hasBlockEntity()) {
                return ActionResult.PASS;
            }

            if (item == Items.FLINT_AND_STEEL) {
                BlockPos aboveClicked = hit.getBlockPos().offset(hit.getSide());

                if (world.getBlockState(hit.getBlockPos()).isOf(Blocks.TNT)) {
                    return ActionResult.PASS;
                }
                if (world.getBlockState(aboveClicked.down()).isOf(Blocks.TNT)) {
                    return ActionResult.PASS;
                }
            }

            // 3. BLOCK PLACEMENT LOGIC
            if (item instanceof BlockItem blockItem) {
                Block blockToPlace = blockItem.getBlock();

                Block supportBlock = clickedState.getBlock();

                if (blockToPlace == Blocks.WHITE_WOOL) {
                    return ActionResult.PASS;
                }

                if (!rules.canPlace(blockToPlace)) {
                    return ActionResult.FAIL;
                }

                if (blockToPlace == Blocks.BIG_DRIPLEAF) {
                    if (supportBlock == Blocks.BIG_DRIPLEAF || supportBlock == Blocks.PODZOL) {
                        return ActionResult.PASS;
                    }
                    return ActionResult.FAIL;
                }

                if (blockToPlace == Blocks.LEVER) {
                    if (supportBlock == Blocks.REDSTONE_BLOCK || supportBlock == Blocks.PODZOL) {
                        return ActionResult.PASS;
                    }
                    return ActionResult.FAIL;
                }

                if (blockToPlace == Blocks.OAK_BUTTON || blockToPlace == Blocks.SPRUCE_BUTTON) {
                    if (supportBlock == Blocks.REDSTONE_LAMP || supportBlock == Blocks.MOSS_BLOCK) {
                        return ActionResult.PASS;
                    }
                    return ActionResult.FAIL;
                }

                return ActionResult.PASS;
            }

            return ActionResult.FAIL;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {

            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (player.getWorld().getRegistryKey() != ModDimensions.TOMBS_LEVEL_KEY) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (world.isClient()) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();

            TrialRules rules = LaneRegistry.getRules(serverPlayer.getUuid());

            // -------------------------
            // SAFE ITEMS (always allowed)
            // -------------------------
            if (item.isFood()) {
                return TypedActionResult.pass(stack);
            }

            if (item instanceof PotionItem) {
                return TypedActionResult.pass(stack);
            }

            if (item instanceof SwordItem ||
                    item instanceof AxeItem ||
                    item instanceof BowItem ||
                    item instanceof ShieldItem ||
                    item instanceof FishingRodItem) {

                return TypedActionResult.pass(stack);
            }

            return TypedActionResult.pass(stack);
        });
    }

}
