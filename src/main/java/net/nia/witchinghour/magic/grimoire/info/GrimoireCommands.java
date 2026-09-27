package net.nia.witchinghour.magic.grimoire.info;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.WitchingHourNetworking;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.categories.CategoryNavigation;

import java.util.List;

public class GrimoireCommands {

    public static GrimoireEntity closestValidBook() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null) {
            return null;
        }

        // Player magic data
        PlayerMagicData pData = ModComponents.PLAYER_MAGIC.get(player);
        String playerCoven = pData.getCovenName();

        double radius = 12.0;

        // All grimoires in range
        List<GrimoireEntity> grimoires = client.world.getEntitiesByClass(
                GrimoireEntity.class,
                player.getBoundingBox().expand(radius),
                g -> true
        );

        GrimoireEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (GrimoireEntity g : grimoires) {

            boolean valid = false;

            // 1. Tamed by this player
            if (g.isTamed() && player.getUuid().equals(g.getOwnerUuid())) {
                valid = true;
            }

            // 2. Same coven
            if (!valid) {
                EntityMagicData gData = ModComponents.ENTITY_MAGIC.get(g);
                String grimoireCoven = gData.get("Coven Name");

                if (playerCoven != null && !playerCoven.isEmpty()
                        && playerCoven.equals(grimoireCoven)) {
                    valid = true;
                }
            }

            if (valid) {
                double dist = g.squaredDistanceTo(player);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = g;
                }
            }
        }

        return closest; // null if none found
    }

    public static void translateCommand(String command) {

        GrimoireEntity book = closestValidBook();
        command = command.toLowerCase();

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null || book == null) {
            return;
        }

        if (command.contains("show me ")) {
            command = command.split("show me ")[1];
            CategoryNavigation.reveal(command, book, player, false);
        } else if (command.contains("reveal ")) {
            command = command.split("reveal ")[1];
            CategoryNavigation.reveal(command, book, player, false);
        } else if (command.contains("display ")) {
            command = command.split("display ")[1];
            CategoryNavigation.reveal(command, book, player, false);
        } else if (command.contains("tell me about ")) {
            command = command.split("tell me about ")[1];
            CategoryNavigation.reveal(command, book, player, false);

        } else if (command.contains("craft ")) {
            command = command.split("craft ")[1];
            CategoryNavigation.reveal(command, book, player, true);
        } else if (command.contains("crafts ")) {
            command = command.split("crafts ")[1];
            CategoryNavigation.reveal(command, book, player, true);
        } else if (command.contains("crafted ")) {
            command = command.split("crafted ")[1];
            CategoryNavigation.reveal(command, book, player, true);

        } else if (command.contains("stay")) {
            WitchingHourNetworking.sendFollowTogglePacket(book.getId(), false);
        } else if (command.contains("follow")) {
            WitchingHourNetworking.sendFollowTogglePacket(book.getId(), true);
        }

    }

}
