package net.nia.witchinghour.screenhandlers;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.nia.witchinghour.entities.plants.mushroom.tinymushroom.MushroomScreenHandler;

import java.util.UUID;

public class WitchingHourScreenHandlers {

    public static ScreenHandlerType<PlayerInventoryViewScreenHandler> PLAYER_INVENTORY_VIEW;
    public static ScreenHandlerType<MushroomScreenHandler> MUSHROOM_SCREEN_HANDLER;

    public static void register() {

        PLAYER_INVENTORY_VIEW = ScreenHandlerRegistry.registerExtended(
                new Identifier("witching-hour", "player_inventory_view"),
                (syncId, openerInventory, buf) -> {

                    UUID targetUuid = buf.readUuid();

                    PlayerEntity target =
                            openerInventory.player
                                    .getWorld()
                                    .getPlayerByUuid(targetUuid);

                    return new PlayerInventoryViewScreenHandler(
                            syncId,
                            openerInventory,
                            target.getInventory()
                    );
                }
        );

        MUSHROOM_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("witching-hour", "mushroom"),
                (syncId, playerInventory, buf) -> {

                    int mushroomId = buf.readInt();

                    return new MushroomScreenHandler(
                            syncId,
                            playerInventory,
                            mushroomId
                    );
                }
        );
    }
}