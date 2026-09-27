package net.nia.witchinghour.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;

public class ManaRegeneration {

    public static void update(ServerPlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

        int mana = data.getMana();
        int level = data.getSpellLevel("General");
        int maxMana = 5 * level;

        // 1. Regen delay
        int delay = data.getRegenDelay();
        if (delay > 0) {
            data.setRegenDelay(delay - 1);
            return;
        }

        // 2. Full mana resets regen speed
        if (mana >= maxMana) {
            data.setRegenInterval(15*2); // much faster restart
            data.setRegenTimer(15*2);
            return;
        }

        // 3. Countdown to next regen tick
        int timer = data.getRegenTimer();
        if (timer > 0) {
            data.setRegenTimer(timer - 1);
            return;
        }

        // 4. Timer hit 0 → regenerate mana
        int interval = data.getRegenInterval();

        // Convert interval speed into bonus mana
        // Smaller interval = faster regen = more mana
        int manaBonus = Math.max(0, (15 - interval) / 4);
        manaBonus = Math.min(manaBonus, 4);
        int manaGain = 1 + manaBonus;

        // Apply mana gain
        mana = Math.min(maxMana, mana + manaGain);
        data.setMana(mana);

        // 5. Reduce interval (regen speeds up aggressively)
        interval = (int)(interval * 0.90);

        // Minimum interval = 2 ticks (very fast top speed)
        interval = Math.max(1, interval);

        data.setRegenInterval(interval);

        // 6. Reset timer to new interval
        data.setRegenTimer(interval);
    }
}