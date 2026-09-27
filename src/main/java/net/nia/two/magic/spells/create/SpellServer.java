package net.nia.witchinghour.magic.spells.create;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.other.SpellBoltEntity;
import org.joml.Vector3f;

import java.util.Random;

public class SpellServer {

    public static void createSpell(ServerPlayerEntity player, String spell, int power, Vec3d direction) {

        SpellBoltEntity bolt = new SpellBoltEntity(
                WitchingHourEntities.SPELL_BOLT,
                player,
                player.getWorld()
        );


        Vec3d eyePos = player.getEyePos();

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        Vector3f color = data.getBoltColor("Bolt Color");

        if (color.x > 1) {
            Random r = new Random();
            Random g = new Random();
            Random b = new Random();

            Vector3f randomColor = new Vector3f(
                    r.nextFloat(),
                    g.nextFloat(),
                    b.nextFloat()
            );
            data.setBoltColor("Bolt Color", randomColor);

            color = data.getBoltColor("Bolt Color");
        }

        System.out.println("Bolt Color: "+color);

        // Assign BEFORE spawning
        bolt.setColor(color.x, color.y, color.z);
        bolt.spell = spell;

        bolt.tick();

        // How far in front of the player to spawn the bolt
        double forwardOffset = .25; // half a block, adjust as needed

        Vec3d spawnPos = eyePos.add(
                direction.x * forwardOffset,
                direction.y * forwardOffset,
                direction.z * forwardOffset
        );

        bolt.refreshPositionAndAngles(
                spawnPos.x, spawnPos.y, spawnPos.z,
                player.getYaw(),
                player.getPitch()
        );

        bolt.setVelocity(direction.x, direction.y, direction.z, 2.5f + power * 0.1f, 0);
        if (player.isSneaking()) {
            bolt.downwards = true;
        }
        player.getWorld().spawnEntity(bolt);
    }
}