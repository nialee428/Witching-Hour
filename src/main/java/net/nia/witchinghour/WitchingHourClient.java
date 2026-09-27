package net.nia.witchinghour;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.ActionResult;
import net.nia.voicetotext.DictionaryHolder;
import net.nia.voicetotext.VoskModelChecker;
import net.nia.witchinghour.data.DataDisplay;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.entities.golems.icegolem.IceGolemEntity;
import net.nia.witchinghour.entities.golems.icegolem.IceGolemRenderer;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.MushroomScreen;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.TinyMushroomEntity;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.TinyMushroomRenderer;
import net.nia.witchinghour.events.EventsHandler;
import net.nia.witchinghour.input.InputMain;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.GrimoireEntityRenderer;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;
import net.nia.witchinghour.magic.spells.other.SpellBoltRenderer;
import net.nia.witchinghour.magic.spells.other.SpellContainerRenderer;
import net.nia.witchinghour.magic.spells.other.SpellLoader;
import net.nia.witchinghour.particles.ModParticles;
import net.nia.witchinghour.screenhandlers.PlayerInventoryViewScreen;
import net.nia.witchinghour.screenhandlers.WitchingHourScreenHandlers;

public class WitchingHourClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        VoskModelChecker.performCheck();
        DictionaryHolder.initialize();

        ModParticles.register();
        SpellLoader.register();

        HandledScreens.register(
                WitchingHourScreenHandlers.PLAYER_INVENTORY_VIEW,
                PlayerInventoryViewScreen::new
        );

        HandledScreens.register(
                WitchingHourScreenHandlers.MUSHROOM_SCREEN_HANDLER,
                MushroomScreen::new
        );

        InputMain.initialize();

        HudRenderCallback.EVENT.register(new DataDisplay());
        ModModelLayers.register();


        EntityRendererRegistry.register(
                WitchingHourEntities.SPELL_BOLT,
                ctx -> new SpellBoltRenderer(ctx)
        );

        EntityRendererRegistry.register(
                WitchingHourEntities.SPELL_CONTAINER,
                ctx -> new SpellContainerRenderer(ctx)
        );

        EntityRendererRegistry.register(WitchingHourEntities.GRIMOIRE, GrimoireEntityRenderer::new);

        FabricDefaultAttributeRegistry.register(
                WitchingHourEntities.ICE_GOLEM,
                IceGolemEntity.createIronGolemAttributes()
        );

        EntityRendererRegistry.register(
                WitchingHourEntities.ICE_GOLEM,
                IceGolemRenderer::new
        );

        FabricDefaultAttributeRegistry.register(
                WitchingHourEntities.TINY_MUSHROOM,
                TinyMushroomEntity.createMobAttributes()
                        .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
        );

        EntityRendererRegistry.register(
                WitchingHourEntities.TINY_MUSHROOM,
                TinyMushroomRenderer::new
        );

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof GrimoireEntity book)) return ActionResult.PASS;

            // Only run on client

            PlayerMagicData pData = ModComponents.PLAYER_MAGIC.get(player);
            EntityMagicData data = ModComponents.ENTITY_MAGIC.get(book);

            if (!book.isTamed() && pData.getCovenName().isEmpty() && !pData.getPlayerName().isEmpty()) {
                book.setOwner(player);
                book.getWorld().sendEntityStatus(book, (byte) 7);

                return ActionResult.SUCCESS;
            }

            if (pData.getCovenName().isEmpty() && !data.get("Coven Name").isEmpty()) {
                data.addPendingInvite(pData.getPlayerName());
                return ActionResult.SUCCESS;
            }

            if (!world.isClient) return ActionResult.PASS;

            // Only react for the local player
            if (!player.equals(MinecraftClient.getInstance().player)) return ActionResult.PASS;

            if (!pData.getCovenName().equals(data.get("Coven Name"))) return ActionResult.PASS;

            // Page turning logic
            if (player.isSneaking()) {
                GrimoireClientData.get(book).previousPage();
            } else {
                GrimoireClientData.get(book).nextPage();
            }

            return ActionResult.SUCCESS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EventsHandler.update(client);
        });


    }


}
